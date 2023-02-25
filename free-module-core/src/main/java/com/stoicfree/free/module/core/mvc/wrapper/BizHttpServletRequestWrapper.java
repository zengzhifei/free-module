package com.stoicfree.free.module.core.mvc.wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author zengzhifei
 * @date 2019/9/1 23:59
 */
public class BizHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public BizHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        String bodyStr = getBodyString(request);
        body = bodyStr.getBytes(Charset.defaultCharset());
    }

    public String getBodyString() {
        final InputStream inputStream = new ByteArrayInputStream(body);

        return inputStream2String(inputStream);
    }

    private String getBodyString(final ServletRequest request) {
        try {
            return inputStream2String(request.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String inputStream2String(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}
