package sessionj.test.functional;

import sessionj.Main;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 */
public class TestUtils {
    protected static Collection<File> findSJSourceFiles(String dir) {
        File compilationErrorDir = new File(TestConstants.TEST_DIR + dir);
        File[] files = compilationErrorDir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                return s.endsWith(".sj");
            }
        });
        Collection<File> filtered = new LinkedList<File>();
        for (File f : files) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(f));
                String first = reader.readLine();
                if (!first.startsWith("//DISABLED")) filtered.add(f);
            } catch (IOException e) {
                // ignore it
            } finally {
                if (reader != null) try {
                    reader.close();
                } catch (IOException e) {
                    // swallow
                }
            }
        }
        return filtered;
    }

    static int runCompiler(File sjFile, File outputDir, PrintStream out, PrintStream err) {
        System.out.println(System.getProperty("java.class.path"));
        return Main.start(
                new String[] {
                    sjFile.getAbsolutePath(),
                    "-d", outputDir.getAbsolutePath(),
                    "-cp", System.getProperty("java.class.path")
                },
                out, err);
    }

    public static File createTempDirectory() throws IOException {
        File tempDir = File.createTempFile("sessionj-test", null);
        boolean ok1 = tempDir.delete();
        boolean ok2 = tempDir.mkdir();
        if (!(ok1 && ok2))
            throw new IOException("Could not create temp directory: " + tempDir);
        return tempDir;
    }
}
