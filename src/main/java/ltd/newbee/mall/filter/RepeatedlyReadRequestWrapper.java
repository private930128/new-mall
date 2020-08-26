package ltd.newbee.mall.filter;

import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.interceptor.InterceptorLogRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;

@Slf4j
public class RepeatedlyReadRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;

    RepeatedlyReadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = readBytes(request.getReader(), "utf-8");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    /**
     * 通过BufferedReader和字符编码集转换成byte数组
     *
     * @param br
     * @param encoding
     * @return
     * @throws IOException
     */
    private byte[] readBytes(BufferedReader br, String encoding) throws IOException {
        String str = null, retStr = "";
        while ((str = br.readLine()) != null) {
            retStr += str;
        }
        if (StringUtils.isNotBlank(retStr)) {
            return retStr.getBytes(Charset.forName(encoding));
        }
        return null;
    }

    /**
     * 获取请求Body
     * @param request
     * @return
     */
    public static String getBodyString(final ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = cloneInputStream(request.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error( e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error( e.getMessage(), e);;
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error( e.getMessage(), e);;
                }
            }
        }
        String res = sb.toString();
        //Emoji拦截
//        if (!"".equals(res) && EmojiParser.extractEmojis(res).size() > 0) {
//            log.error( "特殊字符:[{}]",  res);
//            throw new CommonBizException( ResponseCodeEnum.SPECIAL_STR);
//        }
        res = HtmlUtils.htmlEscape(res);
        res = res.replaceAll("&quot;", "\"").replaceAll("&#39;", "'");
        return res;
    }

    /**
     * Description: 复制输入流</br>
     *
     * @param inputStream
     *
     * @return</br>
     */
    public static InputStream cloneInputStream(ServletInputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            log.error( e.getMessage(), e);;
        }
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return byteArrayInputStream;
    }

    /**
     * 使用OutputStream流输出
     *
     *
     * @param request
     * @param response
     * @param returnMsg  输出信息
     * @throws IOException
     */

    public static void outputOneByOutputStream(HttpServletRequest request, HttpServletResponse response, String returnMsg) throws IOException {
        InterceptorLogRecord.completionLog( request, response, returnMsg );
        response.setHeader("content-type", "application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(returnMsg.getBytes("UTF-8"));
    }

}
