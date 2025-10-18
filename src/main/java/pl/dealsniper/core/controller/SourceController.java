/* (C) 2025 */
package pl.dealsniper.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.request.source.SourceRequest;
import pl.dealsniper.core.dto.response.SourceResponse;
import pl.dealsniper.core.mapper.SourceMapper;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.service.SourceService;
import pl.dealsniper.core.service.UrlService;
import pl.dealsniper.core.util.ResponseUtil;

@RestController
@RequestMapping("/api/v1/sources")
@RequiredArgsConstructor
public class SourceController {

    private final SourceService sourceService;
    private final UrlService urlService;
    private final SourceMapper sourceMapper;

    @PostMapping
    ResponseEntity<SourceResponse> registerNewSource(@Valid @RequestBody SourceRequest sourceRequest) {
        String requestedUrl = urlService.generateAndValidateUrl(sourceRequest);
        Source source = sourceService.saveUserSource(sourceRequest.userId(), requestedUrl);
        SourceResponse response = sourceMapper.toSourceResponse(source);
        return ResponseUtil.created(response, response.id());
    }

    @DeleteMapping("/{sourceId}")
    ResponseEntity<?> deleteExistingSource(@PathVariable Long sourceId) {
        sourceService.deleteSourceById(sourceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{sourceId}")
    ResponseEntity<SourceResponse> getSource(@PathVariable Long sourceId) {
        return ResponseEntity.ok(sourceService.findSourceById(sourceId));
    }
}
