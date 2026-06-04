package com.bjtumarket.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Configuration
@PropertySource(value = "classpath:application-cos.properties", ignoreResourceNotFound = true)
public class CosUtil {

    @Value("${cos.enabled:false}")
    private boolean enabled;

    @Value("${cos.secret-id:}")
    private String secretId;

    @Value("${cos.secret-key:}")
    private String secretKey;

    @Value("${cos.region:ap-beijing}")
    private String region;

    @Value("${cos.bucket:}")
    private String bucket;

    @Value("${cos.base-url:}")
    private String baseUrl;

    private COSClient cosClient;

    @PostConstruct
    public void init() {
        if (!enabled || secretId == null || secretId.isEmpty()) {
            System.out.println("[COS] 未启用或缺少配置，使用本地存储");
            return;
        }
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig config = new ClientConfig(new Region(region));
        cosClient = new COSClient(cred, config);
        System.out.println("[COS] 已启用，bucket=" + bucket + ", region=" + region);
    }

    public boolean isEnabled() {
        return enabled && cosClient != null;
    }

    public void delete(String key) {
        if (!isEnabled()) return;
        cosClient.deleteObject(bucket, key);
        System.out.println("[COS] 已删除: " + key);
    }

    public String upload(MultipartFile file, String key) throws IOException {
        File tempFile = File.createTempFile("cos-", key.substring(key.lastIndexOf(".")));
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        PutObjectRequest request = new PutObjectRequest(bucket, key, tempFile);
        cosClient.putObject(request);
        tempFile.delete();
        return baseUrl + "/" + key;
    }

    @PreDestroy
    public void destroy() {
        if (cosClient != null) cosClient.shutdown();
    }
}
