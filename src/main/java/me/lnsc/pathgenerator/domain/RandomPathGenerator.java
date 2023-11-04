package me.lnsc.pathgenerator.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomPathGenerator implements PathGenerator {
    private static final int PATH_LENGTH = 7;
    private static final List<Character> AVAILABLE_CHARACTERS = initializedAvailableCharacters();

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        for (int count = 0; count < PATH_LENGTH; count++) {
            int randomNumber = (int) (Math.random() * AVAILABLE_CHARACTERS.size());
            sb.append(AVAILABLE_CHARACTERS.get(randomNumber));
        }
        return sb.toString();
    }

    private static List<Character> initializedAvailableCharacters() {
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
