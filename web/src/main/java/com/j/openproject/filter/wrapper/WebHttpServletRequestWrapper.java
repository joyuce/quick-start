package com.j.openproject.filter.wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.j.openproject.utils.HttpRequestUtil;

/**
 * @author shenxiaodong
 * @Type WebHttpServletRequestWrapper
 * @Desc
 * @date 2019年08月27日
 * @Version V1.0
 */
public class WebHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public WebHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        //todo 解密
        String str = HttpRequestUtil.getBodyContent(request);

        body = str.getBytes(Charset.forName("UTF-8"));
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
            public int read() throws IOException {
                return bais.read();
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
