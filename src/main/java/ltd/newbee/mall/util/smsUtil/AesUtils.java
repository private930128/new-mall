package ltd.newbee.mall.util.smsUtil;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtils {

    private static final String IV_INIT = "0102030405060708";

    // 对称加密算法，java默认只支持128bytes。
    /**
     * 加密
     *  aes(src.getBytes,key)
     * @param /encryptStr
     * @return
     */
    public static byte[] encryptDefault(String src, String key) throws Exception {
        // 返回实现指定算法的密码对象实例
        Cipher cipher = Cipher.getInstance("AES");
        // 根据key产生指定算法的秘钥
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(),"AES");
        //设置密钥和加密形式
        cipher.init(Cipher.ENCRYPT_MODE, securekey);
        // 依据init,执行具体操作
        byte[] doFinal = cipher.doFinal(src.getBytes());
        return doFinal;
    }
    /**
     * 加密
     *  base64(aes(src,key))
     * @param /encryptStr
     * @return
     */
    public static String encryptBase64Default(String src, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(),"AES");
        cipher.init(Cipher.ENCRYPT_MODE, securekey);//设置密钥和加密形式
        byte[] doFinal = cipher.doFinal(src.getBytes());
        String encode = Base64Util.encode(doFinal);
        return encode;
    }

    /**
     * 解密
     *  aes(src.getBytes,key)
     * @param /decryptStr
     * @return
     * @throws Exception
     */
    public static byte[] decryptDefault(String src, String key)  throws Exception  {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), "AES");//设置加密Key
        cipher.init(Cipher.DECRYPT_MODE, securekey);//设置密钥和解密形式
        return cipher.doFinal(src.getBytes());
    }

    /**
     * 解密
     *  decryptBase64--->decryptAes
     * @param /decryptStr
     * @return
     * @throws Exception
     */
    public static String decryptBase64Default(String src, String key)  throws Exception  {
        byte[] decode = Base64Util.decode(src);
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), "AES");//设置加密Key
        cipher.init(Cipher.DECRYPT_MODE, securekey);//设置密钥和解密形式
        byte[] doFinal = cipher.doFinal(decode);

        return new String(doFinal);
    }

    /**
     * [加密]<BR>
     *aes(src,key)
     */
    public static byte[] encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            return null;
        }
        // 返回实现指定算法的密码对象实例
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 根据key产生指定算法的秘钥
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(IV_INIT.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());
        return encrypted;
    }
    /**
     * [加密]<BR>
     *base64(aes(src,key))
     */
    public static String encryptBase64(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            return null;
        }
        // 返回实现指定算法的密码对象实例
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 根据key产生指定算法的秘钥
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//	        SecretKey generateSKey = generateSKey(sKey);
        IvParameterSpec iv = new IvParameterSpec(IV_INIT.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));

        //此处使用BASE64做转码功能，同时能起到2次加密的作用。
        return Base64Util.encode(encrypted);
    }

    /**
     * [解密]<BR>
     *decryptAes(src,key)
     * @throws Exception 异常抛出
     */
    public static byte[] decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //每次都创建新的iv
            IvParameterSpec iv = new IvParameterSpec(IV_INIT.getBytes());
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(sSrc.getBytes());
            return original;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    /**
     * [解密]<BR>
     *decryptBase64--->decryptAes
     * @param sSrc 源字符串
     * @param sKey 解密key
     * @return 解密后字符串
     * @throws Exception 异常抛出
     */
    public static String decryptBase64(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                return null;
            }
            sSrc=sSrc.replaceAll(" ","+");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV_INIT.getBytes());
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//	            SecretKey generateSKey = generateSKey(sKey);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64Util.decode(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //每次生成新的iv
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception{
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }

    /* 解决windows与linux相互加密解密出错 */
    public static SecretKey generateSKey(String strKey){
        try {
            KeyGenerator generator = KeyGenerator.getInstance( "AES" );
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(strKey.getBytes());
            generator.init(128,secureRandom);
            return generator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

