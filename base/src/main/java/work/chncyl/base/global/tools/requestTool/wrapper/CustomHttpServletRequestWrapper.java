package work.chncyl.base.global.tools.requestTool.wrapper;

import cn.hutool.core.io.IoUtil;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import work.chncyl.base.global.tools.requestTool.service.ServletInputStreamImpl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 自定义HttpServletRequest包装类
 * 实现请求体多次读取
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] requestBody;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        requestBody = IoUtil.readBytes(request.getInputStream());
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