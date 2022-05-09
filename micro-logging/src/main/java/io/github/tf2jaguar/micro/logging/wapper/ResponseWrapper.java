package io.github.tf2jaguar.micro.logging.wapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author ：guodong
 * @since ：Created in 2021/7/1 15:21
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    /**
     * This class implements an output stream in which the data is written into a byte array.
     * The buffer automatically grows as data is written to it. The data can be retrieved using toByteArray() and toString().
     * Closing a ByteArrayOutputStream has no effect. The methods in this class can be called after the stream has been closed without generating an IOException.
     */
    private ByteArrayOutputStream buffer = null;
    private ServletOutputStream out = null;
    private PrintWriter writer = null;

    public ResponseWrapper(HttpServletResponse resp) throws IOException {
        super(resp);
        buffer = new ByteArrayOutputStream();
        out = new OutputStreamWrapper(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));
    }

    /**
     * 重载父类获取 outputStream 的方法
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    /**
     * 重载父类获取 writer 的方法
     */
    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        return writer;
    }

    /**
     * 重载父类获取 flushBuffer 的方法
     */
    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
        }
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    /**
     * 将 out、writer 中的数据强制输出到 buffer 里面
     * @return String
     * @throws IOException io异常
     */
    public String getResponseData() throws IOException {
        flushBuffer();
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * 内部类，对ServletOutputStream进行包装
     */
    private static class OutputStreamWrapper extends ServletOutputStream {
        private ByteArrayOutputStream bos = null;

        public OutputStreamWrapper(ByteArrayOutputStream stream) throws IOException {
            bos = stream;
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            bos.write(b, 0, b.length);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {

        }
    }

}
