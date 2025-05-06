package code.olexecutor.execute;

public class ByteUtils {

      /*
       * 将字符数组a的[start,start+end)转换为整数
       * byte有符号，[-128,127],直接转int会保留符号位,负数被扩展为全1
       * & 0xff 只取最后8位
       */
      public static int byte2Int(byte[] a, int start, int len) {
            int res = 0;
            for (int i = start; i < start + len; i++) {
                  int cur = ((int) a[i]) & 0xff;
                  cur <<= (--len) * 8;
                  res += cur;
            }
            return res;
      }

      /*
       * 将字节数组的[start,start+len)转换为字符串
       */
      public static String byte2String(byte[] a, int start, int len) {
            return new String(a, start, len);
      }

      public static byte[] string2Byte(String str) {
            return str.getBytes();
      }

      /*
       * 将整数转换为指定长度的字节数组
       */
      public static byte[] int2Byte(int num, int len) {
            byte[] b = new byte[len];
            for (int i = 0; i < len; i++) {
                  b[len - i - 1] = (byte) ((num >> (8 * i) & 0xff));
            }
            return b;
      }

      /**
       * 
       * @param oldBytes:原始字节数组
       * @param offset: 替换的起始位置
       * @param len: 替换长度
       * @param replaceBytes: 替换后的新字节数组
       * @return
       */
      public static byte[] byteReplace(byte[] oldBytes, int offset, int len, byte[] replaceBytes) {
            byte[] newBytes = new byte[oldBytes.length+replaceBytes.length-len];
            // 复制原数组前半部分
            System.arraycopy(oldBytes, 0, newBytes, 0 , offset);
            // 复制替换数组
            System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
            // 复制原数组后半部分
            System.arraycopy(oldBytes, offset+len, newBytes, offset+replaceBytes.length, oldBytes.length-offset-len);
            return newBytes;
      }

}
