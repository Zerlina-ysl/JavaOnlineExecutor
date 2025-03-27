package code.olexecutor.executor.execute;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public final class HackSystem {

      private HackSystem() {

      }

      public final static InputStream input = new HackInputStream();
      public final static PrintStream output = new HackPrintStream();
      public final static PrintStream err = output;

      /*
       * 关闭输入输出流
       */
      public static void closeBuffer() {
            try {
                  ((HackInputStream) input).close();
                  output.close();
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      /*
       * 获取线程输出流内容
       */
      public static String getBufferString() {
            return output.toString();
      }

}
