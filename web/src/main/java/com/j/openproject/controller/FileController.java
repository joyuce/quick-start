package com.j.openproject.controller;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.j.openproject.annotation.NoLogin;
import com.j.openproject.annotation.RestPathController;
import com.j.openproject.base.CommonRs;
import com.j.openproject.code.CommonRsCode;
import com.j.openproject.exception.AppException;
import com.j.openproject.utils.FileUtil;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type UploadController
 * @Desc
 * @date 2019年12月06日
 * @Version V1.0
 */
@NoLogin
@Api(description = "文件接口")
@Slf4j
@RestPathController("/file")
public class FileController {

    private String filePathPre = System.getProperty("user.dir") + "/upload";

    @Value("${server.port:8080}")
    private String port;

    @Value("${server.ip:localhost}")
    private String serverIp;

    /**
     * 文件上传
     *
     * @param files
     * @return
     */
    @PostMapping(value = "/upload")
    public CommonRs imgUpload(@RequestParam(value = "file") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new AppException(CommonRsCode.FILE_ERROR);
        }
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            LocalDate date = LocalDate.now();
            // 获取文件名
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                log.error("获取不到文件名");
                continue;
            }
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String folder = "/" + date.toString().replace("-", "") + "/";
            fileName = IdUtil.simpleUUID() + suffixName;
            File dest = FileUtil.createFile(filePathPre + folder + fileName);
            if (dest == null) {
                log.error("创建文件失败");
                continue;
            }
            try {
                file.transferTo(dest);
                String url = "http://" + serverIp + ":" + port + "/file/download" + folder + fileName;
                urls.add(url);
            } catch (Exception e) {
                log.error("文件保存异常", e);
                throw new AppException(CommonRsCode.INT_ERROR);
            }
        }
        return CommonRs.createSuccessRs(urls);
    }

    /**
     * 获取文件
     *
     * @param isOnLine
     * @param filename
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "/download/{filename:.+}")
    public void getFile(
            @RequestParam(value = "isOnLine", defaultValue = "false") boolean isOnLine,
            @PathVariable("filename") String filename, HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        String path = filePathPre + "/" + filename;
        FileUtil.downloadOrVisit(path, response, isOnLine);
    }

    /**
     * 获取文件
     *
     * @param isOnLine
     * @param folderName
     * @param filename
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "/download/{folderName}/{filename:.+}")
    public void getFile(
            @RequestParam(value = "isOnLine", defaultValue = "false") boolean isOnLine,
            @PathVariable("folderName") String folderName, @PathVariable("filename") String filename,
            HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        String path = filePathPre + "/" + folderName + "/" + filename;
        FileUtil.downloadOrVisit(path, response, isOnLine);
    }

    /**
     * 获取文件
     *
     * @param isOnLine
     * @param folderName
     * @param folderName2
     * @param filename
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "/download/{folderName}/{folderName2}/{filename:.+}")
    public void getFile(
            @RequestParam(value = "isOnLine", defaultValue = "false") boolean isOnLine,
            @PathVariable("folderName") String folderName, @PathVariable("folderName2") String folderName2,
            @PathVariable("filename") String filename, HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        String path = filePathPre + "/" + folderName + "/" + folderName2 + "/" + filename;
        FileUtil.downloadOrVisit(path, response, isOnLine);
    }

}
