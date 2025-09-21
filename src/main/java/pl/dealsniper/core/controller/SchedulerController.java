/* (C) 2025 */
package pl.dealsniper.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.service.SchedulerService;
import pl.dealsniper.core.service.SourceService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final SourceService sourceService;

    @GetMapping("/start")
    public ResponseEntity<?> startScheduledTask(@RequestParam UUID userId, @RequestParam Long sourceId,
                                                @RequestParam String taskName) {
        sourceService.validateUserOwnsSource(userId, sourceId);
        schedulerService.startScheduledTask(userId, sourceId, taskName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stop")
    public ResponseEntity<?> stopScheduledTask(@RequestParam UUID userId, @RequestParam Long sourceId) {
        sourceService.validateUserOwnsSource(userId, sourceId);
        schedulerService.stopScheduledTask(userId, sourceId);
        return ResponseEntity.ok().build();
    }
}
