package io.github.tanyaofei.votekick;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeTest {

    public static void main(String[] args) {
        var now = LocalDateTime.now();
        var later = now.plus(Duration.ofSeconds(100));
        System.out.println(Duration.between(now, later).toSeconds());
    }
}
