package org.powernukkitx.level.format.leveldb;

import com.google.common.base.Preconditions;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Normalizes copied LevelDB test fixtures without relaxing runtime storage validation.
 *
 * @author Curse
 */
public final class LevelDBTestFixtureUtil {

    private LevelDBTestFixtureUtil() {
    }

    /**
     * Canonicalizes empty LimboEntities lists in a copied test world.
     */
    public static void canonicalizeCopiedWorld(Path worldPath) throws IOException {
        LevelDBStorage storage = new LevelDBStorage(1, worldPath.toString());
        try {
            canonicalizeLimboEntities(storage, "Overworld");
            canonicalizeLimboEntities(storage, "Nether");
            canonicalizeLimboEntities(storage, "TheEnd");
        } finally {
            storage.close();
        }
    }

    private static void canonicalizeLimboEntities(LevelDBStorage storage, String dimensionKey) {
        byte[] key = dimensionKey.getBytes(StandardCharsets.UTF_8);
        CompoundTag root = storage.readGlobalCompound(key);
        if (root == null || !root.containsCompound("data")) return;

        CompoundTag data = root.getCompound("data");
        if (!data.containsList("LimboEntities")) return;

        ListTag<?> limboEntities = data.getList("LimboEntities");
        if (limboEntities.type == Tag.TAG_Compound) return;

        Preconditions.checkState(
                limboEntities.size() == 0,
                "Test fixture %s LimboEntities has non-empty non-compound data",
                dimensionKey
        );

        CompoundTag updatedData = data.copy()
                .putList("LimboEntities", new ListTag<CompoundTag>(Tag.TAG_Compound));

        storage.writeGlobalCompound(key, root.copy().putCompound("data", updatedData));
    }
}
