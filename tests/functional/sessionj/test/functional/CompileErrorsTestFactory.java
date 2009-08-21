package sessionj.test.functional;

import org.testng.annotations.Factory;

import java.io.File;

public class CompileErrorsTestFactory {
    @Factory
    public Object[] createInstances() {
        File[] sjFiles = TestUtils.findSJSourceFiles("compilationerror/");
        Object[] result = new CompilationError[sjFiles.length];
        for (int i=0; i<sjFiles.length; ++i) result[i] = new CompilationError(sjFiles[i]);
        return result;
    }
}
