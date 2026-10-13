package org.powernukkitx;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * Builds and reads byte keys used by the server player-data database. Centralizing these layouts keeps player, custom,
 * PNX extra, and name-index records encoded consistently.
 *
 * @author Curse
 */
final class PlayerDataKeys {

    private PlayerDataKeys() {
    }

    static byte[] player(UUID uuid) {
        return uuidKey(ServerDBStorageFormat.SERVER_DATA_PLAYER_PREFIX, uuid);
    }

    static byte[] pnxExtra(UUID uuid) {
        return uuidKey(ServerDBStorageFormat.SERVER_DATA_PLAYER_PNX_EXTRA_PREFIX, uuid);
    }

    static byte[] custom(UUID uuid) {
        return uuidKey(ServerDBStorageFormat.SERVER_DATA_PLAYER_CUSTOM_PREFIX, uuid);
    }

    static byte[] name(String name) {
        byte[] value = name.toLowerCase(Locale.ENGLISH).getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(ServerDBStorageFormat.SERVER_DATA_PLAYER_NAME_PREFIX.length + value.length);
        buffer.put(ServerDBStorageFormat.SERVER_DATA_PLAYER_NAME_PREFIX);
        buffer.put(value);
        return buffer.array();
    }

    static boolean isName(byte[] key) {
        byte[] prefix = ServerDBStorageFormat.SERVER_DATA_PLAYER_NAME_PREFIX;

        if (key.length <= prefix.length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (key[i] != prefix[i]) {
                return false;
            }
        }

        return true;
    }

    static String readName(byte[] key) {
        return new String(
            key,
            ServerDBStorageFormat.SERVER_DATA_PLAYER_NAME_PREFIX.length,
            key.length - ServerDBStorageFormat.SERVER_DATA_PLAYER_NAME_PREFIX.length,
            StandardCharsets.UTF_8
        );
    }

    private static byte[] uuidKey(byte[] prefix, UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(prefix.length + 16);
        buffer.put(prefix);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }
}
