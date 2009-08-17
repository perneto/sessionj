package sessionj;

/**
 * Main is the main program of the compiler extension.
 * It simply invokes Polyglot's main, passing in the extension's
 * ExtensionInfo.
 */
public class Main
{
  public static void main(String[] args) {
      polyglot.main.Main polyglotMain = new polyglot.main.Main();

      try {
          polyglotMain.start(args, new ExtensionInfo());
      }
      catch (polyglot.main.Main.TerminationException te) {
          if (te.getMessage() != null)
              (te.exitCode==0 ? System.out : System.err).println(te.getMessage());
          System.exit(te.exitCode);
      }
  }
}
