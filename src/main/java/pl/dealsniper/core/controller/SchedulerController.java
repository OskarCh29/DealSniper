/* (C) 2025 */
package pl.dealsniper.core.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.request.task.TaskChangeStateRequest;
import pl.dealsniper.core.dto.request.task.TaskCreateRequest;
import pl.dealsniper.core.dto.response.task.TaskResponse;
import pl.dealsniper.core.service.SchedulerService;
import pl.dealsniper.core.service.SourceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/scheduled-tasks")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final SourceService sourceService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskCreateRequest taskRequest) {
        sourceService.validateUserOwnsSource(taskRequest.userId(), taskRequest.sourceId());
        TaskResponse response = schedulerService.startNewScheduledTask(taskRequest);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/state")
    public ResponseEntity<TaskResponse> resumeExistingTask(@RequestBody @Valid TaskChangeStateRequest stateRequest) {
        sourceService.validateUserOwnsSource(stateRequest.userId(), stateRequest.sourceId());
        TaskResponse response = schedulerService.changeTaskState(stateRequest);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{userId}/tasks-sources/{sourceId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID userId, @PathVariable Long sourceId) {
        schedulerService.deleteTask(userId, sourceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
