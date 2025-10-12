/* (C) 2025 */
package pl.dealsniper.core.validation.source;

import pl.dealsniper.core.dto.request.source.UrlSourceRequest;

public interface UserSource {

    String filteredUrl();

    UrlSourceRequest urlRequest();
}
