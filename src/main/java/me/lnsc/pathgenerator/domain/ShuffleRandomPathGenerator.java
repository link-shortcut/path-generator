package me.lnsc.pathgenerator.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ShuffleRandomPathGenerator implements PathGenerator {
    private static final int PATH_LENGTH = 7;
    private static final List<Character> AVAILABLE_CHARACTERS = initializeAvailableCharacters();
    private static final ShufflePathInitializer shufflePathInitializer = new ShufflePathInitializer(PATH_LENGTH, AVAILABLE_CHARACTERS);

    private ConcurrentLinkedQueue<String> randomShortenPaths;

    public ShuffleRandomPathGenerator(long offset, int gap) {
        initialize(offset, gap);
    }

    public ShuffleRandomPathGenerator(ConcurrentLinkedQueue<String> randomShortenPaths) {
        this.randomShortenPaths = randomShortenPaths;
    }

    public void initialize(long offset, int gap) {
        this.randomShortenPaths = shufflePathInitializer.initializeShuffledQueue(offset, gap);
    }

    @Override
    public String generate() {
        String randomPath = randomShortenPaths.poll();
        if (randomPath == null) {
            // TODO: 랜덤값 고갈된 경우 후속 처리 고민
            throw new RuntimeException("Out of RandomPath");
        }
        return randomPath;
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