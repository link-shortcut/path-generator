package me.lnsc.pathgenerator.service;

import me.lnsc.pathgenerator.domain.ShuffleRandomPathGenerator;
import me.lnsc.pathgenerator.zookeeper.ZooKeeperManager;
import me.lnsc.pathgenerator.zookeeper.ZooKeeperManagerImpl;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class ZookeeperService {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperService.class);
    private static final String OFFSET_PATH = "/offset";
    private static final int DEFAULT_RETRY_COUNT = 3;

    private final ZooKeeperManager zooKeeperManager;

    public ZookeeperService(ZooKeeperManager zooKeeperManager) {
        this.zooKeeperManager = zooKeeperManager;
    }

    public long getOffset() throws UnsupportedEncodingException, InterruptedException, KeeperException {
        Object data = null;
        try {
            data = zooKeeperManager.getZNodeData(OFFSET_PATH, false);
        } catch (KeeperException.NoNodeException e) {
            log.info("{}", e);
            log.info("Offset not exist in {} path. Proceed offset initialization", OFFSET_PATH);
            initOffset();
        }
        return Long.parseLong((String) data);
    }

    public long increaseOffsetWithRetry(int gap) throws UnsupportedEncodingException, InterruptedException, KeeperException {
        return increaseOffsetWithRetry(gap, DEFAULT_RETRY_COUNT);
    }

    public long increaseOffsetWithRetry(int gap, int failRetry) throws UnsupportedEncodingException, InterruptedException, KeeperException {
        int retryCount = 0;
        while (retryCount < failRetry) {
            try {
                return increaseOffset(gap);
            } catch (KeeperException.BadVersionException e) {
                retryCount++;
                log.info("Increase offset try {}", retryCount, e);
                if (retryCount == failRetry) {
                    log.info("Failed all {} trys.", failRetry);
                    throw e;
                }
            }
        }
        return -1;
    }

    public long increaseOffset(int gap) throws UnsupportedEncodingException, InterruptedException, KeeperException {
        String lockNode = ZooKeeperManagerImpl.ZookeeperDistributedLock.lock();
        long offset = getOffset();
        if (offset + gap < ShuffleRandomPathGenerator.MAX_OFFSET) {
            byte[] increasedOffsetData = Long.toString(offset + gap).getBytes();
            zooKeeperManager.update(OFFSET_PATH, increasedOffsetData);
        } else {
            zooKeeperManager.update(OFFSET_PATH, Long.toString(0).getBytes());
        }
        ZooKeeperManagerImpl.ZookeeperDistributedLock.unlock(lockNode);
        return offset;
    }

    private void initOffset() throws InterruptedException, KeeperException {
        byte[] initialData = Long.toString(0l).getBytes();
        zooKeeperManager.create(OFFSET_PATH, initialData);
    }
}