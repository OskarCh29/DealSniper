/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import pl.dealsniper.core.model.Source;

public interface SourceRepository {

    Source save(Source source);

    Optional<Source> findByUserIdAndFilterUrl(UUID userId, String filterUrl);

    Optional<Source> findById(Long id);

    Optional<Source> findByIdAndUserId(Long id, UUID userId);

    List<Source> findByUserId(UUID userId);

    int deleteById(Long id);

    boolean existsForUserAndActive(UUID userId, Long sourceId);
}
