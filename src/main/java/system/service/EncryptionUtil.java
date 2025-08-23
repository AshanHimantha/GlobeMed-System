
package system.service;



import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * A simple utility class for basic data encryption/decryption using Base64.
 * NOTE: This is for demonstration purposes and is not cryptographically secure for production.
 */
public class EncryptionUtil {

    public static String encrypt(String data) {
        if (data == null) {
            return null;
        }
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decrypt(String encryptedData) {
        if (encryptedData == null) {
            return null;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}