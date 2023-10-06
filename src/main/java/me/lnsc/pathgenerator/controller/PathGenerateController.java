package me.lnsc.pathgenerator.controller;

import me.lnsc.pathgenerator.domain.PathGenerator;
import me.lnsc.pathgenerator.domain.exception.PathExhaustionException;
import me.lnsc.pathgenerator.helper.ShufflePathInitializationHelper;
import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
public class PathGenerateController {
    private final ShufflePathInitializationHelper pathInitializationHelper;
    private final PathGenerator pathGenerator;

    public PathGenerateController(ShufflePathInitializationHelper pathInitializationHelper, PathGenerator pathGenerator) {
        this.pathInitializationHelper = pathInitializationHelper;
        this.pathGenerator = pathGenerator;
    }

    @GetMapping("/")
    public String getShortenPath() throws UnsupportedEncodingException, InterruptedException, KeeperException {
        try {
            return pathGenerator.generate();
        } catch (PathExhaustionException e) {
            pathInitializationHelper.initializePathGenerator();
            return getShortenPath();
        }
    }
}
