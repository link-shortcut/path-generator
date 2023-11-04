package me.lnsc.pathgenerator.config;

import me.lnsc.pathgenerator.domain.PathGenerator;
import me.lnsc.pathgenerator.domain.RandomPathGenerator;
import me.lnsc.pathgenerator.domain.ShuffleRandomPathGenerator;
import me.lnsc.pathgenerator.helper.ShufflePathInitializationHelper;
import me.lnsc.pathgenerator.service.ZookeeperService;
import me.lnsc.pathgenerator.zookeeper.ZooKeeperManager;
import me.lnsc.pathgenerator.zookeeper.ZooKeeperManagerImpl;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.zookeeper.ZookeeperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Configuration
public class AppConfig {
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public ZooKeeperManager zookeeperManager(ZookeeperProperties properties) throws IOException, InterruptedException {
        return new ZooKeeperManagerImpl(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "path-generator", name = "type", havingValue = "shuffle")
    public PathGenerator shuffleRandomPathGenerator(@Value("${path-generator.gap:100000}") int gap, ZookeeperService zookeeperService) throws UnsupportedEncodingException, InterruptedException, KeeperException {
        log.info("ShuffleRandomPathGenerator has been registered as PathGenerator.");
        long offset = zookeeperService.increaseOffsetWithRetry(gap);
        return new ShuffleRandomPathGenerator(offset, gap);
    }

    @Bean
    @ConditionalOnProperty(prefix = "path-generator", name = "type", havingValue = "shuffle")
    public ShufflePathInitializationHelper shufflePathInitializationHelper(ZookeeperService zookeeperService, PathGenerator pathGenerator) {
        return new ShufflePathInitializationHelper(zookeeperService, (ShuffleRandomPathGenerator) pathGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public PathGenerator randomPathGenerator() {
        log.info("RandomPathGenerator has been registered as PathGenerator.");
        return new RandomPathGenerator();
    }
}