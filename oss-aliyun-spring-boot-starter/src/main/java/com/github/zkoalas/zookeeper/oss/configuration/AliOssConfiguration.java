package com.github.zkoalas.zookeeper.oss.configuration;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(AliOssProperties.class)
public class AliOssConfiguration {

    @Resource
    private AliOssProperties aliOssProperties;

    @Bean
    public OSS ossClient(){
        OSS ossClient = new OSSClientBuilder().build(aliOssProperties.endpoint, aliOssProperties.accessKeyId, aliOssProperties.accessKeySecret);
        return ossClient;
    }

}