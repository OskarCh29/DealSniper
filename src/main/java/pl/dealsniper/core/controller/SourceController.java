/* (C) 2025 */
package pl.dealsniper.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.request.SourceRequest;
import pl.dealsniper.core.dto.response.SourceResponse;
import pl.dealsniper.core.mapper.SourceMapper;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.service.SourceService;
import pl.dealsniper.core.util.ResponseUtils;

@RestController
@RequestMapping("/api/v1/sources")
@RequiredArgsConstructor
public class SourceController {

    private final SourceService sourceService;
    private final SourceMapper sourceMapper;

    @PostMapping
    ResponseEntity<SourceResponse> registerNewSource(@Valid @RequestBody SourceRequest sourceRequest) {
        Source source = sourceService.saveUserSource(sourceRequest);
        SourceResponse response = sourceMapper.toSourceResponse(source);
        return ResponseUtils.created(response, source.getId());
    }

    @DeleteMapping("/{sourceId}")
    ResponseEntity<?> deleteExistingSource(@PathVariable Long sourceId) {
        sourceService.deleteSourceById(sourceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
