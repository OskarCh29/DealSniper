/* (C) 2025 */
package pl.dealsniper.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.request.SourceRequest;
import pl.dealsniper.core.dto.response.SourceResponse;
import pl.dealsniper.core.mapper.SourceMapper;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.service.UserSourceService;
import pl.dealsniper.core.util.ResponseUtils;

@RestController
@RequestMapping("/api/v1/sources")
@RequiredArgsConstructor
public class SourceController {

    private final UserSourceService userSourceService;
    private final SourceMapper sourceMapper;

    @PostMapping
    ResponseEntity<SourceResponse> registerNewSource(@Valid @RequestBody SourceRequest sourceRequest) {
        Source source = userSourceService.saveUserSource(sourceRequest);
        SourceResponse response = sourceMapper.toSourceResponse(source);
        return ResponseUtils.created(response, source.getId());
    }
}
