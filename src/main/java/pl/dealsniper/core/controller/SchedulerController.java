/* (C) 2025 */
package pl.dealsniper.core.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        schedulerService.startScheduledTask(userId, sourceId, taskName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resume")
    public ResponseEntity<?> resumeExistingTask(
            @RequestParam UUID userId, @RequestParam Long sourceId, @RequestParam String taskName) {
        sourceService.validateUserOwnsSource(userId, sourceId);
        schedulerService.resumeInactiveTask(userId, sourceId, taskName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stop")
    public ResponseEntity<?> stopScheduledTask(@RequestParam UUID userId, @RequestParam Long sourceId) {
        sourceService.validateUserOwnsSource(userId, sourceId);
        schedulerService.stopScheduledTask(userId, sourceId);
        return ResponseEntity.ok().build();
    }
}
