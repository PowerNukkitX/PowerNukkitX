package org.powernukkitx.level.format.leveldb;

import org.junit.jupiter.api.Test;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BDSEntityTranslatorTest {
    @Test
    void translatesPositionAndMotionIntoSeparateLists() {
        CompoundTag source = new CompoundTag()
                .putList("Pos", new ListTag<FloatTag>()
                        .add(new FloatTag(1.25))
                        .add(new FloatTag(64.5))
                        .add(new FloatTag(-3.75)))
                .putList("Motion", new ListTag<FloatTag>()
                        .add(new FloatTag(0.125))
                        .add(new FloatTag(-0.25))
                        .add(new FloatTag(0.5)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(90))
                        .add(new FloatTag(15)));

        CompoundTag translated = BDSEntityTranslator.translate(source);

        assertEquals(
                List.of(1.25, 64.5, -3.75),
                translated.getList("Pos", DoubleTag.class).getAll().stream()
                        .map(DoubleTag::getData)
                        .toList()
        );
        assertEquals(
                List.of(0.125, -0.25, 0.5),
                translated.getList("Motion", DoubleTag.class).getAll().stream()
                        .map(DoubleTag::getData)
                        .toList()
        );
    }
}
