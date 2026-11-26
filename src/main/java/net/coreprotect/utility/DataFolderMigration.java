package net.coreprotect.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * One-time migration from the old TemsProtect data directory.
 *
 * <p>Renaming the plugin changes its data folder to {@code plugins/TemsProtect}.
 * To avoid silently starting with a fresh (empty) database, an existing
 * {@code plugins/TemsProtect} folder is moved into place on first start. The
 * move is attempted first; if that is not possible, a bounded copy is used.</p>
 */
public final class DataFolderMigration {

    /** Refuse to copy more than this much data; renaming a huge database is still attempted. */
    private static final long MAX_COPY_BYTES = 2L * 1024L * 1024L * 1024L;

    private DataFolderMigration() {
        throw new IllegalStateException("Utility class");
    }

    public static void migrateIfNeeded(JavaPlugin plugin) {
        File target = plugin.getDataFolder();
        File parent = target.getParentFile();
        if (parent == null) {
            return;
        }

        boolean targetEmpty = !target.exists() || (target.isDirectory() && target.list() != null && target.list().length == 0);
        if (!targetEmpty) {
            return;
        }

        File source = new File(parent, "CoreProtect");
        if (!source.isDirectory()) {
            return;
        }

        Path sourcePath = source.toPath();
        Path targetPath = target.toPath();
        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
            plugin.getLogger().info("Migrated existing CoreProtect data folder to " + target.getName() + ".");
            return;
        }
        catch (IOException | UnsupportedOperationException moveFailure) {
            plugin.getLogger().info("Could not rename the CoreProtect data folder; attempting a safe copy.");
        }

        try {
            long size = directorySize(sourcePath);
            if (size > MAX_COPY_BYTES) {
                plugin.getLogger().warning("Existing CoreProtect data folder is larger than 2 GiB; "
                        + "move it to " + target.getPath() + " manually before starting the server.");
                return;
            }

            copyTree(sourcePath, targetPath);
            plugin.getLogger().info("Copied existing CoreProtect data folder to " + target.getName()
                    + ". The original folder was left in place.");
        }
        catch (IOException copyFailure) {
            plugin.getLogger().warning("Could not migrate the CoreProtect data folder: " + copyFailure.getMessage()
                    + ". TemsProtect will start with a new data folder at " + target.getPath() + ".");
        }
    }

    private static long directorySize(Path root) throws IOException {
        AtomicLong size = new AtomicLong();
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isSymbolicLink() || !attrs.isRegularFile()) {
                    throw new IOException("Refusing to copy non-regular file: " + file.getFileName());
                }
                if (size.addAndGet(attrs.size()) > MAX_COPY_BYTES) {
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return size.get();
    }

    private static void copyTree(Path source, Path target) throws IOException {
        Files.createDirectories(target);
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
                Path destination = target.resolve(source.relativize(directory).toString());
                if (!destination.equals(target)) {
                    Files.createDirectories(destination);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isSymbolicLink() || !attrs.isRegularFile()) {
                    throw new IOException("Refusing to copy non-regular file: " + file.getFileName());
                }
                Path destination = target.resolve(source.relativize(file).toString());
                Files.copy(file, destination, StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
