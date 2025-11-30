/* (C) 2025 */
package pl.dealsniper.core.dto.response;

public enum TaskStatus {
    STARTED("TASK STARTED SUCCESSFULLY"),
    STOPPED("TASK STOPPED SUCESSFULLY"),
    RESUMED("TASK RESUMING IN PROGRESS ");

    private String message;

    TaskStatus(String message) {}

    public String getMessage() {
        return message;
    }
}
