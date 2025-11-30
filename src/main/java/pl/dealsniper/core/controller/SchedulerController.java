/* (C) 2025 */
package pl.dealsniper.core.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.response.TaskResponse;
import pl.dealsniper.core.service.SchedulerService;
import pl.dealsniper.core.service.SourceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/scheduler")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final SourceService sourceService;

    @GetMapping("/start")
    public ResponseEntity<?> startScheduledTask(
            @RequestParam UUID userId, @RequestParam Long sourceId, @RequestParam String taskName) {
        sourceService.validateUserOwnsSource(userId, sourceId);
        TaskResponse response = schedulerService.startNewScheduledTask(userId, sourceId, taskName);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/resume")
    public ResponseEntity<?> resumeExistingTask(
            @RequestParam UUID userId, @RequestParam Long sourceId, @RequestParam String taskName) {
        sourceService.validateUserOwnsSource(userId, sourceId);
        TaskResponse response = schedulerService.resumeScheduledTask(userId, sourceId, taskName);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/stop")
    public ResponseEntity<?> stopScheduledTask(
            @RequestParam UUID userId, @RequestParam Long sourceId, @RequestParam String taskName) {
        sourceService.validateUserOwnsSource(userId, sourceId);
        TaskResponse response = schedulerService.stopScheduledTask(userId, sourceId, taskName);
        return ResponseEntity.ok().body(response);
    }
}
