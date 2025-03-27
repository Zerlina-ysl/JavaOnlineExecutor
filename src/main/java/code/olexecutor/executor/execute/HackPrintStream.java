package code.olexecutor.executor.execute;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class HackPrintStream extends PrintStream {
      private static final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

      public HackPrintStream() {
            super(buffer);
      }

      public static String getBufferString() {
            return buffer.toString();
      }

      public static void clearBuffer() {
            buffer.reset();
      }
}