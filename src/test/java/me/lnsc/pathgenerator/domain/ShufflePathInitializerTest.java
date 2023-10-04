package me.lnsc.pathgenerator.domain;

import me.lnsc.pathgenerator.domain.ShufflePathInitializer.BaseNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShufflePathInitializerTest {
    private static final ShufflePathInitializer INITIALIZER = new ShufflePathInitializer(3, List.of('a', 'b', 'c'));

    @Test
    @DisplayName("pathLength가 0보다 작거나, availableCharacters 크기가 2보다 작은 경우 예외가 발생한다.")
    void createShufflePathInitializerFail() {
        assertThatThrownBy(() -> new ShufflePathInitializer(-1, List.of('a', 'b', 'c')))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ShufflePathInitializer(1, List.of('a')))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("offset 부터 gap 만큼의 Path를 생성해 Shuffle한 후 반환한다.")
    void initailizeShuffledQueueSuccess() {
        ConcurrentLinkedQueue<String> shuffledQueue = INITIALIZER.initializeShuffledQueue(2, 3);

        assertThat(shuffledQueue).hasSize(3);
        assertThat(shuffledQueue).containsOnlyOnce("aac", "aba", "abb");
    }

    @Test
    @DisplayName("입력한 offset과 gap이 0보다 작을 경우 예외가 발생한다.")
    void initailizeShuffledQueueFail() {
        assertThatThrownBy(() -> INITIALIZER.initializeShuffledQueue(-1, 1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> INITIALIZER.initializeShuffledQueue(0, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Nested
    @DisplayName("BaseNumber 테스트")
    class BaseNumberTest {
        private static final int BASE2 = 2;
        private static final BaseNumber BASE_NUMBER = new BaseNumber(List.of(1, 0, 1), BASE2);

        @Test
        @DisplayName("숫자를 입력한 진수로 변환하여 BaseNumber 객체를 반환한다.")
        void createBaseNumberSuccess() {
            BaseNumber baseNumber = new BaseNumber(4, BASE2);

            assertThat(baseNumber.getBase()).isEqualTo(BASE2);
            assertThat(baseNumber.getValue()).hasSize(3);
            assertThat(baseNumber.getValue()).containsExactly(1, 0, 0);
        }

        @Test
        @DisplayName("숫자가 음수이거나, 진수가 2 이하인 경우 예외가 발생한다.")
        void createBaseNumberFail() {
            assertThatThrownBy(() -> new BaseNumber(-1, BASE2))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> new BaseNumber(1, 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("BaseNumber를 빈자리에 0을 채워 padLength만큼 자리수를 맞춘 후 BaseNumber 객체를 반환한다.")
        void padZerosSuccess() {
            BaseNumber paddedBaseNumber = BASE_NUMBER.padZeros(5);

            assertThat(paddedBaseNumber.getValue()).hasSize(5);
            assertThat(paddedBaseNumber.getValue()).containsExactly(0, 0, 1, 0, 1);
        }

        @Test
        @DisplayName("BaseNumber의 길이보다 작은 padLength를 입력할시 예외가 발생한다.")
        void padZerosFail() {
            assertThatThrownBy(() -> BASE_NUMBER.padZeros(2))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("BaseNumber의 각 자리에 입력받은 리스트로 매핑 후 반환한다.")
        void mapToStringSuccess() {
            List<Character> mappingCharacters = List.of('a', 'b');
            String mappedString = BASE_NUMBER.mapToString(mappingCharacters);

            assertThat(mappedString).isEqualTo("bab");
        }

        @Test
        @DisplayName("입력받은 리스트의 길이가 진수와 다른 경우 예외가 발생한다.")
        void mapToStringFail() {
            List<Character> mappingCharacters = List.of('a', 'b', 'c');

            assertThatThrownBy(() -> BASE_NUMBER.mapToString(mappingCharacters))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}