package com.bjtumarket.controller;

import com.bjtumarket.vo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {

    @Value("${upload.path:D:/dasanxiaShixun/project/backend/uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        if (!suffix.equalsIgnoreCase(".pdf") && !suffix.equalsIgnoreCase(".doc") &&
            !suffix.equalsIgnoreCase(".docx")) {
            return Result.error("仅支持 PDF、Word 文档格式");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("文件大小不能超过 5MB");
        }

        String datePath = LocalDate.now().toString().replace("-", "/");
        String fullPath = uploadPath + "/" + datePath;
        File uploadDir = new File(fullPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        File dest = new File(fullPath, newFileName);

        try {
            file.transferTo(dest);
            String fileUrl = "/uploads/" + datePath + "/" + newFileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }
    }
}