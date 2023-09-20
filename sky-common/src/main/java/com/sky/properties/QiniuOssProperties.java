package com.sky.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 七牛云配置
 */

@ConfigurationProperties(prefix = "sky.qiniu")
@Data
@Component
public class QiniuOssProperties {
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String httAddressName;

}
