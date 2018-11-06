package com.fms.utils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * 请求缓存包装
 *
 * @author Jinkai.Ma
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
    private PrintWriter cachedWriter;
    private CharArrayWriter bufferedWriter;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        bufferedWriter = new CharArrayWriter();
        cachedWriter = new PrintWriter(bufferedWriter);
    }

    @Override
    public PrintWriter getWriter() {
        return cachedWriter;
    }
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        cachedWriter.close();
        bufferedWriter.close();
    }

    public String getContent() {
        return bufferedWriter.toString();
    }
}
