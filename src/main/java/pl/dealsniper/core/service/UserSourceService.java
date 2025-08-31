/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.dto.request.SourceRequest;
import pl.dealsniper.core.exception.RecordNotFoundException;
import pl.dealsniper.core.exception.UserInactiveException;
import pl.dealsniper.core.mapper.SourceMapper;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.SourceRepository;
import pl.dealsniper.core.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserSourceService {

    private final SourceRepository sourceRepository;
    private final SourceMapper sourceMapper;
    private final UserRepository userRepository;

    @Transactional
    public Source saveUserSource(SourceRequest sourceRequest) {
        ensureUserActiveAndExists(sourceRequest.userId());
        Source source = sourceMapper.toDomainSource(sourceRequest);
        return sourceRepository.save(source);
    }

    @Transactional(readOnly = true)
    public Source getSourceByUserIdAndURL(UUID userId, String filterUrl) {
        return sourceRepository
                .findByUserIdAndFilterUrl(userId, filterUrl)
                .orElseThrow(() -> new RecordNotFoundException("Provided URL is not connected to given user"));
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
