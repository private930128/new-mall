package ltd.newbee.mall.util.smsUtil;

import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64Util {


    /**
     *
     * TODO
     * @param str
     * @return str
     */
    public static String encode(String str){

        String encoded = null;

        encoded = Base64.encodeBytes(str.getBytes());

        return encoded;
    }

    /**
     *
     * TODO
     * @param /byte[]
     * @return str
     */
    public static String encode(byte[] bytes){

        String encoded = null;

        encoded = Base64.encodeBytes(bytes);

        return encoded;
    }

    /**
     *
     * @param encoded
     * @return
     * @throws IOException byte[]
     */
    public static byte[] decode(String encoded) throws IOException{

        byte[] decoded = null;

        decoded = Base64.decode(encoded);

        return decoded;
    }

    /**
     *
     * @param encoded
     * @return
     * @throws IOException String
     */
    public static String decode(byte[] encoded) throws IOException{

        byte[] decoded = null;

        decoded = Base64.decode(new String(encoded));

        return new String(decoded);
    }

    /**
     *
     * 功能描述：Base64加密（将byte[]转换成字符串）  方法。
     *
     * @param data byte[]类型 要加密的数据
     *
     * @return String类型 加密后结果
     *
     */
    public static String encryptBASE64(byte[] data)
    {
        return (new BASE64Encoder()).encodeBuffer(data);
    }

    /**
     *
     * 功能描述：Base64解密（将字符串转换成byte[]）  方法。
     *
     * @param data String类型 要解密数据
     *
     * @return byte[]类型 解密后byte数组结果
     *
     */
    public static byte[] decryptBASE64(String data)
    {
        try{
            return (new BASE64Decoder()).decodeBuffer(data);
        }catch(Exception e){
            return null;
        }
    }

    /**
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     * @return
     */
    public static String getImageStr(String filePath) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(filePath);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encoded = Base64.encodeBytes(data);
        return encoded;
    }

    /**
     * @Description: 将base64编码字符串转换为图片
     * @Author:
     * @CreateTime:
     * @param imgStr base64编码字符串
     * @param path 图片路径-具体到文件
     * @return
     * @throws IOException
     */
    public static boolean generateImage(String imgStr, String path) throws Exception{
        if (StringUtils.isEmpty(imgStr)) {
            return false;
        }
        byte[] decode = decode(imgStr);
        // 处理数据
        for(int i=0;i<decode.length;i++){
            if (decode[i]<0) {
                decode[i] = (byte) (decode[i]+256);
            }
        }
        FileOutputStream out = new FileOutputStream(path);
        out.write(decode);
        out.flush();
        out.close();
        return true;
    }

}