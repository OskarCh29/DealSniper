/* (C) 2025 */
package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.Verifications.VERIFICATIONS;

import com.dealsniper.jooq.tables.records.VerificationsRecord;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.mapper.VerificationMapper;
import pl.dealsniper.core.model.Verification;
import pl.dealsniper.core.repository.VerificationRepository;

@Repository
@RequiredArgsConstructor
public class VerificationRepositoryImpl implements VerificationRepository {

    private static final LocalDateTime EXPIRY_TIME = LocalDateTime.now().minusHours(1);

    private final DSLContext dsl;
    private final VerificationMapper mapper;

    @Override
    public Verification save(String verificationCode, String email) {
        VerificationsRecord saved = dsl.insertInto(VERIFICATIONS)
                .set(VERIFICATIONS.VERIFICATION_CODE, verificationCode)
                .set(VERIFICATIONS.REQUESTED_EMAIL, email)
                .returning()
                .fetchOne();
        return mapper.toDomainModel(saved);
    }

    @Override
    public Optional<Verification> getVerificationByCode(String code) {
        return dsl.selectFrom(VERIFICATIONS)
                .where(VERIFICATIONS.VERIFICATION_CODE.eq(code))
                .fetchOptional()
                .map(mapper::toDomainModel);
    }

    @Override
    public void deleteVerificationByCode(String code) {
        dsl.delete(VERIFICATIONS)
                .where(VERIFICATIONS.VERIFICATION_CODE.eq(code))
                .execute();
    }

    @Override
    public void deactivateExpiredCodes() {
        dsl.delete(VERIFICATIONS)
                .where(VERIFICATIONS.CREATED_AT.lessThan(EXPIRY_TIME))
                .execute();
    }
}
