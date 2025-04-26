package code.olexecutor.executor.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.*;

public class StringSourceCompiler {
    // 存储编译后的对象
    private static Map<String, JavaFileObject> fileObjectMap = new ConcurrentHashMap<>();

    // 匹配class名称
    private static Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");

    public static byte[] compile(String source, DiagnosticCollector<JavaFileObject> compileCollector) {
        // 获取系统Java编译器实例
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // 自定义文件管理器
        JavaFileManager javaFileManager = new TmpJavaFileManager(
                compiler.getStandardFileManager(compileCollector, null, null));

        Matcher matcher = CLASS_PATTERN.matcher(source);

        String className;
        // 匹配类名
        if (matcher.find()) {
            className = matcher.group(1);
        } else {
            throw new IllegalArgumentException("no valid class");
        }

        JavaFileObject sourceJavaFileObject = new TmpJavaFileObject(className, source);

        // 执行编译任务
        Boolean result = compiler.getTask(null, javaFileManager, compileCollector, null, null, Arrays.asList(sourceJavaFileObject)).call();
        // 编译后的文件对象
        JavaFileObject bytesJavaFileObject = fileObjectMap.get(className);
        if (result && bytesJavaFileObject != null) {
            // 返回编译后的字节码
            return ((TmpJavaFileObject) bytesJavaFileObject).getCompiledBytes();
        }
        return null;
    }

    /*
     * 管理java文件
     */
    public static class TmpJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

        protected TmpJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
            JavaFileObject javaFileObject = fileObjectMap.get(className);
            if (javaFileObject == null) {
                return super.getJavaFileForInput(location, className, kind);
            }
            return javaFileObject;
        }


        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            JavaFileObject javaFileObject = new TmpJavaFileObject(kind, className);
            fileObjectMap.put(className, javaFileObject);
            return javaFileObject;
        }
    }

    /**
     * 源码和字节码文件
     */
    public static class TmpJavaFileObject extends SimpleJavaFileObject {
        private String source;
        private ByteArrayOutputStream outputStream;

        public TmpJavaFileObject(String name, String source) {
            super(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        public TmpJavaFileObject(Kind kind, String name) {
            super(URI.create("String:///" + name + Kind.SOURCE.extension), kind);
            this.source = null;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            if (source == null) {
                throw new IllegalStateException("source is null");
            }
            return source;
        }

        public byte[] getCompiledBytes() {
            return outputStream.toByteArray();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            outputStream = new ByteArrayOutputStream();
            return outputStream;
        }
    }
}
