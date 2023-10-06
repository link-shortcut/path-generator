package me.lnsc.pathgenerator.zookeeper;

import org.apache.zookeeper.KeeperException;

import java.io.UnsupportedEncodingException;

public interface ZooKeeperManager {
    void create(String path, byte[] data) throws KeeperException, InterruptedException;

    Object getZNodeData(String path, boolean watchFlag) throws InterruptedException, KeeperException, UnsupportedEncodingException;

    void update(String path, byte[] data) throws KeeperException, InterruptedException;
}
