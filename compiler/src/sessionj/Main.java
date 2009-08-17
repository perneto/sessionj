package sessionj;

import java.io.PrintStream;

/**
 * Main is the main program of the compiler extension.
 * It simply invokes Polyglot's main, passing in the extension's
 * ExtensionInfo.
 */
public class Main
{
    public static void main(String[] args) {
        int exitCode = start(args, System.out, System.err);
        System.exit(exitCode);
    }

    public static int start(String[] args, PrintStream out, PrintStream err) {
        polyglot.main.Main polyglotMain = new polyglot.main.Main();

        try {
            polyglotMain.start(args, new ExtensionInfo());
        }
        catch (polyglot.main.Main.TerminationException te) {
            if (te.getMessage() != null)
                (te.exitCode==0 ? out : err).println(te.getMessage());
            return te.exitCode;
        }
        return 0;
    }
}
