package net.coreprotect.utility;

/**
 * Local error reporter.
 *
 * <p>TemsProtect deliberately does not transmit crash reports or stack traces to
 * a third party. Errors are printed to the server console (and therefore the
 * server's own log files), which keeps diagnostic data on the server owner's
 * infrastructure and avoids leaking plugin, player or server details.</p>
 */
public final class ErrorReporter {

    private ErrorReporter() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean report(Throwable throwable) {
        return report(throwable, true);
    }

    public static boolean report(Throwable throwable, boolean printStackTrace) {
        if (throwable == null) {
            return false;
        }

        if (printStackTrace) {
            throwable.printStackTrace();
        }
        else {
            String message = throwable.getMessage();
            System.err.println("[TemsProtect] " + throwable.getClass().getName() + (message == null ? "" : ": " + message));
        }

        return true;
    }
}
