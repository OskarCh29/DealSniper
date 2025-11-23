/* (C) 2025 */
package pl.dealsniper.core.mock;

import java.time.LocalDateTime;
import java.util.UUID;

public final class MockConst {

    private MockConst() {}

    // UUID && ID
    public static final UUID MOCK_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final Long MOCK_ID = 1L;

    // TEST_DATE
    public static final LocalDateTime MOCK_CREATED_AT = LocalDateTime.of(2025, 3, 15, 10, 0);

    // USER_DATA
    public static final String MOCK_EMAIL = "user@example.com";
    public static final String MOCK_HASH_PASSWORD = "b56094a66443430e2b0871f31439fda21d7c4a50cd5ddadb511c85114e3906c9";
    public static final String MOCK_VERIFICATION_CODE = "000111222333444555666777888999";

    // URL
    public static final String MOCK_BASE_URL = "https://example.com";
    public static final String MOCK_OFFER_URL = "https://example.com/car/offer";
}
