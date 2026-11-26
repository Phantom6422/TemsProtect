package net.coreprotect.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class TabHandlerTest {

    @Test
    void suggestsFirstLevelCommandsForPlayer() {
        List<String> suggestions = new TabHandler().onTabComplete(fakePlayer(), null, "temsprotect", new String[] { "" });

        assertNotNull(suggestions);
        assertTrue(suggestions.contains("help"));
        assertTrue(suggestions.contains("lookup"));
        assertTrue(suggestions.contains("rollback"));
    }

    @Test
    void filtersFirstLevelCommandsByPrefix() {
        List<String> suggestions = new TabHandler().onTabComplete(fakePlayer(), null, "temsprotect", new String[] { "h" });

        assertNotNull(suggestions);
        assertTrue(suggestions.contains("help"));
        assertFalse(suggestions.contains("lookup"));
    }

    @Test
    void suggestsHelpTopics() {
        List<String> suggestions = new TabHandler().onTabComplete(fakePlayer(), null, "temsprotect", new String[] { "help", "" });

        assertNotNull(suggestions);
        assertTrue(suggestions.contains("rollback"));
        assertTrue(suggestions.contains("purge"));
    }

    private static Player fakePlayer() {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[] { Player.class }, (proxy, method, args) -> {
            switch (method.getName()) {
                case "hasPermission":
                case "isPermissionSet":
                    return true;
                case "getName":
                    return "Tester";
                case "getUniqueId":
                    return UUID.fromString("00000000-0000-0000-0000-000000000001");
                case "toString":
                    return "Tester";
                default:
                    break;
            }

            Class<?> returnType = method.getReturnType();
            if (!returnType.isPrimitive()) {
                return null;
            }
            if (returnType == boolean.class) {
                return false;
            }
            if (returnType == byte.class) {
                return (byte) 0;
            }
            if (returnType == short.class) {
                return (short) 0;
            }
            if (returnType == int.class) {
                return 0;
            }
            if (returnType == long.class) {
                return 0L;
            }
            if (returnType == float.class) {
                return 0.0F;
            }
            if (returnType == double.class) {
                return 0.0D;
            }
            if (returnType == char.class) {
                return '\0';
            }
            return null;
        });
    }
}
