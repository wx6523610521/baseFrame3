package work.chncyl.base.global.tools.requestTool.wrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import work.chncyl.base.global.tools.requestTool.service.ServletInputStreamImpl;

import java.io.*;

/**
 * 自定义HttpServletRequest包装类
 * 实现请求体多次读取
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] requestBody;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        requestBody = readByteFromInputStream(request.getInputStream());
    }

    public byte[] readByteFromInputStream(InputStream inputStream) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer))!= -1) {
                result.write(buffer, 0, length);
            }
            return result.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                result.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStreamImpl(new ByteArrayInputStream(requestBody));
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}