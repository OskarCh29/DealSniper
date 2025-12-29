/* (C) 2025 */
package pl.dealsniper.core.dto.response.task;

import lombok.Getter;

@Getter
public enum TaskStatus {
    STARTED("TASK STARTED SUCCESSFULLY"),
    STOPPED("TASK STOP SUCCESSFULLY"),
    RESUMED("TASK RESUMING IN PROGRESS ");

    private final String message;

    TaskStatus(String message) {
        this.message = message;
    }
}
