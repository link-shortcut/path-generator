package me.lnsc.pathgenerator.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.zookeeper.ZookeeperProperties;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ZooKeeperManagerImpl implements ZooKeeperManager, DisposableBean {
    private static ZooKeeper zooKeeper;
    private static ZooKeeperConnection zooKeeperConnection;

    public ZooKeeperManagerImpl(ZookeeperProperties properties) throws IOException, InterruptedException {
        initialize(properties);
    }

    private void initialize(ZookeeperProperties properties) throws IOException, InterruptedException {
        zooKeeperConnection = new ZooKeeperConnection();
        zooKeeper = zooKeeperConnection.connect(
                properties.getConnectString(),
                (int) properties.getSessionTimeout().toMillis()
        );
    }

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public Object getZNodeData(String path, boolean watchFlag) throws InterruptedException, KeeperException {
        byte[] data = zooKeeper.getData(path, null, null);
        return new String(data, UTF_8);
    }

    public void update(String path, byte[] data) throws KeeperException, InterruptedException {
        int version = zooKeeper.exists(path, true).getVersion();
        zooKeeper.setData(path, data, version);
    }

    @Override
    public void destroy() throws Exception {
        zooKeeperConnection.close();
    }
}