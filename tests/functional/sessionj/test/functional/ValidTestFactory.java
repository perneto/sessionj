package sessionj.test.functional;

import org.testng.annotations.Factory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

public class ValidTestFactory {
    private final File classesDir;
    private final ClassLoader loader;

    public ValidTestFactory() throws MalformedURLException {
        classesDir = new File(System.getProperty(TestConstants.TEMP_BUILD_DIR));
        loader = new URLClassLoader(new URL[] {classesDir.toURI().toURL()});
    }

    @Factory
    public Object[] createValidTests() throws ClassNotFoundException {
        File[] sjFiles = TestUtils.findSJSourceFiles("valid/");
        Object[] result = new Object[sjFiles.length];
        for (int i=0; i<sjFiles.length; ++i) {
            assert TestUtils.runCompiler(sjFiles[i], classesDir, System.out, System.err) == 0;
            result[i] = loadCompiledClass(sjFiles[i]);
        }

        return result;
    }

    private Object loadCompiledClass(File sjFile) throws ClassNotFoundException {
        String className = sjFile.getName();
        className = className.substring(0, className.indexOf('.'));
        return loader.loadClass(TestConstants.TEST_BASE_PACKAGE + '.' + className);
    }
}
