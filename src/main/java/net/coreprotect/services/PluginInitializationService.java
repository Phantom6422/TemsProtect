package net.coreprotect.services;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import net.coreprotect.CoreProtect;
import net.coreprotect.command.CommandHandler;
import net.coreprotect.command.PurgeCommand;
import net.coreprotect.command.TabHandler;
import net.coreprotect.config.ConfigHandler;
import net.coreprotect.consumer.Consumer;
import net.coreprotect.language.Language;
import net.coreprotect.language.Phrase;
import net.coreprotect.listener.ListenerHandler;
import net.coreprotect.listener.player.inspector.BaseInspector;
import net.coreprotect.thread.CacheHandler;
import net.coreprotect.thread.NetworkHandler;
import net.coreprotect.thread.Scheduler;
import net.coreprotect.thread.TickTimeMonitor;
import net.coreprotect.utility.Chat;
import net.coreprotect.utility.ChatUtils;
import net.coreprotect.utility.Extensions;
import net.coreprotect.utility.EntitySpawnTracking;
import net.coreprotect.utility.ErrorReporter;

/**
 * Service responsible for plugin initialization tasks
 */
public class PluginInitializationService {

    private PluginInitializationService() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Initializes plugin components and configurations
     *
     * @param plugin
     *            The TemsProtect plugin instance
     * @return true if initialization was successful, false otherwise
     */
    public static boolean initializePlugin(CoreProtect plugin) {
        // Load language phrases
        Language.loadPhrases();

        // Capture primary-thread server port and cached license before startup checks
        NetworkHandler.setServerPort(plugin.getServer().getPort());
        NetworkHandler.loadCachedLicense();

        // Perform version checks
        boolean start = VersionCheckService.performVersionChecks();
        if (!start) {
            return false;
        }

        try {
            // Initialize core components
            PurgeCommand.resetShutdownCancellation();
            Consumer.initialize();

            // Register commands
            registerCommands(plugin);

            // Ensure data directory exists
            createDataDirectory();

            // Initialize configuration
            start = ConfigHandler.performInitialization(true);
            if (start) {
                new ListenerHandler(plugin);
            }
        }
        catch (Exception e) {
            ErrorReporter.report(e);
            return false;
        }

        if (start) {
            // Display startup messages
            displayStartupMessages(plugin);

            // Start background services
            startBackgroundServices(plugin);
        }

        return start;
    }

    /**
     * Registers plugin commands
     *
     * @param plugin
     *            The TemsProtect plugin instance
     */
    private static void registerCommands(JavaPlugin plugin) {
        TabHandler tabHandler = new TabHandler();
        registerCommand(plugin, "temsprotect", tabHandler);
        registerCommand(plugin, "coreprotect", tabHandler);
        registerCommand(plugin, "core", tabHandler);
        registerCommand(plugin, "co", tabHandler);
    }

    private static void registerCommand(JavaPlugin plugin, String name, TabHandler tabHandler) {
        PluginCommand command = plugin.getCommand(name);
        if (command == null) {
            return;
        }
        command.setExecutor(CommandHandler.getInstance());
        command.setTabCompleter(tabHandler);
    }

    /**
     * Creates the plugin data directory if it doesn't exist
     */
    private static void createDataDirectory() {
        File dataDirectory = new File(ConfigHandler.path);
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
    }

    /**
     * Displays startup messages in the console
     *
     * @param plugin
     *            The TemsProtect plugin instance
     */
    private static void displayStartupMessages(JavaPlugin plugin) {
        PluginDescriptionFile pluginDescription = plugin.getDescription();
        ChatUtils.sendConsoleComponentStartup(Bukkit.getServer().getConsoleSender(), Phrase.build(Phrase.ENABLE_SUCCESS, ConfigHandler.EDITION_NAME));

        if (ConfigHandler.databaseType.isMySQL()) {
            Chat.console(Phrase.build(Phrase.USING_MYSQL));
        }
        else if (ConfigHandler.databaseType.isSQLite()) {
            Chat.console(Phrase.build(Phrase.USING_SQLITE));
        }
        else {
            Chat.console(Phrase.build(Phrase.USING_DATABASE, ConfigHandler.databaseType.getDisplayName()));
        }

        Chat.console("--------------------");
        Chat.console(Phrase.build(Phrase.ENJOY_COREPROTECT, pluginDescription.getName()));
        Chat.console(Phrase.build(Phrase.LINK_DISCORD, "https://github.com/Phantom6422/TemsProtect"));
        Chat.console("--------------------");
    }

    /**
     * Starts background services
     *
     * @param plugin
     *            The TemsProtect plugin instance
     */
    private static void startBackgroundServices(CoreProtect plugin) {
        // Start network handler
        Scheduler.scheduleSyncDelayedTask(plugin, () -> {
            try {
                NetworkHandler.startThread(new NetworkHandler(true, true));
            }
            catch (Exception e) {
                ErrorReporter.report(e);
            }
        }, 0);

        // Start tick time monitor (only used where native tick timings are unavailable)
        TickTimeMonitor.initialize(plugin);

        // Start cache cleanup thread
        CacheHandler.startThread();

        // Start inspector lookup threads
        BaseInspector.startLookups();

        Consumer.startConsumer();
        EntitySpawnTracking.initializeLoadedEntities();
        Extensions.startBackgroundService();
    }
}
