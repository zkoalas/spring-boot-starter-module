package com.github.zkoalas.zookeeper.impl;

import com.github.zkoalas.zookeeper.ZookeeperIdGenerator;
import com.github.zkoalas.zookeeper.configuration.ZookeeperProperties;
import com.github.zkoalas.zookeeper.exception.ZookeeperException;
import com.github.zkoalas.zookeeper.handler.CuratorHandler;
import com.github.zkoalas.zookeeper.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PreDestroy;

public class ZookeeperIdGeneratorImpl implements ZookeeperIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperIdGeneratorImpl.class);

    private static final int MAX_BATCH_COUNT = 1000;

    @Autowired
    private CuratorHandler curatorHandler;

    @Autowired
   private ZookeeperProperties zookeeperProperties;


    @PreDestroy
    public void destroy() {
        try {
            curatorHandler.close();
        } catch (Exception e) {
            throw new ZookeeperException("Close ZooKeeper failed", e);
        }
    }

    @Override
    public String nextSequenceId(String name, String key) throws Exception {
        if (StringUtils.isEmpty(name)) {
            throw new ZookeeperException("name is null or empty");
        }
        if (StringUtils.isEmpty(key)) {
            throw new ZookeeperException("key is null or empty");
        }
        String compositeKey = KeyUtil.getCompositeKey(zookeeperProperties.getPrefix(), name, key);
        return nextSequenceId(compositeKey);
    }

    @Override
    public String nextSequenceId(String compositeKey) throws Exception {
        if (StringUtils.isEmpty(compositeKey)) {
            throw new ZookeeperException("Composite key is null or empty");
        }
        curatorHandler.validateStartedStatus();
        String path = curatorHandler.getPath(zookeeperProperties.getPrefix(), compositeKey);
        // 并发过快，这里会抛“节点已经存在”的错误，当节点存在时候，就不会创建，所以不必打印异常
        try {
            if (!curatorHandler.pathExist(path)) {
                curatorHandler.createPath(path, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {

        }
        ZooKeeper curator = curatorHandler.getZooKeeper();
        int nextSequenceId = curator.setData(path,"".getBytes(),-1).getVersion();
        if (zookeeperProperties.isPrint()) {
            LOG.info("Next sequenceId id is {} for key={}", nextSequenceId, compositeKey);
        }
        return String.valueOf(nextSequenceId);
    }

    @Override
    public String[] nextSequenceIds(String name, String key, int count) throws Exception {
        if (count <= 0 || count > MAX_BATCH_COUNT) {
            throw new ZookeeperException(String.format("Count can't be greater than %d or less than 0", MAX_BATCH_COUNT));
        }

        String[] nextSequenceIds = new String[count];
        for (int i = 0; i < count; i++) {
            nextSequenceIds[i] = nextSequenceId(name, key);
        }
        return nextSequenceIds;
    }

    @Override
    public String[] nextSequenceIds(String compositeKey, int count) throws Exception {
        if (count <= 0 || count > MAX_BATCH_COUNT) {
            throw new ZookeeperException(String.format("Count can't be greater than %d or less than 0", MAX_BATCH_COUNT));
        }

        String[] nextSequenceIds = new String[count];
        for (int i = 0; i < count; i++) {
            nextSequenceIds[i] = nextSequenceId(compositeKey);
        }

        return nextSequenceIds;
    }
}