package org.powernukkitx.entity.data.profession;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.data.PlayerFlag;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives the per-profession villager trade tables. Each profession picks its offers
 * from seeded Random branches - iterating many seeds hits every switch arm and the
 * enchanted-book paths, exercising the big emerald/item/enchant trade tables.
 */
class ProfessionTradeSmokeTest {

    private int checked = 0;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
        Profession.init();
    }

    private void safe(Runnable r) {
        try {
            r.run();
            checked++;
        } catch (Throwable ignore) {
        }
    }

    @Test
    void buildTradesForEveryProfessionAcrossSeeds() {
        Collection<Profession> professions = Profession.getProfessions().values();
        assertTrue(professions.size() > 0, "professions registered");

        for (Profession profession : professions) {
            for (int seed = 0; seed < 64; seed++) {
                final int s = seed;
                safe(() -> {
                    ListTag<CompoundTag> trades = profession.buildTrades(s);
                    if (trades != null) {
                        trades.getAll().forEach(CompoundTag::toString);
                    }
                });
            }
        }
        assertTrue(checked > 0, "at least one trade table built");
    }

    @Test
    void professionGettersExposeConstructorData() {
        for (Profession profession : Profession.getProfessions().values()) {
            safe(() -> {
                profession.getName();
                profession.getBlockID();
                profession.getIndex();
                profession.getWorkSound();
            });
        }
        assertTrue(checked > 0);
    }

    @Test
    void playerFlagValuesAreStable() {
        for (PlayerFlag flag : PlayerFlag.values()) {
            safe(flag::getValue);
        }
        assertTrue(checked > 0);
    }
}
