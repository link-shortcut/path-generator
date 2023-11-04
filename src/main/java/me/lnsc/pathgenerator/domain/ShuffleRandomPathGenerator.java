package me.lnsc.pathgenerator.domain;

import me.lnsc.pathgenerator.domain.exception.PathExhaustionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ShuffleRandomPathGenerator implements PathGenerator {
    private static final Logger log = LoggerFactory.getLogger(ShuffleRandomPathGenerator.class);

    private static final int PATH_LENGTH = 7;
    private static final List<Character> AVAILABLE_CHARACTERS = initializeAvailableCharacters();
    public static final long MAX_OFFSET = (long) Math.pow((long) AVAILABLE_CHARACTERS.size(), (long) PATH_LENGTH);
    private static final ShufflePathInitializer shufflePathInitializer = new ShufflePathInitializer(PATH_LENGTH, AVAILABLE_CHARACTERS);

    private ConcurrentLinkedQueue<String> randomShortenPaths;
    private int gap;

    public ShuffleRandomPathGenerator(long offset, int gap) {
        this.gap = gap;
        initialize(offset);
    }

    public ShuffleRandomPathGenerator(ConcurrentLinkedQueue<String> randomShortenPaths) {
        this.randomShortenPaths = randomShortenPaths;
    }

    public void initialize(long offset) {
        this.randomShortenPaths = shufflePathInitializer.initializeShuffledQueue(offset, gap);
        log.info("Initialize shuffled queue with offset={}, gap={}", offset, gap);
    }

    @Override
    public String generate() {
        String randomPath = randomShortenPaths.poll();
        if (randomPath == null) {
            throw new PathExhaustionException();
        }
        log.debug("Generate shorten path = {}", randomPath);
        return randomPath;
    }

    public int getGap() {
        return gap;
    }

    private static List<Character> initializeAvailableCharacters() {
        Set<Character> result = new HashSet<>();
        for (int ch = 'a'; ch <= 'z'; ch++) {
            result.add((char) ch);
        }
        for (int ch = 'A'; ch <= 'Z'; ch++) {
            result.add((char) ch);
        }
        return List.copyOf(result);
    }
}