/* (C) 2025 */
package pl.dealsniper.core.mock;

import static pl.dealsniper.core.mock.MockConst.MOCK_CREATED_AT;
import static pl.dealsniper.core.mock.MockConst.MOCK_EMAIL;
import static pl.dealsniper.core.mock.MockConst.MOCK_HASH_PASSWORD;
import static pl.dealsniper.core.mock.MockConst.MOCK_ID;
import static pl.dealsniper.core.mock.MockConst.MOCK_OFFER_URL;
import static pl.dealsniper.core.mock.MockConst.MOCK_UUID;
import static pl.dealsniper.core.mock.MockConst.MOCK_VERIFICATION_CODE;

import pl.dealsniper.core.dto.request.user.UserRequest;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.model.Verification;

public final class MockFactory {

    private MockFactory() {}

    public static User getMockUser() {
        return User.builder()
                .id(MOCK_UUID)
                .email(MOCK_EMAIL)
                .password(MOCK_HASH_PASSWORD)
                .active(true)
                .createdAt(MOCK_CREATED_AT)
                .build();
    }

    public static UserRequest getMockUserRequest() {
        return UserRequest.builder()
                .email(MOCK_EMAIL)
                .password(MOCK_HASH_PASSWORD)
                .build();
    }

    public static Verification getMockVerification() {
        return Verification.builder()
                .verificationCode(MOCK_VERIFICATION_CODE)
                .createdAt(MOCK_CREATED_AT)
                .requestedEmail(MOCK_EMAIL)
                .build();
    }

    public static Source getMockSource() {
        return Source.builder()
                .id(MOCK_ID)
                .userId(MOCK_UUID)
                .filteredUrl(MOCK_OFFER_URL)
                .build();
    }
}
