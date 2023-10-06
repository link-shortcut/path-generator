package me.lnsc.pathgenerator.service;

import me.lnsc.pathgenerator.domain.ShuffleRandomPathGenerator;
import me.lnsc.pathgenerator.zookeeper.ZooKeeperManager;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class ZookeeperService {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperService.class);
    private static final String OFFSET_PATH = "/offset";

    private final ZooKeeperManager zooKeeperManager;

    public ZookeeperService(ZooKeeperManager zooKeeperManager) {
        this.zooKeeperManager = zooKeeperManager;
    }

    public long getOffset() throws UnsupportedEncodingException, InterruptedException, KeeperException {
        Object data = null;
        try {
            data = zooKeeperManager.getZNodeData(OFFSET_PATH, false);
        } catch (KeeperException e) {
            log.info("{}", e);
            log.info("Offset not exist in {} path. Proceed offset initialization", OFFSET_PATH);
            initOffset();
        }
        return Long.parseLong((String) data);
    }

    public long increaseOffset(int gap) throws UnsupportedEncodingException, InterruptedException, KeeperException {
        long offset = getOffset();
        if (offset + gap < ShuffleRandomPathGenerator.MAX_OFFSET) {
            byte[] increasedOffsetData = Long.toString(offset + gap).getBytes();
            zooKeeperManager.update(OFFSET_PATH, increasedOffsetData);
        } else {
            zooKeeperManager.update(OFFSET_PATH, Long.toString(0).getBytes());
        }
        return offset;
    }

    private void initOffset() throws InterruptedException, KeeperException {
        byte[] initialData = Long.toString(0l).getBytes();
        zooKeeperManager.create(OFFSET_PATH, initialData);
    }
}