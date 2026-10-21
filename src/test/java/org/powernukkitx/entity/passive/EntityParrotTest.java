package org.powernukkitx.entity.passive;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.packet.SetActorLinkPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetActorDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveActorAbsolutePacket;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.item.ItemMusicDisc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.Player;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestEventHandler;
import org.powernukkitx.block.Block;
import org.powernukkitx.blockentity.BlockEntityJukebox;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.ai.behaviorgroup.EmptyBehaviorGroup;
import org.powernukkitx.entity.ai.controller.ParrotFlightController;
import org.powernukkitx.entity.ai.executor.ParrotMovementExecutor;
import org.powernukkitx.entity.ai.route.posevaluator.ParrotPosEvaluator;
import org.powernukkitx.entity.ai.route.finder.impl.ParrotRouteFinder;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityVehicleEnterEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntityParrotTest {
    private static Level level;
    private static final AtomicInteger tick = new AtomicInteger();
    private static final AtomicLong actorIds = new AtomicLong(-2L << 32);
    private static int originalTick;
    private final List<Entity> entities = new ArrayList<>();

    @BeforeAll
    static void boot() throws Exception {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        level.getChunk(0, 0, true);
        originalTick = ServerMockFixture.server.getTick();
        ServerMockFixture.server.getLevelTickExecutor().submit(() ->
                doAnswer(invocation -> tick.get()).when(ServerMockFixture.server).getTick()).get();
    }

    @AfterAll
    static void restoreClock() throws Exception {
        ServerMockFixture.server.getLevelTickExecutor().submit(() ->
                doReturn(originalTick).when(ServerMockFixture.server).getTick()).get();
    }

    @AfterEach
    void cleanup() {
        ServerMockFixture.pluginManager.resetAll();
        level.releaseTickCachedBlocks();
        tick.set(0);
        for (Entity entity : entities) entity.close();
    }

    private EntityParrot parrot() {
        EntityParrot parrot = new EntityParrot(level.getChunk(0, 0, true),
                Entity.getDefaultNBT(new Vector3(8.5, 100, 8.5)).putLong("UniqueID", actorIds.incrementAndGet()));
        entities.add(parrot);
        return parrot;
    }

    private Player carrier() {
        Player player = spy(PlayerFixture.get());
        doReturn(true).when(player).isOnline();
        doReturn(true).when(player).isAlive();
        doReturn(false).when(player).isTouchingWater();
        doReturn(false).when(player).isSpectator();
        doReturn(false).when(player).isFlying();
        doReturn(false).when(player).isSneaking();
        doReturn(false).when(player).isSleeping();
        doReturn(false).when(player).isGliding();
        player.setPosition(new Vector3(8, 100, 8));
        player.motionY = 0;
        return player;
    }

    @Test
    void createsAiAndPersistsVariantAndSitting() {
        EntityParrot parrot = parrot();
        assertFalse(parrot.getBehaviorGroup() instanceof EmptyBehaviorGroup);
        assertTrue(parrot.getVariant() >= 0 && parrot.getVariant() <= 4);
        parrot.setVariant(3);
        parrot.setOwnerName("keeper");
        parrot.setSitting(true);
        parrot.saveNBT();
        CompoundTag saved = parrot.getNbt().copy();
        parrot.close();
        var restored = new EntityParrot(level.getChunk(0, 0), saved);
        entities.add(restored);
        assertEquals(3, restored.getVariant());
        assertTrue(restored.isSitting());
        assertTrue(restored.typeFamily().contains("parrot_tame"));
        assertTrue(restored.getDataFlag(ActorFlags.CAN_FLY));
    }

    @Test
    void sittingClearsFlightTarget() {
        EntityParrot parrot = parrot();
        parrot.setMoveTarget(parrot.add(3, 2, 0));
        parrot.setSitting(true);
        new ParrotMovementExecutor().execute(parrot);
        assertNull(parrot.getMoveTarget());
        assertFalse(new ParrotFlightController().control(parrot));
    }

    @Test
    void fallDamageDoesNotStartPanic() {
        EntityParrot parrot = parrot();
        assertFalse(parrot.attack(new EntityDamageEvent(parrot, EntityDamageEvent.DamageCause.FALL, 4)));
        assertEquals(6, parrot.getHealthCurrent());
        assertFalse(parrot.isPanicking());
    }

    @Test
    void damageUnsitsAndStartsPanic() {
        EntityParrot parrot = parrot();
        parrot.setSitting(true);
        assertTrue(parrot.attack(new EntityDamageEvent(parrot, EntityDamageEvent.DamageCause.CUSTOM, 1)));
        assertTrue(parrot.isPanicking());
        assertFalse(parrot.isSitting());
        parrot.setMovementSpeed(0.4f);
        new ParrotMovementExecutor().execute(parrot);
        assertEquals(0.5f, parrot.getMovementSpeed());
    }

    @Test
    void cancelledDamageDoesNotStartPanic() {
        ServerMockFixture.pluginManager.registerTestEventHandler(List.of(new TestEventHandler<EntityDamageEvent>() {
            @Override public void handle(EntityDamageEvent event) { event.setCancelled(); }
        }));
        EntityParrot parrot = parrot();
        parrot.setSitting(true);
        assertFalse(parrot.attack(new EntityDamageEvent(parrot, EntityDamageEvent.DamageCause.CUSTOM, 1)));
        assertFalse(parrot.isPanicking());
        assertTrue(parrot.isSitting());
    }

    @Test
    void shoulderSeatsHaveExactOffsetsAndRejectThirdPassenger() {
        Player player = carrier();
        EntityParrot left = parrot();
        EntityParrot right = parrot();
        EntityParrot third = parrot();
        for (EntityParrot parrot : List.of(left, right, third)) parrot.setTamed(true);
        assertTrue(player.mountEntity(left));
        assertTrue(player.mountEntity(right));
        verify(player, atLeastOnce()).sendPacket(argThat(packet -> packet instanceof SetActorLinkPacket link
                && link.getLink().getTargetB() == right.uniqueIdLong()
                && link.getLink().getType() == ActorLinkType.PASSENGER));
        verify(player, atLeastOnce()).sendPacket(argThat(packet -> packet instanceof SetActorDataPacket data
                && data.getTargetRuntimeID() == right.runtimeId()));
        assertFalse(player.mountEntity(third));
        assertSame(player, left.getRiding());
        assertEquals(0.4f, left.getSeatPosition(left).x);
        assertEquals(-0.4f, right.getSeatPosition(right).x);
        assertEquals(-0.2f, left.getSeatPosition(left).y);
        assertEquals(-0.1f, left.getSeatPosition(left).z);
        assertTrue(left.y > player.y + 1);
        assertTrue(player.dismountEntity(left));
        assertEquals(0.4f, right.getSeatPosition(right).x);
        player.dismountEntity(right);
    }

    @Test
    void remountAfterLastPassengerLeavesRestoresShoulderOffset() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        assertTrue(player.mountEntity(parrot));
        assertEquals(0.4f, parrot.getSeatPosition(parrot).x);
        assertTrue(player.dismountEntity(parrot));
        assertEquals(0f, parrot.getSeatPosition(parrot).x);
        assertTrue(player.mountEntity(parrot));
        assertEquals(0.4f, parrot.getSeatPosition(parrot).x);
        assertEquals(-0.2f, parrot.getSeatPosition(parrot).y);
        player.dismountEntity(parrot);
    }

    @Test
    void wildParrotCannotRideAndMountCancellationIsRespected() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        assertFalse(player.mountEntity(parrot));
        parrot.setTamed(true);
        ServerMockFixture.pluginManager.registerTestEventHandler(List.of(new TestEventHandler<EntityVehicleEnterEvent>() {
            @Override public void handle(EntityVehicleEnterEvent event) { event.setCancelled(); }
        }));
        assertFalse(player.mountEntity(parrot));
        assertNull(parrot.getRiding());
        assertTrue(player.getPassengers().isEmpty());
    }

    @Test
    void sittingAndUnsafeCarrierPreventShoulderSearch() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        assertFalse(parrot.canRideShoulder(player), "new parrots must wait before finding a mount");
        tick.set(100);
        assertTrue(parrot.canRideShoulder(player));
        parrot.setSitting(true);
        assertFalse(parrot.canRideShoulder(player));
        parrot.setSitting(false);
        player.motionY = -0.6;
        assertFalse(parrot.canRideShoulder(player));
        player.motionY = 0;
        doReturn(true).when(player).isTouchingWater();
        assertFalse(parrot.canRideShoulder(player));
    }

    @Test
    void mountedParrotDoesNotMoveUnderFlightPhysics() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        assertTrue(player.mountEntity(parrot));
        Vector3 before = parrot.getPosition();
        parrot.motionX = 0.5;
        parrot.motionY = -0.5;
        parrot.updateMovement();
        assertEquals(before, parrot.getPosition());
        assertEquals(0, parrot.motionY);
        player.dismountEntity(parrot);
    }

    @Test
    void flightMovementSendsGroundStateForWingAnimation() {
        EntityParrot parrot = parrot();
        Player observer = mock(Player.class);
        parrot.getViewers().put(123, observer);
        parrot.onGround = false;
        parrot.addMovement(parrot.x, parrot.y, parrot.z, 0, 0, 0);
        verify(observer).sendPacket(argThat(packet -> packet instanceof MoveActorAbsolutePacket move
                && !move.getMoveData().isOnGround()));
        clearInvocations(observer);
        parrot.onGround = true;
        parrot.addMovement(parrot.x, parrot.y, parrot.z, 0, 0, 0);
        verify(observer).sendPacket(argThat(packet -> packet instanceof MoveActorAbsolutePacket move
                && move.getMoveData().isOnGround()));
        parrot.getViewers().clear();
    }

    @Test
    void shoulderOffsetIsSentAfterMountLinkAndAfterSeatChanges() {
        Player player = carrier();
        EntityParrot left = parrot();
        EntityParrot right = parrot();
        left.setTamed(true);
        right.setTamed(true);
        assertTrue(player.mountEntity(left));
        clearInvocations(player);
        assertTrue(player.mountEntity(right));
        var ordered = inOrder(player);
        ordered.verify(player).sendPacket(argThat(packet -> packet instanceof SetActorLinkPacket link
                && link.getLink().getTargetB() == right.uniqueIdLong()));
        ordered.verify(player).sendPacket(argThat(packet -> packet instanceof SetActorDataPacket data
                && data.getTargetRuntimeID() == right.runtimeId()
                && data.getActorData().get(ActorDataTypes.SEAT_OFFSET).getX() == -0.4f));
        clearInvocations(player);
        assertTrue(player.dismountEntity(left));
        ordered = inOrder(player);
        ordered.verify(player).sendPacket(argThat(packet -> packet instanceof SetActorLinkPacket link
                && link.getLink().getTargetB() == right.uniqueIdLong()
                && link.getLink().getType() == ActorLinkType.RIDING));
        ordered.verify(player).sendPacket(argThat(packet -> packet instanceof SetActorDataPacket data
                && data.getTargetRuntimeID() == right.runtimeId()
                && data.getActorData().get(ActorDataTypes.SEAT_OFFSET).getX() == 0.4f));
        player.dismountEntity(right);
    }

    @Test
    void zeroLengthFlightDirectionDoesNotProduceNaN() {
        EntityParrot parrot = parrot();
        parrot.setMoveTarget(parrot.getPosition());
        parrot.setMoveDirectionStart(parrot.getPosition());
        parrot.setMoveDirectionEnd(parrot.getPosition());
        parrot.setShouldUpdateMoveDirection(false);
        assertFalse(new ParrotFlightController().control(parrot));
        assertTrue(Double.isFinite(parrot.motionX));
        assertTrue(Double.isFinite(parrot.motionY));
        assertNull(parrot.getMoveTarget());
    }

    @Test
    void horizontalFlightCompensatesGravityWithoutLosingAltitude() {
        EntityParrot parrot = parrot();
        Vector3 target = parrot.add(5, 0, 0);
        parrot.setMoveTarget(target);
        parrot.setMoveDirectionStart(parrot.getPosition());
        parrot.setMoveDirectionEnd(target);
        parrot.setShouldUpdateMoveDirection(false);
        parrot.motionX = 0;
        parrot.motionY = 0;
        parrot.motionZ = 0;
        assertTrue(new ParrotFlightController().control(parrot));
        parrot.handleGravity();
        assertEquals(0, parrot.motionY, 0.000001);
        assertTrue(parrot.motionX > 0);
    }

    @Test
    void flightCanLandAboveSolidBlocksAndAvoidsDangerousBlocks() {
        EntityParrot parrot = parrot();
        Vector3 feet = new Vector3(8.5, 100, 8.5);
        level.setBlock(feet.down(), Block.get(Block.STONE));
        level.setBlock(feet, Block.get(Block.AIR));
        level.setBlock(feet.up(), Block.get(Block.AIR));
        var evaluator = new ParrotPosEvaluator();
        assertTrue(evaluator.evalPos(parrot, feet));
        level.setBlock(feet.down(), Block.get(Block.MAGMA));
        level.releaseTickCachedBlocks();
        assertFalse(evaluator.evalPos(parrot, feet));
        level.setBlock(feet.down(), Block.get(Block.AIR));
        level.setBlock(feet, Block.get(Block.WATER));
        level.releaseTickCachedBlocks();
        assertFalse(evaluator.evalPos(parrot, feet));
        level.setBlock(feet, Block.get(Block.AIR));
        assertFalse(evaluator.evalPos(parrot, new Vector3(1000000, 100, 1000000)));
    }

    @Test
    void treeLandingSelectsLeavesWithTwoFreeBlocksAbove() {
        EntityParrot parrot = parrot();
        class PerchSearch extends ParrotMovementExecutor {
            Vector3 search(EntityParrot bird) { return findTreeLanding(bird); }
        }
        Vector3 leaves = new Vector3(7, 99, 8);
        level.setBlock(leaves, Block.get(Block.OAK_LEAVES));
        level.setBlock(leaves.up(), Block.get(Block.AIR));
        level.setBlock(leaves.up(2), Block.get(Block.AIR));
        level.releaseTickCachedBlocks();
        var search = new PerchSearch();
        try {
            assertEquals(new Vector3(7.5, 100, 8.5), search.search(parrot));
            level.setBlock(leaves.up(2), Block.get(Block.STONE));
            level.releaseTickCachedBlocks();
            assertNull(search.search(parrot));
        } finally {
            level.setBlock(leaves, Block.get(Block.AIR));
            level.setBlock(leaves.up(2), Block.get(Block.AIR));
            level.releaseTickCachedBlocks();
        }
    }

    @Test
    void jukeboxStartAndStopControlsDance() {
        EntityParrot parrot = parrot();
        level.setBlock(new Vector3(9, 100, 8), Block.get(Block.JUKEBOX));
        BlockEntityJukebox jukebox = new BlockEntityJukebox(level.getChunk(0, 0),
                new CompoundTag().putString("id", "Jukebox").putInt("x", 9).putInt("y", 100).putInt("z", 8));
        try {
            jukebox.setRecordItem(Item.get("minecraft:music_disc_cat"));
            jukebox.play();
            assertTrue(jukebox.isPlaying());
            assertTrue(parrot.isDancing());
            new ParrotMovementExecutor().execute(parrot);
            assertNull(parrot.getMoveTarget());
            jukebox.stop();
            assertFalse(parrot.isDancing());
        } finally {
            jukebox.close();
            level.setBlock(new Vector3(9, 100, 8), Block.get(Block.AIR));
        }
    }
    @Test
    void followsOwnerWithStartAndStopHysteresis() {
        EntityParrot parrot = parrot();
        Player owner = carrier();
        parrot.setOwnerName(owner.getName());
        parrot.getMemoryStorage().put(CoreMemoryTypes.OWNER, owner);
        var executor = new ParrotMovementExecutor();
        owner.setPosition(parrot.add(6, 0, 0));
        executor.execute(parrot);
        assertNotNull(parrot.getMoveTarget());
        owner.setPosition(parrot.add(3, 0, 0));
        executor.execute(parrot);
        assertNotNull(parrot.getMoveTarget(), "following must continue inside the start distance");
        owner.setPosition(parrot.add(0.5, 0, 0));
        executor.execute(parrot);
        assertNull(parrot.getMoveTarget());
        owner.setPosition(parrot.add(3, 0, 0));
        executor.execute(parrot);
        assertNull(parrot.getMoveTarget(), "following must not restart before five blocks");
    }

    @Test
    void teleportsToDistantOwnerOnlyWhilePanicking() {
        EntityParrot parrot = parrot();
        Player owner = carrier();
        level.getChunk(1, 0, true);
        parrot.setPosition(new Vector3(1, 100, 8.5));
        owner.setPosition(new Vector3(15, 100, 8.5));
        parrot.setOwnerName(owner.getName());
        parrot.getMemoryStorage().put(CoreMemoryTypes.OWNER, owner);
        parrot.onUpdate(1);
        assertTrue(parrot.distanceSquared(owner) > 144);
        parrot.attack(new EntityDamageEvent(parrot, EntityDamageEvent.DamageCause.CUSTOM, 1));
        parrot.onUpdate(2);
        assertTrue(parrot.distanceSquared(owner) < 25);
    }

    @Test
    void nativeRecordDurationsCoverEveryRegisteredDisc() {
        for (String id : new String[]{"13", "cat", "blocks", "chirp", "far", "mall", "mellohi",
                "stal", "strad", "ward", "11", "wait", "pigstep", "otherside", "5", "relic",
                "creator", "creator_music_box", "precipice", "bounce", "lava_chicken"}) {
            ItemMusicDisc disc = assertInstanceOf(ItemMusicDisc.class, Item.get("minecraft:music_disc_" + id));
            assertTrue(disc.getDurationTicks() > 0, id);
        }
        assertEquals(3700, ((ItemMusicDisc) Item.get("minecraft:music_disc_cat")).getDurationTicks());
        assertEquals(2680, ((ItemMusicDisc) Item.get("minecraft:music_disc_bounce")).getDurationTicks());
        assertEquals(2680, ((ItemMusicDisc) Item.get("minecraft:music_disc_lava_chicken")).getDurationTicks());
    }

    @Test
    void recordExpirationStopsDancingWithoutEjectingDisc() {
        EntityParrot parrot = parrot();
        Vector3 position = new Vector3(9, 100, 8);
        level.setBlock(position, Block.get(Block.JUKEBOX));
        BlockEntityJukebox jukebox = new BlockEntityJukebox(level.getChunk(0, 0),
                new CompoundTag().putString("id", "Jukebox").putInt("x", 9).putInt("y", 100).putInt("z", 8));
        try {
            jukebox.setRecordItem(Item.get("minecraft:music_disc_cat"));
            jukebox.play();
            assertTrue(parrot.isDancing());
            tick.set(3700);
            assertFalse(jukebox.onUpdate());
            assertFalse(parrot.isDancing());
            assertFalse(jukebox.getRecordItem().isNull());
        } finally {
            jukebox.close();
            level.setBlock(position, Block.get(Block.AIR));
        }
    }

    @Test
    void nearbyMonsterProducesMatchingParrotImitation() {
        EntityParrot parrot = parrot();
        Entity zombie = Entity.createEntityWithUniqueId(Entity.ZOMBIE, new Position(10, 100, 8, level), actorIds.incrementAndGet());
        assertNotNull(zombie);
        entities.add(zombie);
        Level sounds = mock(Level.class);
        doReturn(List.of(zombie)).when(sounds).fastNearbyEntities(any(AxisAlignedBB.class), same(parrot));
        parrot.level = sounds;
        try {
            parrot.imitateNearbyMob();
            verify(sounds).addLevelSoundEvent(parrot, SoundEvent.IMITATE_ZOMBIE, -1, Entity.PARROT, false, false);
            zombie.close();
            parrot.imitateNearbyMob();
            verify(sounds, times(1)).addLevelSoundEvent(parrot, SoundEvent.IMITATE_ZOMBIE, -1, Entity.PARROT, false, false);
        } finally {
            parrot.level = level;
        }
    }

    @Test
    void sneakingCarrierReleasesParrotAndAppliesRemountDelay() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        assertTrue(player.mountEntity(parrot));
        doReturn(true).when(player).isSneaking();
        parrot.onUpdate(1);
        assertNull(parrot.getRiding());
        assertTrue(player.getPassengers().isEmpty());
        doReturn(false).when(player).isSneaking();
        assertFalse(parrot.canRideShoulder(player));
    }

    @Test
    void cancelledShoulderExitPreventsCrossLevelTransfer() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        assertTrue(player.mountEntity(parrot));
        ServerMockFixture.pluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<org.powernukkitx.event.entity.EntityVehicleExitEvent>() {
                    @Override public void handle(org.powernukkitx.event.entity.EntityVehicleExitEvent event) {
                        event.setCancelled();
                    }
                }));
        assertFalse(player.switchLevel(mock(Level.class)));
        assertSame(level, player.level);
        assertSame(player, parrot.getRiding());
    }

    @Test
    void newlyObservingPlayerReceivesSecondShoulderAsPassengerLink() {
        Player player = carrier();
        Player observer = mock(Player.class);
        doReturn(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(new long[]{Level.chunkHash(0, 0)}))
                .when(observer).getUsedChunks();
        doNothing().when(player).spawnTo(observer);
        EntityParrot left = parrot();
        EntityParrot right = parrot();
        left.setTamed(true);
        right.setTamed(true);
        assertTrue(player.mountEntity(left));
        assertTrue(player.mountEntity(right));
        right.spawnTo(observer);
        verify(observer).sendPacket(argThat(packet -> packet instanceof SetActorLinkPacket link
                && link.getLink().getTargetA() == player.uniqueIdLong()
                && link.getLink().getTargetB() == right.uniqueIdLong()
                && link.getLink().getType() == ActorLinkType.PASSENGER));
        var ordered = inOrder(observer);
        ordered.verify(observer).sendPacket(argThat(packet -> packet instanceof SetActorLinkPacket));
        ordered.verify(observer).sendPacket(argThat(packet -> packet instanceof SetActorDataPacket data
                && data.getTargetRuntimeID() == right.runtimeId()
                && data.getActorData().get(ActorDataTypes.SEAT_OFFSET).getX() == -0.4f));
        right.getViewers().clear();
        player.dismountEntity(left);
        player.dismountEntity(right);
    }

    @Test
    void passiveMobCanProduceMatchingImitation() {
        EntityParrot parrot = parrot();
        Entity wolf = Entity.createEntityWithUniqueId(Entity.WOLF, new Position(10, 100, 8, level), actorIds.incrementAndGet());
        assertNotNull(wolf);
        entities.add(wolf);
        Level sounds = mock(Level.class);
        doReturn(List.of(wolf)).when(sounds).fastNearbyEntities(any(AxisAlignedBB.class), same(parrot));
        parrot.level = sounds;
        try {
            parrot.imitateNearbyMob();
            verify(sounds).addLevelSoundEvent(parrot, SoundEvent.IMITATE_WOLF, -1, Entity.PARROT, false, false);
        } finally {
            parrot.level = level;
        }
    }

    @Test
    void parrotArrivingDuringPlaybackStartsDancing() {
        EntityParrot parrot = parrot();
        Vector3 position = new Vector3(9, 100, 8);
        parrot.setPosition(new Vector3(3, 100, 8));
        level.setBlock(position, Block.get(Block.JUKEBOX));
        BlockEntityJukebox jukebox = new BlockEntityJukebox(level.getChunk(0, 0),
                new CompoundTag().putString("id", "Jukebox").putInt("x", 9).putInt("y", 100).putInt("z", 8));
        try {
            jukebox.setRecordItem(Item.get("minecraft:music_disc_cat"));
            jukebox.play();
            assertFalse(parrot.isDancing());
            parrot.setPosition(new Vector3(8.5, 100, 8.5));
            tick.set(20);
            assertTrue(jukebox.onUpdate());
            assertTrue(parrot.isDancing());
        } finally {
            jukebox.close();
            level.setBlock(position, Block.get(Block.AIR));
        }
    }

    @Test
    void carrierInitiatedDismountAlsoAppliesRemountDelay() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        tick.set(200);
        assertTrue(player.mountEntity(parrot));
        assertTrue(player.dismountEntity(parrot));
        assertFalse(parrot.canRideShoulder(player));
        tick.set(300);
        assertTrue(parrot.canRideShoulder(player));
    }

    @Test
    void flightSmoothingRejectsWallAtTheBirdsHeight() {
        EntityParrot parrot = parrot();
        class FlightPath extends ParrotRouteFinder {
            FlightPath() { super(parrot); }
            boolean blocked(Vector3 from, Vector3 to) { return hasBarrier(from, to); }
        }
        Vector3 wall = new Vector3(9, 100, 8);
        level.setBlock(wall, Block.get(Block.STONE));
        level.releaseTickCachedBlocks();
        try {
            assertTrue(new FlightPath().blocked(new Vector3(8.5, 100, 8.5), new Vector3(10.5, 100, 8.5)));
        } finally {
            level.setBlock(wall, Block.get(Block.AIR));
            level.releaseTickCachedBlocks();
        }
        assertFalse(new FlightPath().blocked(new Vector3(8.5, 100, 8.5), new Vector3(10.5, 100, 8.5)));
    }

    @Test
    void flightEdgesCannotCutSolidCorners() {
        EntityParrot parrot = parrot();
        Vector3 from = new Vector3(8.5, 100.5, 8.5);
        Vector3 to = new Vector3(9.5, 100.5, 9.5);
        class FlightPath extends ParrotRouteFinder {
            FlightPath() { super(parrot); }
            boolean acceptsEdge() {
                setTarget(to);
                putNeighborNodeIntoOpen(new org.powernukkitx.entity.ai.route.data.Node(from, null, 0, 0));
                return getOpenNode(to) != null;
            }
        }
        Vector3 wall = new Vector3(9, 100, 8);
        level.setBlock(wall, Block.get(Block.STONE));
        level.releaseTickCachedBlocks();
        try {
            assertFalse(new FlightPath().acceptsEdge());
        } finally {
            level.setBlock(wall, Block.get(Block.AIR));
            level.releaseTickCachedBlocks();
        }
        assertTrue(new FlightPath().acceptsEdge());
    }

    private void cancelShoulderExits() {
        ServerMockFixture.pluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<org.powernukkitx.event.entity.EntityVehicleExitEvent>() {
                    @Override public void handle(org.powernukkitx.event.entity.EntityVehicleExitEvent event) {
                        event.setCancelled();
                    }
                }));
    }

    @Test
    void cancelledExitCannotKeepParrotLinkedToOfflineCarrier() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        assertTrue(player.mountEntity(parrot));
        cancelShoulderExits();
        doReturn(false).when(player).isOnline();
        parrot.onUpdate(1);
        assertNull(parrot.getRiding());
        assertTrue(player.getPassengers().isEmpty());
    }

    @Test
    void closingParrotCleansShoulderEvenWhenExitIsCancelled() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        assertTrue(player.mountEntity(parrot));
        cancelShoulderExits();
        parrot.close();
        assertTrue(parrot.closed);
        assertNull(parrot.getRiding());
        assertTrue(player.getPassengers().isEmpty());
    }

    @Test
    void cancelledDespawnKeepsParrotOnShoulder() {
        Player player = carrier();
        EntityParrot parrot = parrot();
        parrot.setTamed(true);
        assertTrue(player.mountEntity(parrot));
        ServerMockFixture.pluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<org.powernukkitx.event.entity.EntityDespawnEvent>() {
                    @Override public void handle(org.powernukkitx.event.entity.EntityDespawnEvent event) {
                        event.setCancelled();
                    }
                }));
        parrot.close();
        assertFalse(parrot.closed);
        assertSame(player, parrot.getRiding());
        assertTrue(player.isPassenger(parrot));
    }

}
