package cn.nukkit.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for generating SHA-256, SHA-512, and MD5 hashes.
 */
@UtilityClass
public class SHAUtil {
    /**
     * Returns the SHA-256 hash of the input string.
     *
     * @param input the input string
     * @return the SHA-256 hash in hexadecimal, or an empty string if input is null/empty
     */
    public String sha256(final String input) {
        return hash(input, "SHA-256");
    }

    /**
     * Returns the SHA-512 hash of the input string.
     *
     * @param input the input string
     * @return the SHA-512 hash in hexadecimal, or an empty string if input is null/empty
     */
    public String sha512(final String input) {
        return hash(input, "SHA-512");
    }

    /**
     * Returns the MD5 hash of the input string.
     *
     * @param input the input string
     * @return the MD5 hash in hexadecimal, or an empty string if input is null/empty
     */
    public String md5(final String input) {
        return hash(input, "MD5");
    }

    /**
     * Computes the hash of the input string using the specified algorithm.
     *
     * @param input     the input string
     * @param algorithm the hash algorithm (e.g., "SHA-256")
     * @return the hash in hexadecimal, or an empty string if input is null/empty
     * @throws IllegalArgumentException if the algorithm is not supported
     */
    private String hash(final String input, final String algorithm) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported hash algorithm: " + algorithm, e);
        }
    }
}
