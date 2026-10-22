package com.rookie.rookiemeeting.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

public class BASE64 {
    public static String encodeImgageToBase64(File imageFile) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        // 其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imageFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码（每 76 个字符换一行，和原来的 sun.misc.BASE64Encoder 输出一致）
        // 返回Base64编码过的字节数组字符串
        return Base64.getMimeEncoder(76, new byte[]{'\n'}).encodeToString(data);
    }
}
