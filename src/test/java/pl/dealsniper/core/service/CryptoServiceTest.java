/* (C) 2025 */
package pl.dealsniper.core.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static pl.dealsniper.core.mock.MockConst.MOCK_EMAIL;

import org.junit.jupiter.api.Test;
import pl.dealsniper.core.dto.request.user.UserRequest;

public class CryptoServiceTest {

    CryptoService underTest = new CryptoService();

    @Test
    void createHashedUserRequest() {
        String password = "NotHashedPassword";

        UserRequest request = underTest.prepareHashedUserRequest(MOCK_EMAIL, password);

        assertThat(request.email()).isEqualTo(MOCK_EMAIL);

        assertThat(request.password()).isNotEqualTo(password);
    }
}
