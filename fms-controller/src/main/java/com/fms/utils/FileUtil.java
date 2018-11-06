package com.fms.utils;

import sun.security.action.GetPropertyAction;

import java.io.File;
import java.security.AccessController;
import java.security.SecureRandom;

public class FileUtil {
    private static final SecureRandom random = new SecureRandom();
    public static File getTempDir() {
        long n = random.nextLong();
        if (n == Long.MIN_VALUE) {
            n = 0;      // corner case
        } else {
            n = Math.abs(n);
        }
        File dir = new File(AccessController
                .doPrivileged(new GetPropertyAction("java.io.tmpdir")) + Long.toString(n));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir.deleteOnExit();
        return dir;
    }

    public static File createTempFile(String fileName) {
        File file = new File(FileUtil.getTempDir(), fileName);
        file.deleteOnExit();
        return file;
    }
}
