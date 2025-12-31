package pl.dealsniper.core.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.dealsniper.core.configuration.ApplicationConfiguration;
import pl.dealsniper.core.model.Task;
import pl.dealsniper.core.service.SchedulerService;
import pl.dealsniper.core.service.TaskService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRecoveryListener {

    private final TaskService taskService;
    private final SchedulerService schedulerService;
    private final ApplicationConfiguration applicationConfiguration;

    @EventListener(ApplicationReadyEvent.class)
    public void recoverActiveTasks() {
        log.info("Starting recovery of active scheduled tasks...");

        int pageNumber = 0;
        int pageSize = 10;
        boolean hasMorePages = true;

        while (hasMorePages) {
            Pageable page = PageRequest.of(pageNumber, pageSize);
            Page<Task> activeTasks = taskService.getActiveTaskPage(page);
            for (Task task : activeTasks.getContent()) {
                schedulerService.recoverTask(task);
                log.info("Recovered task: {} for user {}", task.getTaskName(), task.getUserId());
            }
            hasMorePages = activeTasks.hasNext();
            pageNumber++;
        }
        log.info("Task recovery completed. Recovered all tasks");
    }
}
