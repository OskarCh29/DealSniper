/* (C) 2025 */
package pl.dealsniper.core.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.request.task.TaskChangeStateRequest;
import pl.dealsniper.core.dto.response.task.TaskResponse;
import pl.dealsniper.core.service.SchedulerService;
import pl.dealsniper.core.service.SourceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/scheduled-tasks")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final SourceService sourceService;

    @PostMapping("/start")
    public ResponseEntity<TaskResponse> createTask(
            @RequestParam UUID userId, @RequestParam Long sourceId, @RequestParam String taskName) {
        sourceService.validateUserOwnsSource(userId, sourceId);
        TaskResponse response = schedulerService.startNewScheduledTask(userId, sourceId, taskName);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/state")
    public ResponseEntity<TaskResponse> resumeExistingTask(@RequestBody @Valid TaskChangeStateRequest stateRequest) {
        sourceService.validateUserOwnsSource(stateRequest.userId(), stateRequest.sourceId());
        TaskResponse response = schedulerService.changeTaskState(stateRequest);
        return ResponseEntity.ok().body(response);
    }
}
