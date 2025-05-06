/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package code.olexecutor.execute;

class ClassModifier {
      // 常量池起始偏移
      private static final int CONSTAIN_POOL_COUNT_INDEX = 8;
      // 常量tag
      private static final int CONSTAIN_UTF8_INFO = 1;
      // 待修改的字节码文件
      public byte[] classByte;
      private static final int[] CONSTANT_ITEM_LENGTH = {
            -1,  // [0] 未使用
            -1,  // [1] CONSTANT_Utf8: 变长，需特殊处理
            -1,  // [2] 未使用
            5,   // [3] CONSTANT_Integer: 1(tag) + 4(int值) = 5字节
            5,   // [4] CONSTANT_Float: 1(tag) + 4(float值) = 5字节
            9,   // [5] CONSTANT_Long: 1(tag) + 8(long值) = 9字节
            9,   // [6] CONSTANT_Double: 1(tag) + 8(double值) = 9字节
            3,   // [7] CONSTANT_Class: 1(tag) + 2(指向UTF8的索引) = 3字节
            3,   // [8] CONSTANT_String: 1(tag) + 2(指向UTF8的索引) = 3字节
            5,   // [9] CONSTANT_Fieldref: 1(tag) + 2(类索引) + 2(名称类型索引) = 5字节
            5,   // [10] CONSTANT_Methodref: 1(tag) + 2(类索引) + 2(名称类型索引) = 5字节
            5,   // [11] CONSTANT_InterfaceMethodref: 1(tag) + 2(类索引) + 2(名称类型索引) = 5字节
            5    // [12] CONSTANT_NameAndType: 1(tag) + 2(名称索引) + 2(描述符索引) = 5字节
        };


      public ClassModifier(byte[] classBytes) {
            this.classByte = classBytes;
      }

      /**
       * 修改Class文件常量池中UTF8字符串常量
       * @param oldStr
       * @param newStr
       * @return
       */
      public byte[] modifyUTF8Constant(String oldStr, String newStr) {
            // 获取常量数量
            int constantPoolCount = getConstantPoolCount();
            int offset = CONSTAIN_POOL_COUNT_INDEX + u2;
            for (int i = 1; i < constantPoolCount; i++) {
                  // CONSTANT_Utf8_info {
                  //       u1 tag;         // 值为1
                  //       u2 length;      // UTF8字符串的长度
                  //       u1 bytes[length]; // 字符串内容
                  //   }
                  int tag = ByteUtils.byte2Int(classByte, offset, u1);
                  if (tag == CONSTAIN_UTF8_INFO) {
                        // UTF8类
                        int len = ByteUtils.byte2Int(classByte, offset + u1, u2);
                        offset += u1 + u2;
                        String str = ByteUtils.byte2String(classByte, offset, len);
                        if (str.equals(oldStr)) {
                              byte[] strReplaceBytes = ByteUtils.string2Byte(newStr);
                              byte[] intReplaceBytes = ByteUtils.int2Byte(strReplaceBytes.length, u2);
                              classByte = ByteUtils.byteReplace(classByte, offset - u2, u2, intReplaceBytes);
                              classByte = ByteUtils.byteReplace(classByte, offset, len, strReplaceBytes);
                              return classByte;
                        } else {
                              // 移动到下一个常量
                              offset += len;
                        }
                  } else {
                        offset += CONSTANT_ITEM_LENGTH[tag];
                  }
            }
            return classByte;
      }

      /*
       * 在classbyte数组中取tag和len
       */
      private static final int u1 = 1;
      private static final int u2 = 2;

      /*
       * @return 常量池中常量个数
       */
      public int getConstantPoolCount() {
            return ByteUtils.byte2Int(classByte, CONSTAIN_POOL_COUNT_INDEX, u2);
      }

}
