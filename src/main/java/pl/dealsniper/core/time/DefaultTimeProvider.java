/* (C) 2025 */
package pl.dealsniper.core.time;

import java.time.Clock;
import java.time.LocalDateTime;

public class DefaultTimeProvider implements TimeProvider {

    private final Clock clock;

    public DefaultTimeProvider() {
        this.clock = Clock.systemUTC();
    }

    public DefaultTimeProvider(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
