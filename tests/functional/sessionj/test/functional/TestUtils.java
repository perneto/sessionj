package sessionj.test.functional;

import sessionj.Main;

import java.io.*;

/**
 *
 */
public class TestUtils {
    protected static File[] findSJSourceFiles(String dir) {
        File compilationErrorDir = new File(TestConstants.TEST_DIR + dir);
        return compilationErrorDir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                return s.endsWith(".sj");
            }
        });
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
