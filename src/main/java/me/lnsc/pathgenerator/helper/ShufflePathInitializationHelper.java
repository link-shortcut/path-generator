package me.lnsc.pathgenerator.helper;

import me.lnsc.pathgenerator.domain.ShuffleRandomPathGenerator;
import me.lnsc.pathgenerator.service.ZookeeperService;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class ShufflePathInitializationHelper {
    private static final Logger log = LoggerFactory.getLogger(ShuffleRandomPathGenerator.class);
    private final ZookeeperService zookeeperService;
    private final ShuffleRandomPathGenerator pathGenerator;

    public ShufflePathInitializationHelper(ZookeeperService zookeeperService,
                                           ShuffleRandomPathGenerator pathGenerator) {
        this.zookeeperService = zookeeperService;
        this.pathGenerator = pathGenerator;
    }

    public void initializePathGenerator() throws UnsupportedEncodingException, InterruptedException, KeeperException {
        int gap = pathGenerator.getGap();
        long offset = zookeeperService.increaseOffsetWithRetry(gap);
        pathGenerator.initialize(offset);
        log.info("Shuffle path generator is initialized");
    }
}
