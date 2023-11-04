package me.lnsc.pathgenerator.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.zookeeper.ZookeeperProperties;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

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
        int version = zooKeeper.exists(path, false).getVersion();
        zooKeeper.setData(path, data, version);
    }

    @Override
    public void destroy() throws Exception {
        zooKeeperConnection.close();
    }

    public static class ZookeeperDistributedLock {
        private static final String LOCK_NODE_PATH = "/offset";
        private static final String WRITE_LOCK_PATH = LOCK_NODE_PATH + "/write-";

        public static String lock() throws InterruptedException, KeeperException {
            String createdNodeActualPath = zooKeeper.create(WRITE_LOCK_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            String createdNode = getCreateNode(createdNodeActualPath);

            while (true) {
                String lowestSequenceNode = findLowestSequenceNode();
                if (lowestSequenceNode.equals(createdNode)) {   // 생성한 Node가 최소 Sequence Node이면 Lock 점유
                    return lowestSequenceNode;
                }

                // spin lock 방식으로 구현
                while (zooKeeper.exists(getNodeActualPath(lowestSequenceNode), false) != null) {
                    Thread.sleep(100);
                }
            }
        }

        public static void unlock(String node) throws InterruptedException, KeeperException {
            String nodeActualPath = getNodeActualPath(node);
            int version = zooKeeper.exists(nodeActualPath, false).getVersion();
            zooKeeper.delete(nodeActualPath, version);
        }

        private static String findLowestSequenceNode() throws KeeperException, InterruptedException {
            List<String> children = zooKeeper.getChildren(LOCK_NODE_PATH, false);
            children.sort(Comparator.naturalOrder());
            return children.get(0);
        }

        private static String getCreateNode(String createdNodeActualPath) {
            return createdNodeActualPath.substring(LOCK_NODE_PATH.length() + 1);
        }

        public static String getNodeActualPath(String node) {
            return String.format("%s/%s", LOCK_NODE_PATH, node);
        }
    }
}