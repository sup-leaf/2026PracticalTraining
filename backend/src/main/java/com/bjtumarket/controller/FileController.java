package com.bjtumarket.controller;

import com.bjtumarket.util.CosUtil;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Api(tags = "文件模块")
@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Autowired
    private CosUtil cosUtil;

    @ApiOperation("上传简历文件（PDF/Word，≤5MB）")
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
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        String key = datePath + "/" + newFileName;

        // COS 上传
        if (cosUtil.isEnabled()) {
            try {
                String cosUrl = cosUtil.upload(file, key);
                return Result.success(cosUrl);
            } catch (IOException e) {
                return Result.error("文件上传失败（COS）");
            }
        }

        // 本地降级
        File fullDir = new File(uploadPath, datePath);
        if (!fullDir.exists()) fullDir.mkdirs();
        File dest = new File(fullDir, newFileName);
        try {
            file.transferTo(dest);
            return Result.success("/uploads/" + key);
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }
    }
}