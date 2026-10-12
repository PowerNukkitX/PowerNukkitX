package org.powernukkitx.level.util;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.DimensionEnum;

import java.nio.charset.StandardCharsets;

/**
 * Allay Project 8/22/2023
 * PowerNukkitX Project 19/09/2026
 *
 * @author Cool_Loong | Cloudburst Server
 * @author Curse | PowerNukkitX
 */
public enum LevelDBKeyUtil {
    /**
     * Legacy BDS Data2D chunk data.
     * <p>
     * Stores the pre-1.18 heightmap and 2D biome IDs.
     * Modern BDS worlds use {@link #DATA_3D} instead.
     */
    DATA_2D('-'),
    /**
     * Biomes are stored as palettes similar to blocks. One biome palette is written for each vertical chunk section.
     * Biome IDs are written as integers.
     */
    DATA_3D('+'),
    /**
     * Version levelDB key after v1.16.100
     */
    VERSION(','),
    /**
     * Legacy BDS Data2D chunk data.
     * <p>
     * Each biome entry contains the biome ID followed by RGB color data.
     * This format predates the newer Data2D and Data3D formats and is no
     * longer written by modern BDS versions.
     */
    DATA_2D_LEGACY('.'),
    /**
     * Block data for a 16×16×16 chunk section
     */
    CHUNK_SECTION_PREFIX('/'),
    /**
     * Legacy BDS terrain data.
     * <p>
     * Stores terrain in XZY order together with legacy biome ID and RGB
     * color information. This format predates the modern SubChunk and
     * Data3D storage formats and is no longer written by modern BDS.
     */
    LEGACY_TERRAIN('0'),
    /**
     * Block entity data (little-endian NBT)
     */
    BLOCK_ENTITIES('1'),
    /**
     * Entity data (little-endian NBT)
     */
    ENTITIES('2'),
    /**
     * Pending tick data (little-endian NBT)
     */
    PENDING_TICKS('3'),
    /**
     * Array of blocks that appear in the same place as other blocks. Used for grass appearing inside snow layers prior to v1.2.13. No longer written as of v1.2.13.
     */
    BLOCK_EXTRA_DATA('4'),
    BIOME_STATE('5'),
    /**
     * Random tick data (little-endian NBT)
     */
    RANDOM_TICKS(':'),
    CHUNK_FINALIZED_STATE('6'),
    /**
     * Legacy BDS chunk conversion data.
     */
    CONVERSION_DATA('7'),
    /**
     * Education Edition Feature
     */
    BORDER_BLOCKS('8'),
    /**
     * Bounding boxes for structure spawns stored in binary format
     */
    HARDCODED_SPAWNERS('9'),
    /**
     * Legacy BDS chunk checksums.
     */
    CHECKSUMS(';'),
    /**
     * Reserved BDS chunk generation seed.
     */
    GENERATION_SEED('<'),
    /**
     * Legacy BDS pre-Caves & Cliffs blending state.
     */
    GENERATED_PRE_CAVES_AND_CLIFFS_BLENDING('='),
    /**
     * Legacy BDS blending biome height data.
     */
    BLENDING_BIOME_HEIGHT('>'),
    /**
     * References an entry in the global LevelChunkMetaDataDictionary.
     * The value is an 8-byte little-endian XXH64 metadata hash.
     */
    LEVEL_CHUNK_METADATA('?'),
    /**
     * Stores chunk blending state.
     * Byte 0: whether the chunk is used for blending.
     * Byte 1: blend version when saved.
     */
    CHUNK_BLENDING_DATA('@'),
    /**
     * Stores the actor digest format version used by modern actor storage.
     */
    ACTOR_DIGEST_VERSION('A'),
    LEGACY_VERSION('v'),
    /**
     * Modern BDS per-chunk AABB volume data used by structure spawning.
     */
    AABB_VOLUMES('w'),
    /**
     * Stores PNX-defined extra data,BIG BYTE_ORDER NBT FORMAT
     */
    PNX_EXTRA_DATA('|');

    /**
     * Reserved BDS global keys/prefixes that PNX must not repurpose.
     */
    public static final String REALMS_STORIES_DATA_PREFIX = "RealmsStoriesData_";
    public static final String LOCAL_PLAYER_KEY = "~local_player";
    public static final String PLAYER_PREFIX = "player_";
    public static final String PLAYER_SERVER_PREFIX = "player_server_";
    public static final String LEGACY_CONSOLE_PLAYER_PREFIX = "legacy_console_player_";

    private final byte encoded;

    LevelDBKeyUtil(char encoded) {
        this.encoded = (byte) encoded;
    }

    public byte[] getKey(int chunkX, int chunkZ) {
        return new byte[]{
                (byte) (chunkX & 0xff),
                (byte) ((chunkX >>> 8) & 0xff),
                (byte) ((chunkX >>> 16) & 0xff),
                (byte) ((chunkX >>> 24) & 0xff),
                (byte) (chunkZ & 0xff),
                (byte) ((chunkZ >>> 8) & 0xff),
                (byte) ((chunkZ >>> 16) & 0xff),
                (byte) ((chunkZ >>> 24) & 0xff),
                this.encoded
        };
    }

    public byte[] getKey(int chunkX, int chunkZ, DimensionData dimension) {
        if (dimension.equals(DimensionEnum.OVERWORLD.getDimensionData())) {
            return new byte[]{
                    (byte) (chunkX & 0xff),
                    (byte) ((chunkX >>> 8) & 0xff),
                    (byte) ((chunkX >>> 16) & 0xff),
                    (byte) ((chunkX >>> 24) & 0xff),
                    (byte) (chunkZ & 0xff),
                    (byte) ((chunkZ >>> 8) & 0xff),
                    (byte) ((chunkZ >>> 16) & 0xff),
                    (byte) ((chunkZ >>> 24) & 0xff),
                    this.encoded
            };
        } else {
            byte dimensionId = (byte) dimension.getDimensionId();
            return new byte[]{
                    (byte) (chunkX & 0xff),
                    (byte) ((chunkX >>> 8) & 0xff),
                    (byte) ((chunkX >>> 16) & 0xff),
                    (byte) ((chunkX >>> 24) & 0xff),
                    (byte) (chunkZ & 0xff),
                    (byte) ((chunkZ >>> 8) & 0xff),
                    (byte) ((chunkZ >>> 16) & 0xff),
                    (byte) ((chunkZ >>> 24) & 0xff),
                    (byte) (dimensionId & 0xff),
                    (byte) ((dimensionId >>> 8) & 0xff),
                    (byte) ((dimensionId >>> 16) & 0xff),
                    (byte) ((dimensionId >>> 24) & 0xff),
                    this.encoded
            };
        }
    }

    public byte[] getKey(int chunkX, int chunkZ, int chunkSectionY) {
        if (this.encoded != CHUNK_SECTION_PREFIX.encoded)
            throw new IllegalArgumentException("The method must be used with CHUNK_SECTION_PREFIX!");
        return new byte[]{
                (byte) (chunkX & 0xff),
                (byte) ((chunkX >>> 8) & 0xff),
                (byte) ((chunkX >>> 16) & 0xff),
                (byte) ((chunkX >>> 24) & 0xff),
                (byte) (chunkZ & 0xff),
                (byte) ((chunkZ >>> 8) & 0xff),
                (byte) ((chunkZ >>> 16) & 0xff),
                (byte) ((chunkZ >>> 24) & 0xff),
                this.encoded,
                (byte) chunkSectionY
        };
    }

    public byte[] getKey(int chunkX, int chunkZ, int chunkSectionY, DimensionData dimension) {
        if (dimension.equals(DimensionEnum.OVERWORLD.getDimensionData())) {
            return new byte[]{
                    (byte) (chunkX & 0xff),
                    (byte) ((chunkX >>> 8) & 0xff),
                    (byte) ((chunkX >>> 16) & 0xff),
                    (byte) ((chunkX >>> 24) & 0xff),
                    (byte) (chunkZ & 0xff),
                    (byte) ((chunkZ >>> 8) & 0xff),
                    (byte) ((chunkZ >>> 16) & 0xff),
                    (byte) ((chunkZ >>> 24) & 0xff),
                    this.encoded,
                    (byte) chunkSectionY
            };
        } else {
            byte dimensionId = (byte) dimension.getDimensionId();
            return new byte[]{
                    (byte) (chunkX & 0xff),
                    (byte) ((chunkX >>> 8) & 0xff),
                    (byte) ((chunkX >>> 16) & 0xff),
                    (byte) ((chunkX >>> 24) & 0xff),
                    (byte) (chunkZ & 0xff),
                    (byte) ((chunkZ >>> 8) & 0xff),
                    (byte) ((chunkZ >>> 16) & 0xff),
                    (byte) ((chunkZ >>> 24) & 0xff),
                    (byte) (dimensionId & 0xff),
                    (byte) ((dimensionId >>> 8) & 0xff),
                    (byte) ((dimensionId >>> 16) & 0xff),
                    (byte) ((dimensionId >>> 24) & 0xff),
                    this.encoded,
                    (byte) chunkSectionY
            };
        }
    }

    public byte[] getGlobalKey() {
        return new byte[] { this.encoded };
    }

    public static byte[] getGlobalKey(String key) {
        return key.getBytes(StandardCharsets.UTF_8);
    }
}
