package code.olexecutor.execute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JavaClassExecutor {
    /**
     * 执行字节码
     *
     * @param classByte
     * @param systemIn
     * @return
     */
    public static String execute(byte[] classByte, String systemIn) {
        ClassModifier cm = new ClassModifier(classByte);
        // 对System和Scanner的调用被重定向到自定义的HackSystem和HackerScanner
        byte[] modifyBytes = cm.modifyUTF8Constant("java/lang/System",
                "code/olexecutor/execute/HackSystem");
        modifyBytes = cm.modifyUTF8Constant("java/util/Scanner", "code/olexecutor/execute/HackScanner");

        ((HackInputStream) HackSystem.in).set(systemIn);

        // 自定义类加载器
        HotSwapClassLoader classLoader = new HotSwapClassLoader();
        Class clazz = classLoader.loadByte(modifyBytes);

        try {
            // 反射获取main方法
            Method mainMethod = clazz.getMethod("main", new Class[]{String[].class});
            mainMethod.invoke(null, new String[]{null});

        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace(HackSystem.err);
        }
        String res = HackSystem.getBufferString();
        HackSystem.closeBuffer();
        return res;

    }

}
