package pl.dealsniper.core.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import pl.dealsniper.core.model.Source;

public interface SourceRepository {

    Source save(Source source);

    Optional<Source> findByUserIdAndFilterUrl(UUID userId, String filterUrl);

    List<Source> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
