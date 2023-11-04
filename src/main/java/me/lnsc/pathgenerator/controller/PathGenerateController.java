package me.lnsc.pathgenerator.controller;

import me.lnsc.pathgenerator.controller.response.GetShortenPathResponse;
import me.lnsc.pathgenerator.domain.PathGenerator;
import me.lnsc.pathgenerator.domain.exception.PathExhaustionException;
import me.lnsc.pathgenerator.helper.ShufflePathInitializationHelper;
import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class PathGenerateController {
    private final ShufflePathInitializationHelper pathInitializationHelper;
    private final PathGenerator pathGenerator;

    public PathGenerateController(ShufflePathInitializationHelper pathInitializationHelper, PathGenerator pathGenerator) {
        this.pathInitializationHelper = pathInitializationHelper;
        this.pathGenerator = pathGenerator;
    }

    @GetMapping("/")
    public GetShortenPathResponse getShortenPath(@RequestParam(required = false) Long count) throws UnsupportedEncodingException, InterruptedException, KeeperException {
        Long fetchCount = Objects.requireNonNullElse(count, 1L);
        validateCount(fetchCount);

        List<String> shortenPaths = generateShortenPaths(fetchCount);
        return new GetShortenPathResponse(shortenPaths);
    }

    private List<String> generateShortenPaths(Long fetchCount) throws UnsupportedEncodingException, InterruptedException, KeeperException {
        List<String> shortenPaths = new ArrayList<>();
        Long nowCount = 0L;
        while (nowCount < fetchCount) {
            try {
                shortenPaths.add(pathGenerator.generate());
                nowCount++;
            } catch (PathExhaustionException e) {
                pathInitializationHelper.initializePathGenerator();
            }
        }
        return shortenPaths;
    }

    private void validateCount(Long count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be more than 0");
        }
    }
}
