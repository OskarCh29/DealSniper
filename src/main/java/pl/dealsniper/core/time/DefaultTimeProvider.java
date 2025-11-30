/* (C) 2025 */
package pl.dealsniper.core.time;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DefaultTimeProvider implements TimeProvider {

    private final Clock clock;

    public DefaultTimeProvider() {
        this.clock = Clock.systemUTC();
    }

    public DefaultTimeProvider(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LocalDateTime timeNow() {
        return LocalDateTime.now(clock);
    }

    @Override
    public Instant now() {
        return Instant.now(clock);
    }
}
