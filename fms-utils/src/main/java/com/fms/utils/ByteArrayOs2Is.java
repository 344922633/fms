package com.fms.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ByteArrayOs2Is extends ByteArrayOutputStream {
	
	public ByteArrayOs2Is() {
        super(1024);
    }
 
    public ByteArrayOs2Is(int size) {
        super(size);
    }
 
    public synchronized InputStream asInputStream() {
        return new ByteArrayInputStream(buf, 0, count);
    }
 
    public synchronized byte[] getBuf() {
        return buf;
    }

}
