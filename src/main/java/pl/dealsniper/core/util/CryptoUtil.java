/* (C) 2025 */
package pl.dealsniper.core.util;

import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;

public abstract class CryptoUtil {

    public static String getRandomHash() {
        String randomUUID = UUID.randomUUID().toString();
        return DigestUtils.sha256Hex(randomUUID);
    }
}
