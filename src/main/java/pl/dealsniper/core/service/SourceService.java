/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.dto.response.SourceResponse;
import pl.dealsniper.core.exception.RecordNotFoundException;
import pl.dealsniper.core.exception.ResourceUsedException;
import pl.dealsniper.core.exception.UserInactiveException;
import pl.dealsniper.core.mapper.SourceMapper;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.SourceRepository;
import pl.dealsniper.core.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;
    private final SourceMapper sourceMapper;
    private final UserRepository userRepository;

    @Transactional
    public Source saveUserSource(UUID userId, String sourceUrl) {
        ensureUserActiveAndExists(userId);
        Source source = Source.builder().userId(userId).filteredUrl(sourceUrl).build();
        if (getSourceByUserIdAndURL(userId, source.getFilteredUrl()).isPresent()) {
            throw new ResourceUsedException("Provided source url already exists on your account");
        }
        return sourceRepository.save(source);
    }

    @Transactional
    public void deleteSourceById(Long id) {
        int deleted = sourceRepository.deleteById(id);
        if (deleted == 0) {
            throw new RecordNotFoundException("Source with provided id not found");
        }
    }

    @Transactional(readOnly = true)
    public void validateUserOwnsSource(UUID userId, Long sourceId) {
        if (!sourceRepository.existsForUserAndActive(userId, sourceId)) {
            throw new RecordNotFoundException(
                    "Source with ID " + sourceId + " does not belong to active user with ID " + userId);
        }
    }

    @Transactional(readOnly = true)
    public Source getSourceById(Long id) {
        return sourceRepository
                .findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Source with provided Id not found"));
    }

    @Transactional(readOnly = true)
    public Source getSourceByIdAndUserId(Long id, UUID userId) {
        return sourceRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(
                        () -> new RecordNotFoundException("Source with provided Id does not belong to provided user"));
    }

    @Transactional(readOnly = true)
    public Optional<Source> getSourceByUserIdAndURL(UUID userId, String filterUrl) {
        return sourceRepository.findByUserIdAndFilterUrl(userId, filterUrl);
    }

    @Transactional(readOnly = true)
    public SourceResponse findSourceById(Long id) {
        return sourceRepository
                .findById(id)
                .map(sourceMapper::toSourceResponse)
                .orElseThrow(() -> new RecordNotFoundException("Source with provided id does not exists"));
    }

    private void ensureUserActiveAndExists(UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User with provided Id not found"));
        if (!user.getActive()) {
            throw new UserInactiveException("Provided user is not active");
        }
    }
}
