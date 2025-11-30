/* (C) 2025 */
package pl.dealsniper.core.dto.response;

import lombok.Getter;

@Getter
public enum TaskStatus {
    STARTED("TASK STARTED SUCCESSFULLY"),
    STOPPED("TASK STOPPED SUCCESSFULLY"),
    RESUMED("TASK RESUMING IN PROGRESS ");

    private String message;

    TaskStatus(String message) {
        this.message = message;
    }

}
