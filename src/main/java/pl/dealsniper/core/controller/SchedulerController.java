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
import pl.dealsniper.core.service.UserSourceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final UserSourceService sourceService;

    @GetMapping("/start")
    public ResponseEntity<?> startScheduledTask(@RequestParam UUID userId, @RequestParam Long sourceId) {
        sourceService.checkForProperRelation(userId, sourceId);
        schedulerService.startScheduledTask(userId, sourceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stop")
    public ResponseEntity<?> stopScheduledTask(@RequestParam UUID userId, @RequestParam Long sourceId) {
        sourceService.checkForProperRelation(userId, sourceId);
        schedulerService.stopScheduledTask(userId, sourceId);
        return ResponseEntity.ok().build();
    }
}
