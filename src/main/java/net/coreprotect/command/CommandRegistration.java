package net.coreprotect.command;

import java.util.Collection;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import net.coreprotect.CoreProtect;
import net.coreprotect.language.Phrase;
import net.coreprotect.utility.Chat;
import net.coreprotect.utility.Color;

/**
 * Registers the TemsProtect commands through Paper's Brigadier lifecycle API.
 *
 * <p>Paper plugins ignore the {@code commands} section of plugin metadata, so
 * the commands are registered as {@link BasicCommand}s. Each command keeps the
 * existing Bukkit-style argument parser, which preserves the CoreProtect
 * command syntax, permissions and tab completion.</p>
 */
public final class CommandRegistration {

    private CommandRegistration() {
        throw new IllegalStateException("Utility class");
    }

    public static void register(CoreProtect plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("temsprotect", "TemsProtect main command", List.of("tems"),
                    new Handler("temsprotect", "temsprotect.co"));
            event.registrar().register("co", "TemsProtect command alias", List.of(),
                    new Handler("co", "temsprotect.co"));
            event.registrar().register("core", "TemsProtect legacy command alias", List.of(),
                    new Handler("core", "coreprotect.core"));
            event.registrar().register("coreprotect", "TemsProtect legacy command alias", List.of(),
                    new Handler("coreprotect", "coreprotect.coreprotect"));
        });
    }

    private static final class Handler implements BasicCommand {

        private final String name;
        private final String permission;
        private final Command delegate;
        private final TabHandler tabHandler = new TabHandler();

        private Handler(String name, String permission) {
            this.name = name;
            this.permission = permission;
            this.delegate = new DelegateCommand(name, permission);
        }

        @Override
        public void execute(CommandSourceStack source, String[] args) {
            CommandSender sender = source.getSender();
            if (!sender.hasPermission(permission)) {
                Chat.sendMessage(sender, Color.DARK_AQUA + "TemsProtect " + Color.WHITE + "- " + Phrase.build(Phrase.NO_PERMISSION));
                return;
            }
            CommandHandler.getInstance().onCommand(sender, delegate, name, args);
        }

        @Override
        public Collection<String> suggest(CommandSourceStack source, String[] args) {
            String[] suggestionArgs = args.length == 0 ? new String[] { "" } : args;
            Collection<String> suggestions = tabHandler.onTabComplete(source.getSender(), delegate, name, suggestionArgs);
            return suggestions == null ? List.of() : suggestions;
        }

        @Override
        public boolean canUse(CommandSender sender) {
            return sender.hasPermission(permission);
        }

        @Override
        public String permission() {
            return permission;
        }
    }

    /**
     * Minimal Bukkit Command used by the existing command handlers for their
     * name, permission and usage output. It is never registered with Bukkit's
     * command map.
     */
    private static final class DelegateCommand extends Command {

        private DelegateCommand(String name, String permission) {
            super(name);
            setPermission(permission);
            setDescription("TemsProtect command");
            setUsage("/" + name + " <params>");
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
            return List.of();
        }
    }
}
