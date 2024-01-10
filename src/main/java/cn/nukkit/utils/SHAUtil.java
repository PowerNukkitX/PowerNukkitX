package cn.nukkit.utils;

import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class SHAUtil {
    public String SHA256(final String strText) {
        return SHA(strText, "SHA-256");
    }

    public String SHA512(final String strText) {
        return SHA(strText, "SHA-512");
    }

    public String MD5(String strText) {
        return SHA(strText, "MD5");
    }

    private String SHA(final String strText, final String strType) {
        // 返回值
        String strResult = null;

        // 是否是有效字符串
        if (strText != null && !strText.isEmpty()) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                messageDigest.update(strText.getBytes());
                byte[] byteBuffer = messageDigest.digest();

                StringBuilder strHexString = new StringBuilder();
                for (byte b : byteBuffer) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }
}
