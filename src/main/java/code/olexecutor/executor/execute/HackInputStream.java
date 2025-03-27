package code.olexecutor.executor.execute;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * 多线程缓解下为每个线程提供独立的输入流实例
 */
public class HackInputStream extends InputStream {

      // 管理输入流
      public final static ThreadLocal<InputStream> holdInputStream = new ThreadLocal<>();

      @Override
      public void close() throws IOException {
            holdInputStream.remove();
      }

      @Override
      public int read() throws IOException {
            // 无实际读取操作
            return 0;
      }

      public InputStream get() {
            return holdInputStream.get();
      }

      public void set(String systemIn) {
            holdInputStream.set(new ByteArrayInputStream(systemIn.getBytes()));
      }

}
