package me.lnsc.pathgenerator.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ShufflePathInitializer {
    private int pathLength;
    private List<Character> availableCharacters;

    public ShufflePathInitializer(int pathLength, List<Character> availableCharacters) {
        if (pathLength < 0) {
            throw new IllegalArgumentException("pathLength must be more than 0.");
        } else if (availableCharacters.size() < 2) {
            throw new IllegalArgumentException("availableCharacters size must be more than 2.");
        }
        this.pathLength = pathLength;
        this.availableCharacters = availableCharacters;
    }

    public ConcurrentLinkedQueue<String> initializeShuffledQueue(long offset, int gap) {
        if (offset < 0 || gap < 0) {
            throw new IllegalArgumentException("Both the offset and the gap must be more than 0.");
        }
        List<String> shortenPaths = findAllShortenPaths(offset, gap);
        Collections.shuffle(shortenPaths);
        return new ConcurrentLinkedQueue<>(shortenPaths);
    }

    public String toShortenPath(BaseNumber baseNumber, int pathLength) {
        BaseNumber paddedBaseNumber = baseNumber.padZeros(pathLength);
        return paddedBaseNumber.mapToString(availableCharacters);
    }

    private List<String> findAllShortenPaths(long offset, int gap) {
        List<String> result = new ArrayList<>();
        for (int count = 0; count < gap; count++) {
            BaseNumber baseNumber =
                    new BaseNumber(offset + count, availableCharacters.size());
            String shortenPath = toShortenPath(baseNumber, pathLength);

            // 해당 범위에서 생성된 Path 중 정해진 Path의 길이보다 작거나 같은 경우만 추가한다.
            if (shortenPath.length() > pathLength) {
                break;
            }
            result.add(shortenPath);
        }
        return result;
    }

    static class BaseNumber {
        private List<Integer> value;
        private int base;

        public BaseNumber(List<Integer> value, int base) {
            this.value = value;
            this.base = base;
        }

        public BaseNumber(long number, int base) {
            this.value = convertToBase(number, base);
            this.base = base;
        }

        public BaseNumber padZeros(int padLength) {
            if (value.size() > padLength) {
                throw new IllegalArgumentException("padLength must be more than BaseNumber value size.");
            }

            List<Integer> paddedValue = new ArrayList<>(value);
            int padCount = padLength - value.size();
            for (int i = 0; i < padCount; i++) {
                paddedValue.add(0, 0);
            }
            return new BaseNumber(paddedValue, base);
        }

        public String mapToString(List<Character> mappingWithOrder) {
            if (base != mappingWithOrder.size()) {
                throw new IllegalArgumentException("mappingWithOrder list size must be same with BaseNumber's base.");
            }

            return value.stream()
                    .map(mappingWithOrder::get)
                    .map(Object::toString)
                    .collect(Collectors.joining());
        }

        public List<Integer> getValue() {
            return value;
        }

        public int getBase() {
            return base;
        }

        private static List<Integer> convertToBase(long number, int base) {
            if (number < 0) {
                throw new IllegalArgumentException("number must be more than 0.");
            } else if (base < 2) {
                throw new IllegalArgumentException("base must be more than 2.");
            }

            List<Integer> baseConverted = new ArrayList<>();
            long tmp = number;
            while (tmp > 0l) {
                baseConverted.add((int) (tmp % base));
                tmp /= base;
            }
            Collections.reverse(baseConverted);
            return Collections.unmodifiableList(baseConverted);
        }
    }
}
