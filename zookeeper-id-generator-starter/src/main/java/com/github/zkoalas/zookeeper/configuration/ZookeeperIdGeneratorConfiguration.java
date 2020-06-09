package com.github.zkoalas.zookeeper.configuration;



import com.github.zkoalas.zookeeper.ZookeeperIdGenerator;
import com.github.zkoalas.zookeeper.handler.CuratorHandler;
import com.github.zkoalas.zookeeper.handler.CuratorHandlerImpl;
import com.github.zkoalas.zookeeper.impl.ZookeeperIdGeneratorImpl;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperIdGeneratorConfiguration {

    @Resource
    ZookeeperProperties zookeeperProperties;

    @Bean
    public ZookeeperIdGenerator zookeeperIdGenerator() {
        return new ZookeeperIdGeneratorImpl();
    }


    @Bean
    @ConditionalOnMissingBean
    public ZooKeeper zooKeeper() throws IOException {
            return new ZooKeeper(zookeeperProperties.getAddress(),zookeeperProperties.getTimeout(),null);
    }

    @Bean
    @ConditionalOnMissingBean
    public CuratorHandler curatorHandler() {
        return new CuratorHandlerImpl();
    }
}