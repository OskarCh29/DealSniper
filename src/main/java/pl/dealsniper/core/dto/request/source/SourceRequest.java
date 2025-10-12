/* (C) 2025 */
package pl.dealsniper.core.dto.request.source;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;
import pl.dealsniper.core.validation.source.ProvidedSource;
import pl.dealsniper.core.validation.source.UserSource;

@Builder
@ProvidedSource
public record SourceRequest(
        @NotNull UUID userId, @URL(message = "Incorrect URL format") String filteredUrl, UrlSourceRequest urlRequest)
        implements UserSource {}
