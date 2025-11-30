/* (C) 2025 */
package pl.dealsniper.core.time;

import java.time.Instant;
import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime timeNow();

    Instant now();
}
