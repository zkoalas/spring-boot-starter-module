package com.github.zkoalas.zookeeper.handler;

import com.github.zkoalas.zookeeper.configuration.ZookeeperProperties;
import com.github.zkoalas.zookeeper.exception.ZookeeperException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
public class CuratorHandlerImpl implements CuratorHandler{

    @Autowired
    ZooKeeper zooKeeper;

    @Autowired
    private ZookeeperProperties zookeeperProperties;


    //初始化根节点
    @PostConstruct
    private void initialize() throws Exception {
        try {
            String rootPath = getRootPath(zookeeperProperties.getPrefix());
            if (!pathExist(rootPath)) {
                createPath(rootPath, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            log.error("Initialize ZooKeeper failed", e);

            throw e;
        }
    }



    @Override
    public void close() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isInitialized() {
        return zooKeeper != null;
    }

    @Override
    public boolean isStarted() {
        boolean status = zooKeeper.getState() == ZooKeeper.States.CONNECTED;
        return status;
    }

    // 检查ZooKeeper是否是启动状态
    @Override
    public void validateStartedStatus() {
        if (zooKeeper == null) {
            throw new ZookeeperException("ZooKeeper isn't initialized");
        }
        if (!isStarted()) {
            throw new ZookeeperException("ZooKeeper is closed");
        }
    }

    // 检查ZooKeeper是否是关闭状态
    @Override
    public void validateClosedStatus() {
        if (zooKeeper == null) {
            throw new ZookeeperException("ZooKeeper isn't initialized");
        }
        if (isStarted()) {
            throw new ZookeeperException("ZooKeeper is started");
        }
    }

    @Override
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    @Override
    public boolean pathExist(String path) throws Exception {
        Stat exists = zooKeeper.exists(path, false);
        return ObjectUtils.isEmpty(exists);
    }

    @Override
    public Stat getPathStat(String path) throws Exception {
        return  zooKeeper.exists(path,true);
    }


    @Override
    public void createPath(String path, CreateMode mode) throws Exception {
        zooKeeper.create(path,null,null,mode);
    }

    @Override
    public void createPath(String path, byte[] data, CreateMode mode) throws Exception {
        zooKeeper.create(path,data,null,mode);
    }

    @Override
    public void deletePath(String path) throws Exception {
        zooKeeper.delete(path,0);
    }

    @Override
    public List<String> getChildNameList(String path) throws Exception {
        zooKeeper.getChildren(path,true);
        return null;
    }

    @Override
    public List<String> getChildPathList(String path) throws Exception {
        return zooKeeper.getChildren(path,true);
    }

    @Override
    public String getRootPath(String prefix) {
        return "/" + prefix;
    }

    @Override
    public String getPath(String prefix, String key) {
        return "/" + prefix + "/" + key;
    }
}
