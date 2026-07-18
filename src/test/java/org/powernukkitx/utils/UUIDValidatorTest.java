package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UUIDValidatorTest {
    @Test
    void validUUID() {
        Assertions.assertTrue(UUIDValidator.isValidUUID(UUID.randomUUID().toString()));
        Assertions.assertTrue(UUIDValidator.isValidUUID("123e4567-e89b-12d3-a456-426614174000"));
        Assertions.assertTrue(UUIDValidator.isValidUUID("123E4567-E89B-12D3-A456-426614174000"));
    }

    @Test
    void invalidUUID() {
        Assertions.assertFalse(UUIDValidator.isValidUUID(null));
        Assertions.assertFalse(UUIDValidator.isValidUUID(""));
        Assertions.assertFalse(UUIDValidator.isValidUUID("not-a-uuid"));
        Assertions.assertFalse(UUIDValidator.isValidUUID("123e4567e89b12d3a456426614174000"));
        Assertions.assertFalse(UUIDValidator.isValidUUID("123e4567-e89b-12d3-a456-42661417400g"));
    }
}
