package com.novv.dzdesk.secret;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesCbcWithIntegrity {

  public static String sKey = "dAXahiGJXKkkP4Yr";
  public static String ivParameter = "dAXahiGJXKkkP4Yr";
  private static AesCbcWithIntegrity instance = null;
  private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
  //private static
  private AesCbcWithIntegrity() {

  }

  public static AesCbcWithIntegrity getInstance() {
    if (instance == null)
      instance = new AesCbcWithIntegrity();
    return instance;
  }

  // 加密
  public String encrypt(String sSrc, String encodingFormat, String sKey, String ivParameter)
      throws Exception {
    Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
    byte[] raw = sKey.getBytes();
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
    byte[] encrypted = cipher.doFinal(sSrc.getBytes(encodingFormat));
    return Base64.encodeToString(encrypted, Base64.NO_WRAP);//此处使用BASE64做转码。
  }

  // 解密
  public String decrypt(String sSrc, String encodingFormat, String sKey, String ivParameter)
      throws Exception {
    try {
      byte[] raw = sKey.getBytes("ASCII");
      SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
      Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
      IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
      byte[] encrypted1 = Base64.decode(sSrc, Base64.NO_WRAP);//先用base64解密
      byte[] original = cipher.doFinal(encrypted1);
      String originalString = new String(original, encodingFormat);
      return originalString;
    } catch (Exception ex) {
      return null;
    }
  }
}
