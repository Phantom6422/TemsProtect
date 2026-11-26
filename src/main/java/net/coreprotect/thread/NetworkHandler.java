package net.coreprotect.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.coreprotect.config.Config;
import net.coreprotect.language.Phrase;
import net.coreprotect.utility.Chat;
import net.coreprotect.utility.ErrorReporter;
import net.coreprotect.utility.VersionUtils;

/**
 * Bounded GitHub release checker.
 *
 * <p>Older builds of this plugin phoned home to TemsProtect endpoints for
 * licence validation, language translation, statistics and update checks. The
 * TemsProtect build only performs an optional, read-only update check against
 * the configured GitHub repository. No identifiers, licence keys, player data
 * or statistics are sent anywhere.</p>
 */
public class NetworkHandler implements Runnable {

    private static final int HTTP_TIMEOUT_MILLIS = 5000;
    private static final Pattern REPOSITORY_PATTERN = Pattern.compile("^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$");
    private static final Pattern TAG_PATTERN = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+(?:\\.\\d+)*$");

    private static final Map<Thread, NetworkHandler> ACTIVE_HANDLERS = new ConcurrentHashMap<>();
    private static final Object LOCK = new Object();
    private static volatile String latestVersion = null;
    private static volatile boolean updateNoticeShown = false;

    private final boolean startup;
    private final boolean background;
    private volatile boolean cancelled = false;

    public NetworkHandler(boolean startup, boolean background) {
        this.startup = startup;
        this.background = background;
    }

    public static Thread startThread(NetworkHandler handler) {
        if (handler == null) {
            return null;
        }
        Thread thread = new Thread(() -> {
            try {
                handler.run();
            }
            finally {
                synchronized (LOCK) {
                    ACTIVE_HANDLERS.remove(Thread.currentThread());
                }
            }
        }, "TemsProtect-Network");
        thread.setDaemon(true);
        synchronized (LOCK) {
            if (handler.cancelled) {
                return null;
            }
            ACTIVE_HANDLERS.put(thread, handler);
            thread.start();
        }
        return thread;
    }

    public static void stopThreads(long timeoutMillis) {
        List<Map.Entry<Thread, NetworkHandler>> snapshot;
        synchronized (LOCK) {
            if (ACTIVE_HANDLERS.isEmpty()) {
                return;
            }
            snapshot = new ArrayList<>(ACTIVE_HANDLERS.entrySet());
        }

        for (Map.Entry<Thread, NetworkHandler> entry : snapshot) {
            entry.getValue().cancelled = true;
        }

        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(Math.max(0L, timeoutMillis));
        boolean interrupted = Thread.interrupted();
        try {
            for (Map.Entry<Thread, NetworkHandler> entry : snapshot) {
                Thread thread = entry.getKey();
                if (!thread.isAlive()) {
                    continue;
                }
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0L) {
                    thread.interrupt();
                    continue;
                }
                try {
                    thread.join(Math.max(1L, TimeUnit.NANOSECONDS.toMillis(remaining)));
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    thread.interrupt();
                }
            }
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static String latestVersion() {
        return latestVersion;
    }

    /**
     * Retained for the status command. TemsProtect has no separate edge channel.
     */
    public static String latestEdgeVersion() {
        return null;
    }

    /**
     * Retained for API compatibility. TemsProtect has no donation key.
     */
    public static String donationKey() {
        return null;
    }

    public static void setServerPort(int port) {
        // No statistics are sent, so the port is intentionally unused.
    }

    public static void loadCachedLicense() {
        // No licence state exists in TemsProtect.
    }

    private boolean isAborted() {
        return cancelled || Thread.currentThread().isInterrupted();
    }

    private boolean sleepInterruptibly(long millis) {
        if (isAborted()) {
            return false;
        }
        try {
            Thread.sleep(millis);
            return !isAborted();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void run() {
        try {
            if (isAborted()) {
                return;
            }

            do {
                if (!Config.getGlobal().CHECK_UPDATES) {
                    latestVersion = null;
                    return;
                }
                checkForUpdate();
                if (!background) {
                    return;
                }
                if (!sleepInterruptibly(TimeUnit.HOURS.toMillis(1))) {
                    return;
                }
            }
            while (!isAborted());
        }
        catch (Exception e) {
            if (!isAborted()) {
                ErrorReporter.report(e, false);
            }
        }
    }

    private void checkForUpdate() {
        String repository = Config.getGlobal().UPDATE_REPOSITORY;
        if (repository == null || !REPOSITORY_PATTERN.matcher(repository).matches()) {
            return;
        }

        HttpURLConnection connection = null;
        try {
            URL url = URI.create("https://api.github.com/repos/" + repository + "/releases/latest").toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("User-Agent", "TemsProtect/" + VersionUtils.getPluginVersion());
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setConnectTimeout(HTTP_TIMEOUT_MILLIS);
            connection.setReadTimeout(HTTP_TIMEOUT_MILLIS);

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                // A private repository intentionally returns 404 to an unauthenticated
                // server. Treat that as "no update available" and stay quiet.
                return;
            }

            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                    if (body.length() > 65_536) {
                        return;
                    }
                }
            }

            Matcher matcher = TAG_PATTERN.matcher(body.toString());
            if (!matcher.find()) {
                return;
            }

            String remoteVersion = matcher.group(1).trim();
            if (remoteVersion.startsWith("v") || remoteVersion.startsWith("V")) {
                remoteVersion = remoteVersion.substring(1);
            }
            if (!VERSION_PATTERN.matcher(remoteVersion).matches()) {
                return;
            }

            if (!VersionUtils.newVersion(VersionUtils.getPluginVersion(), remoteVersion)) {
                latestVersion = null;
                return;
            }

            latestVersion = remoteVersion;
            if (startup && !updateNoticeShown && !isAborted()) {
                updateNoticeShown = true;
                Chat.console("--------------------");
                Chat.console(Phrase.build(Phrase.VERSION_NOTICE, remoteVersion));
                Chat.console(Phrase.build(Phrase.LINK_DOWNLOAD, "https://github.com/" + repository + "/releases/latest"));
                Chat.console("--------------------");
            }
        }
        catch (Exception e) {
            // Update checks must never affect server startup or gameplay.
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
