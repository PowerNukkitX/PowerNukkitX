package org.powernukkitx.entity.passive;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestEventHandler;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockTurtleEgg;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.event.entity.EntityBlockChangeEvent;
import org.powernukkitx.event.entity.ItemSpawnEvent;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.block.property.enums.TurtleEggCount;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class EntityTurtleTest {
    private static Level level;
    private static final AtomicLong ids = new AtomicLong(-3L << 32);
    private final List<EntityTurtle> turtles = new ArrayList<>();

    @BeforeAll
    static void boot() throws Exception {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        level.getChunk(0, 0, true);
    }

    @AfterEach
    void cleanup() {
        ServerMockFixture.pluginManager.resetAll();
        turtles.forEach(Entity::close);
        level.setBlock(new Vector3(8, 99, 8), Block.get(BlockID.AIR), true, false);
        level.setBlock(new Vector3(8, 100, 8), Block.get(BlockID.AIR), true, false);
        level.releaseTickCachedBlocks();
    }

    private EntityTurtle turtle() {
        return turtle(false);
    }

    private EntityTurtle turtle(boolean laying) {
        CompoundTag nbt = Entity.getDefaultNBT(new Vector3(8.5, 100, 8.5))
                .putLong("UniqueID", ids.incrementAndGet())
                .putBoolean("IsPregnant", laying)
                .putBoolean("WantsToLayEgg", laying)
                .putList("HomePos", new ListTag<FloatTag>()
                        .add(new FloatTag(8)).add(new FloatTag(100)).add(new FloatTag(8)));
        EntityTurtle turtle = new EntityTurtle(level.getChunk(0, 0), nbt);
        turtles.add(turtle);
        turtle.onGround = true;
        return turtle;
    }

    private void tick(EntityTurtle turtle, int count) {
        for (int i = 0; i < count; i++) {
            turtle.getBehaviorGroup().evaluateBehaviors(turtle);
            turtle.getBehaviorGroup().tickRunningBehaviors(turtle);
        }
    }

    private void sand() {
        level.setBlock(new Vector3(8, 99, 8), Block.get(BlockID.SAND), true, false);
    }

    @Test
    void persistsBirthplaceAndPregnancy() {
        EntityTurtle turtle = turtle();
        Vector3 home = new Vector3(123, 65, -456);
        turtle.setHomePos(home);
        home.x = 999;
        turtle.setPregnant(true);
        turtle.saveNBT();
        CompoundTag saved = turtle.getNbt().copy();
        turtle.close();
        EntityTurtle restored = new EntityTurtle(level.getChunk(0, 0), saved);
        turtles.add(restored);
        assertTrue(restored.isPregnant());
        assertTrue(restored.hasHome());
        assertNull(restored.getHomePosition());
        restored.saveNBT();
        assertEquals(123f, restored.getNbt().getList("HomePos", FloatTag.class).get(0).data);
        assertEquals(-456f, restored.getNbt().getList("HomePos", FloatTag.class).get(2).data);
    }

    @Test
    void breedingAgeInitializesBabyGrowthAndBedrockCollisionSize() {
        EntityTurtle turtle = turtle();
        turtle.setBreedingAge(-24000);
        assertTrue(turtle.isBaby());
        assertEquals(24000, turtle.getTicksGrowLeft());
        assertEquals(0.16f, turtle.getScale());
        assertEquals(0.64f, turtle.getWidth() * turtle.getScale(), 0.0001f);
        assertEquals(0.213333f, turtle.getHeight() * turtle.getScale(), 0.0001f);
        assertEquals(0.06f, turtle.getDefaultUnderWaterSpeed());
        assertEquals(0, turtle.getDrops(Item.AIR).length);
        assertTrue(turtle.typeFamily().contains("aquatic"));
        turtle.calculateOffsetBoundingBox();
        var box = turtle.getOffsetBoundingBox();
        assertEquals(0.64, box.getMaxX() - box.getMinX(), 0.0001);
        assertEquals(0.213333, box.getMaxY() - box.getMinY(), 0.0001);
    }

    @Test
    void pregnantTurtleLaysOneToFourEggsAndClearsPregnancy() {
        sand();
        EntityTurtle turtle = turtle(true);
        tick(turtle, 205);
        Block block = level.getBlock(8, 100, 8);
        assertInstanceOf(BlockTurtleEgg.class, block);
        int eggs = ((BlockTurtleEgg) block).getEggCount().ordinal() + 1;
        assertTrue(eggs >= 1 && eggs <= 4);
        assertFalse(turtle.isPregnant());
        assertFalse(turtle.getDataFlag(ActorFlags.LAYING_EGG));
    }

    @Test
    void cancelledEggPlacementKeepsPregnancy() {
        sand();
        ServerMockFixture.pluginManager.registerTestEventHandler(List.of(new TestEventHandler<EntityBlockChangeEvent>() {
            @Override
            public void handle(EntityBlockChangeEvent event) {
                event.setCancelled();
            }
        }));
        EntityTurtle turtle = turtle(true);
        tick(turtle, 205);
        assertTrue(turtle.isPregnant());
        assertTrue(level.getBlock(8, 100, 8).isAir());
        assertFalse(turtle.getDataFlag(ActorFlags.LAYING_EGG));
    }

    @Test
    void removedSandStopsDiggingWithoutLosingEggs() {
        sand();
        EntityTurtle turtle = turtle(true);
        tick(turtle, 100);
        assertTrue(turtle.getDataFlag(ActorFlags.LAYING_EGG));
        level.setBlock(new Vector3(8, 99, 8), Block.get(BlockID.STONE), true, false);
        tick(turtle, 120);
        assertTrue(turtle.isPregnant());
        assertFalse(turtle.getDataFlag(ActorFlags.LAYING_EGG));
        assertTrue(level.getBlock(8, 100, 8).isAir());
    }

    @Test
    void reproductionMakesOneParentPregnantInsteadOfSpawningBaby() {
        EntityTurtle first = turtle();
        EntityTurtle second = turtle();
        first.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, true);
        second.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, true);
        tick(first, 65);
        assertTrue(first.isPregnant() ^ second.isPregnant());
        assertFalse(first.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE));
        assertFalse(second.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE));
    }

    @Test
    void turtleCollisionCannotBreakEggs() {
        EntityTurtle turtle = turtle();
        BlockTurtleEgg eggs = new BlockTurtleEgg();
        level.setBlock(new Vector3(8, 100, 8), eggs, true, false);
        Block placed = level.getBlock(8, 100, 8);
        turtle.setPosition(new Vector3(8.5, 101, 8.5));
        for (int i = 0; i < 1000; i++) placed.onEntityCollide(turtle);
        assertInstanceOf(BlockTurtleEgg.class, level.getBlock(8, 100, 8));
    }

    @Test
    void growthPausesAndDropsOneScuteOnAdulthood() {
        AtomicInteger scutes = new AtomicInteger();
        ServerMockFixture.pluginManager.registerTestEventHandler(List.of(new TestEventHandler<ItemSpawnEvent>() {
            @Override
            public void handle(ItemSpawnEvent event) {
                if (event.getEntity().getItem().getId().equals(ItemID.TURTLE_SCUTE)) {
                    scutes.addAndGet(event.getEntity().getItem().getCount());
                }
            }
        }));
        EntityTurtle turtle = turtle();
        turtle.setBreedingAge(-2);
        assertTrue(turtle.tryGrow(null, Item.get(BlockID.GOLDEN_DANDELION)));
        for (int i = 0; i < 5; i++) {
            turtle.entityBaseTick(1);
        }
        assertEquals(2, turtle.getTicksGrowLeft());
        assertTrue(turtle.tryGrow(null, Item.get(BlockID.GOLDEN_DANDELION)));
        assertFalse(turtle.isGrowthPaused());
        assertEquals(24000, turtle.getTicksGrowLeft());
        turtle.setBreedingAge(-2);
        for (int i = 0; i < 5; i++) {
            turtle.entityBaseTick(1);
        }
        assertFalse(turtle.isBaby());
        assertEquals(1, scutes.get());
    }

    @Test
    void eggsHatchIntoBabiesWithBirthplaceAndGrowthState() {
        sand();
        BlockTurtleEgg eggs = new BlockTurtleEgg();
        eggs.setEggCount(TurtleEggCount.TWO_EGG);
        level.setBlock(new Vector3(8, 100, 8), eggs, true, false);
        Set<Entity> before = Set.of(level.getEntities());
        ((BlockTurtleEgg) level.getBlock(8, 100, 8)).hatch();
        List<EntityTurtle> hatched = Arrays.stream(level.getEntities())
                .filter(e -> !before.contains(e)).filter(EntityTurtle.class::isInstance)
                .map(EntityTurtle.class::cast).toList();
        turtles.addAll(hatched);
        assertEquals(2, hatched.size());
        for (EntityTurtle turtle : hatched) {
            assertTrue(turtle.isBaby());
            assertEquals(24000, turtle.getTicksGrowLeft());
            turtle.saveNBT();
            assertEquals(8f, turtle.getNbt().getList("HomePos", FloatTag.class).get(0).data);
        }
        assertTrue(level.getBlock(8, 100, 8).isAir());
    }

    @Test
    void amphibiousRouteConnectsWaterToDryLand() {
        EntityTurtle turtle = turtle(true);
        try {
            for (int x = 6; x <= 11; x++) {
                for (int z = 6; z <= 10; z++) {
                    level.setBlock(new Vector3(x, 99, z), Block.get(x <= 8 ? BlockID.WATER : BlockID.SAND), true, false);
                    level.setBlock(new Vector3(x, 100, z), Block.get(BlockID.AIR), true, false);
                }
            }
            level.releaseTickCachedBlocks();
            turtle.setPosition(new Vector3(8.5, 99.5, 8.5));
            tick(turtle, 1);
            assertNotNull(turtle.getMoveTarget());
            assertFalse(turtle.getDataFlag(ActorFlags.LAYING_EGG));
            turtle.setMoveDirectionStart(turtle.clone());
            turtle.setMoveDirectionEnd(turtle.clone());
            turtle.setShouldUpdateMoveDirection(false);
            turtle.getBehaviorGroup().applyController(turtle);
            assertTrue(turtle.isShouldUpdateMoveDirection());
            assertTrue(Double.isFinite(turtle.motionX));
            assertTrue(Double.isFinite(turtle.motionY));
            assertTrue(Double.isFinite(turtle.motionZ));
            EntityTurtle swimming = turtle();
            swimming.setBreedingAge(-24000);
            swimming.setPosition(new Vector3(7.5, 99.5, 8.5));
            tick(swimming, 1);
            assertNotNull(swimming.getMoveTarget());
            assertTrue(swimming.distanceSquared(swimming.getMoveTarget()) >= 1);
            var finder = turtle.getBehaviorGroup().getRouteFinder();
            finder.setStart(new Vector3(7.5, 99.5, 8.5));
            finder.setTarget(new Vector3(10.5, 100.5, 8.5));
            assertTrue(finder.search());
            assertTrue(finder.isReachable());
            assertFalse(finder.getRoute().isEmpty());
            finder.setStart(new Vector3(10.5, 100.5, 8.5));
            finder.setTarget(new Vector3(7.5, 99.5, 8.5));
            assertTrue(finder.search());
            assertTrue(finder.isReachable());
            EntityTurtle entering = turtle();
            entering.setPosition(new Vector3(9.5, 100, 8.5));
            tick(entering, 1);
            assertNotNull(entering.getMoveTarget());
            assertTrue(entering.getMoveTarget().x <= 7.5);
        } finally {
            for (int x = 6; x <= 11; x++) {
                for (int z = 6; z <= 10; z++) {
                    level.setBlock(new Vector3(x, 99, z), Block.get(BlockID.AIR), true, false);
                    level.setBlock(new Vector3(x, 100, z), Block.get(BlockID.AIR), true, false);
                }
            }
            level.releaseTickCachedBlocks();
        }
    }
}
