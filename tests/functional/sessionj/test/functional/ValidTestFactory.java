package sessionj.test.functional;

import org.objectweb.asm.*;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Arrays;

public class ValidTestFactory {
    private final File classesDir;
    private final ClassLoader loader;

    public ValidTestFactory() {
        classesDir = new File(System.getProperty(TestConstants.TEMP_BUILD_DIR));
        loader = new AddTestAnnotationClassLoader();
    }

    @Factory
    public Object[] createValidTests() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        File[] sjFiles = TestUtils.findSJSourceFiles("valid/");
        Object[] result = new Object[sjFiles.length];
        for (int i = 0; i < sjFiles.length; ++i) {
            assert TestUtils.runCompiler(sjFiles[i], classesDir, System.out, System.err) == 0;
            result[i] = loadAndAlterCompiledClass(sjFiles[i]).newInstance();
            System.out.println(result[i]);
            System.out.println(Arrays.toString(result[i].getClass().getMethod("run").getAnnotations()));
        }

        return result;
    }

    private Class<?> loadAndAlterCompiledClass(File sjFile) throws ClassNotFoundException {
        String className = sjFile.getName();
        className = className.substring(0, className.indexOf('.'));
        return loader.loadClass
                (TestConstants.TEST_BASE_PACKAGE + '.' + className);
    }

    private class AddTestAnnotationClassLoader extends ClassLoader {
        public Class<?> findClass(String className) throws ClassNotFoundException {
            try {
                byte[] bytecode = transformClass(className);
                return defineClass(className, bytecode, 0, bytecode.length);
            } catch (IOException ex) {
                throw new ClassNotFoundException("Load error: " + ex.toString(), ex);
            }
        }

        private byte[] transformClass(String className) throws IOException {
            InputStream is = null;
            try {
                is = new FileInputStream(new File(classesDir, className.replace('.','/') + ".class"));
                ClassReader reader = new ClassReader(is);
                ClassWriter writer = new ClassWriter(reader, 0);
                reader.accept(new AddTestAnnotationClassVisitor(writer), 0);
                return writer.toByteArray();
            } finally {
                if (is != null) is.close();
            }
        }
    }

    private static class AddTestAnnotationClassVisitor extends ClassAdapter {
        AddTestAnnotationClassVisitor(ClassVisitor writer) {
            super(writer);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (mv != null && "run".equals(name)) {
                return new AddTestAnnotationMethodVisitor(mv);
            }
            return mv;
        }

    }

    private static class AddTestAnnotationMethodVisitor extends MethodAdapter {
        private static final String TEST_ANNOTATION_DESC = Type.getDescriptor(Test.class);

        AddTestAnnotationMethodVisitor(MethodVisitor methodVisitor) {
            super(methodVisitor);
        }

        @Override
        public void visitCode() {
            // visitCode is the only method that can be called right after method annotations
            // have been visited, so it's the place to add a new one.
            // true: the new annotation will be visible at runtime.
            AnnotationVisitor av = mv.visitAnnotation(TEST_ANNOTATION_DESC, true);
            if (av != null) {
                av.visitEnd();
            }
            super.visitCode();
        }
    }
}
