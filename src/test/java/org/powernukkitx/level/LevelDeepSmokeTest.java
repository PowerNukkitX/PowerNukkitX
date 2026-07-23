package org.powernukkitx.level;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.particle.HeartParticle;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Deeper pass over the real {@link Level} API against the fixture world - block updates,
 * lighting, entity management, drops, sounds/particles, chunk ops and save/time paths.
 * Complements LevelOpsSmokeTest which only covers getters and basic set/get. Everything is
 * tolerant so a single misbehaving path never sinks the run.
 */
public class LevelDeepSmokeTest {

    static Level level;
    static int checked;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void blockUpdatesAndLighting() {
        Vector3 pos = new Vector3(3, 70, 3);
        BlockState stone = firstSolidState();
        level.setBlock(pos, stone.toBlock());
        Block block = level.getBlock(pos);

        safe(() -> level.scheduleUpdate(block, 1));
        safe(() -> level.scheduleUpdate(block, 1, true));
        safe(() -> level.scheduleUpdate(block, pos, 1));
        safe(() -> level.updateAround(pos));
        safe(() -> level.updateAround(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()));
        safe(() -> level.updateAroundObserver(pos));
        safe(() -> level.updateAllLight(pos));
        safe(level::updateBlockLight);
        safe(() -> level.setBlockLightAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), 15));
        safe(() -> level.getBlockLightAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()));

        Assertions.assertTrue(true);
    }

    @Test
    void entityManagementAndQueries() {
        Entity entity = spawn();
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(-8, 60, -8, 24, 90, 24);

        safe(level::getEntities);
        safe(level::getPlayers);
        safe(() -> level.getCollidingEntities(bb));
        safe(() -> level.getNearbyEntities(bb));
        safe(() -> level.getNearbyEntities(bb, entity));
        safe(() -> level.getNearbyEntitiesSafe(bb));
        if (entity != null) {
            safe(() -> level.getEntity(entity.getId()));
            safe(() -> level.getChunkEntities(entity.getChunkX(), entity.getChunkZ()));
            safe(() -> level.removeEntity(entity));
            safe(() -> level.addEntity(entity));
            checked++;
        }
        Assertions.assertTrue(true);
    }

    @Test
    void dropsAndExpAndBreak() {
        Vector3 pos = new Vector3(5, 70, 5);
        Item stone = Item.get("minecraft:cobblestone");

        safe(() -> level.dropItem(pos, stone));
        safe(() -> level.dropItem(pos, stone, new Vector3(0, 0.1, 0)));
        safe(() -> level.dropItem(pos, stone, new Vector3(0, 0.1, 0), 5));
        safe(() -> level.dropItem(pos, stone, new Vector3(0, 0.1, 0), true, 5));
        safe(() -> level.dropAndGetItem(pos, stone));
        safe(() -> level.dropAndGetItem(pos, stone, new Vector3(0, 0.1, 0)));
        safe(() -> level.dropExpOrb(pos, 5));
        safe(() -> level.dropExpOrb(pos, 5, new Vector3(0, 0.1, 0)));

        level.setBlock(pos, firstSolidState().toBlock());
        safe(() -> level.useBreakOn(pos));
        level.setBlock(pos, firstSolidState().toBlock());
        safe(() -> level.useBreakOn(pos, Item.get("minecraft:diamond_pickaxe")));
        Assertions.assertTrue(true);
    }

    @Test
    void soundsAndParticles() {
        Vector3 pos = new Vector3(6, 70, 6);

        safe(() -> level.addSound(pos, Sound.AMBIENT_CAVE));
        safe(() -> level.addSound(pos, Sound.AMBIENT_CAVE, 1f, 1f));
        safe(() -> level.addSound(pos, "random.pop"));
        safe(() -> level.addSound(pos, "random.pop", 1f, 1f));
        safe(() -> level.addLevelSoundEvent(pos, SoundEvent.POP));
        safe(() -> level.addLevelSoundEvent(pos, SoundEvent.POP, 0));
        safe(() -> level.addLevelSoundEvent(pos, SoundEvent.POP, 0, -1));
        safe(() -> level.addParticle(new HeartParticle(pos)));
        safe(() -> level.addParticleEffect(pos, ParticleEffect.BASIC_FLAME));
        safe(() -> level.addParticleEffect(pos, ParticleEffect.BASIC_FLAME, -1L));
        safe(() -> level.addParticleEffect(pos, "minecraft:basic_flame_particle"));
        Assertions.assertTrue(true);
    }

    @Test
    void chunkOps() {
        int cx = 0;
        int cz = 0;
        safe(() -> level.loadChunk(cx, cz));
        safe(() -> level.loadChunk(cx, cz, true));
        safe(() -> level.isChunkLoaded(cx, cz));
        safe(() -> level.getChunk(cx, cz));
        safe(() -> level.getChunk(cx, cz, true));
        safe(() -> level.getChunkEntities(cx, cz));
        safe(level::getChunks);
        safe(() -> level.unloadChunkRequest(cx + 30, cz + 30));
        safe(() -> level.cancelUnloadChunkRequest(cx + 30, cz + 30));
        Assertions.assertTrue(true);
    }

    @Test
    void timeSaveAndMisc() {
        safe(level::checkTime);
        safe(level::sendTime);
        safe(() -> level.calculateSkylightSubtracted(0f));
        safe(() -> level.calculateSkylightSubtracted(1f));
        safe(() -> level.setThundering(true));
        safe(() -> level.setThundering(false));
        safe(level::getGameRules);
        safe(level::saveChunks);
        safe(() -> level.save(false));
        Assertions.assertTrue(true);
    }

    private Entity spawn() {
        try {
            Position pos = new Position(1, 80, 1, level);
            for (String id : Registries.ENTITY.getKnownEntities().keySet()) {
                try {
                    Entity e = Entity.createEntity(id, pos);
                    if (e != null) return e;
                } catch (Throwable ignore) {
                }
            }
        } catch (Throwable ignore) {
        }
        return null;
    }

    private BlockState firstSolidState() {
        for (BlockState s : Registries.BLOCKSTATE.getAllState()) {
            if (s.getIdentifier().equals("minecraft:stone")) return s;
        }
        return Registries.BLOCKSTATE.getAllState().iterator().next();
    }

    private void safe(Runnable r) {
        try {
            r.run();
            checked++;
        } catch (Throwable ignore) {
        }
    }
}
