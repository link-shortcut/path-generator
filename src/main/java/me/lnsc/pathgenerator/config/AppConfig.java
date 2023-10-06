package me.lnsc.pathgenerator.config;

import me.lnsc.pathgenerator.domain.PathGenerator;
import me.lnsc.pathgenerator.domain.ShuffleRandomPathGenerator;
import me.lnsc.pathgenerator.helper.ShufflePathInitializationHelper;
import me.lnsc.pathgenerator.service.ZookeeperService;
import me.lnsc.pathgenerator.zookeeper.ZooKeeperManager;
import me.lnsc.pathgenerator.zookeeper.ZooKeeperManagerImpl;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.zookeeper.ZookeeperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Configuration
public class AppConfig {

    @Bean
    public ZooKeeperManager zookeeperManager(ZookeeperProperties properties) throws IOException, InterruptedException {
        return new ZooKeeperManagerImpl(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "path-generator", name = "type", havingValue = "shuffle")
    public PathGenerator pathGenerator(@Value("${path-generator.gap:100000}") int gap, ZookeeperService zookeeperService) throws UnsupportedEncodingException, InterruptedException, KeeperException {
        long offset = zookeeperService.increaseOffset(gap);
        return new ShuffleRandomPathGenerator(offset, gap);
    }

    @Bean
    @ConditionalOnProperty(prefix = "path-generator", name = "type", havingValue = "shuffle")
    public ShufflePathInitializationHelper shufflePathInitializationHelper(ZookeeperService zookeeperService, PathGenerator pathGenerator) {
        return new ShufflePathInitializationHelper(zookeeperService, (ShuffleRandomPathGenerator) pathGenerator);
    }
}