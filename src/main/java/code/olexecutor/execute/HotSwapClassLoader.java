/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package code.olexecutor.execute;

/*
 * 自定义类加载器
 */
public class HotSwapClassLoader extends ClassLoader {

      public HotSwapClassLoader() {
            super(HotSwapClassLoader.class.getClassLoader());
      }
      /**
       * 将字节数组定义为一个类
       */
      public Class loadByte(byte[] classBytes) {
            return defineClass(null, classBytes, 0, classBytes.length);
      }
}
