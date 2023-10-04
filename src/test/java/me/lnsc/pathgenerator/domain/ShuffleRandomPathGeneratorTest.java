package me.lnsc.pathgenerator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShuffleRandomPathGeneratorTest {

    @Test
    @DisplayName("임의의 Path를 반환한다.")
    void generateSuccess() {
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue(List.of("aaa", "bbb", "ccc"));
        ShuffleRandomPathGenerator generator = new ShuffleRandomPathGenerator(queue);

        String first = generator.generate();
        String second = generator.generate();
        String third = generator.generate();

        assertThat(first).isEqualTo("aaa");
        assertThat(second).isEqualTo("bbb");
        assertThat(third).isEqualTo("ccc");
    }

    @Test
    @DisplayName("PathGenerator 내부의 Queue가 빈 상태에서 생성할 경우 예외가 발생한다.")
    void generateFail() {
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue(List.of());
        ShuffleRandomPathGenerator generator = new ShuffleRandomPathGenerator(queue);

        assertThatThrownBy(() -> generator.generate())
                .isInstanceOf(RuntimeException.class);
    }
}