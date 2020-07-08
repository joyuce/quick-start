package com.j.openproject.utils;

import java.io.*;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type FileUtil
 * @Desc
 * @date 2019年12月06日
 * @Version V1.0
 */
@Slf4j
public class FileUtil {

    /**
     * 下载或在线访问
     *
     * @param filePath
     * @param response
     * @param isOnLine
     * @throws Exception
     */
    public static void downloadOrVisit(String filePath, HttpServletResponse response, boolean isOnLine)
            throws Exception {
        File f = new File(filePath);
        if (!f.exists()) {
            response.sendError(404, "File not found!");
            return;
        }
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
        byte[] buf = new byte[1024];
        int len = 0;
        // 非常重要
        response.reset();
        // 在线打开方式
        if (isOnLine) {
            URL u = new URL("file:///" + filePath);
            response.setContentType(u.openConnection().getContentType());
            response.setHeader("Content-Disposition", "inline; filename=" + f.getName());
            // 文件名应该编码成UTF-8
        } else {
            // 纯下载方式
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        }
        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        br.close();
        out.close();
    }

    /**
     * 文件或文件夹不存在则创建
     *
     * @param dir      文件夹
     * @param filepath 文件名
     */
    public static void createFile(String dir, String filepath) {
        File file = new File(dir);
        if (!file.exists()) {
            boolean r = file.mkdirs();
        }
        file = new File(dir + filepath);
        if (!file.exists()) {
            try {
                boolean r = file.createNewFile();
            } catch (IOException e) {
                log.error("文件创建失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建文件，如果文件夹不存在将被创建
     *
     * @param destFileName 文件路径
     */
    public static File createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            return null;
        }
        if (destFileName.endsWith(File.separator)) {
            return null;
        }
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return null;
            }
        }
        try {
            if (file.createNewFile()) {
                return file;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private FileUtil() {
    }
}
