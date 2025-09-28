package work.chncyl.base.global.tools.requestTool.service;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;

public class ServletInputStreamImpl extends ServletInputStream {

    private final ByteArrayInputStream inputStream;

    public ServletInputStreamImpl(ByteArrayInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() {
        return inputStream.read();
    }

    @Override
    public boolean isFinished() {
        return inputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        // 留空，可根据需要实现
    }
}