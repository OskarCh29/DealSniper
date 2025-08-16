package pl.dealsniper.core.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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

    private void ensureUserActiveAndExists(UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User with provided Id not found"));
        if (!user.getActive()) {
            throw new UserInactiveException("Provided user is not active");
        }
    }
}
