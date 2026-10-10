package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockCreakingHeart;
import org.powernukkitx.block.BlockSculkCatalyst;
import org.powernukkitx.blockentity.BlockEntityCreakingHeart;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.DoNothingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.PlayerStaringSensor;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.entity.data.property.EnumEntityProperty;
import org.powernukkitx.entity.data.property.IntEntityProperty;
import org.powernukkitx.event.entity.EntityDamageByChildEntityEvent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.particle.GenericParticle;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventGenericPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EntityCreaking extends EntityMob {
    private final static String PROPERTY_CREAKING = "minecraft:creaking_state";
    private final static String PROPERTY_SWAYING_TICKS = "minecraft:creaking_swaying_ticks";

    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
            new EnumEntityProperty(PROPERTY_CREAKING, new String[]{
                    "neutral",
                    "hostile_observed",
                    "hostile_unobserved",
                    "twitching",
                    "crumbling"
            }, "neutral", true),
            new IntEntityProperty(PROPERTY_SWAYING_TICKS, 0, 0, 6, true)
    };

    private Vector3 homePos;
    private int homeDimensionId = Integer.MIN_VALUE;
    private int swayingTicks;
    private int twitchEndTick = -1;
    private int playerIntersectionTicks;
    private boolean crumbling;

    @Override
    @NotNull
    public String getIdentifier() {
        return CREAKING;
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("creaking", "monster", "mob");
    }

    @Getter
    @Setter
    protected BlockEntityCreakingHeart creakingHeart;

    public EntityCreaking(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        String state = getEnumEntityProperty(PROPERTY_CREAKING);
        boolean stopped = "hostile_observed".equals(state)
                || "twitching".equals(state)
                || "crumbling".equals(state);
        return MovementComponent.value(stopped ? 0f : 0.4f);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(new DoNothingExecutor(),
                                new EntityCheckEvaluator(CoreMemoryTypes.STARING_PLAYER), 5, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_CREAKING_AMBIENT),
                                all(new RandomSoundEvaluator(),
                                        not(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET))), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, 24, true, 30),
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.12f, 12, 100, false, -1, true, 10),
                                none(), 1, 1)
                )
                .sensors(new PlayerStaringCreakingSensor(24, 60, true))
                .controllers(new WalkController(), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    @Override
    protected void initEntity() {
        this.setHealthMax(1);
        this.diffHandDamage = new float[]{2.5f, 3, 4.5f};
        final CompoundTag nbtMap = this.getNbt();
        super.initEntity();

        if (nbtMap.containsList("HomePos")) {
            ListTag<FloatTag> home = nbtMap.getList("HomePos", FloatTag.class);

            if (home.size() == 3) {
                homePos = new Vector3(home.get(0).data, home.get(1).data, home.get(2).data);
                homeDimensionId = nbtMap.getInt("HomeDimensionId");
                fireProof = true;
                setPersistent(true);

                if (homeDimensionId == getLevel().getDimension() && getLevel().getBlock(homePos, true) instanceof BlockCreakingHeart heart) {
                    heart.getOrCreateBlockEntity().setLinkedCreaking(this);
                }
            }
        }
    }

    /**
     * Binds this creaking to its controlling creaking heart.
     *
     * @param heart controlling creaking heart
     */
    public void bindToCreakingHeart(BlockEntityCreakingHeart heart) {
        this.creakingHeart = heart;
        this.homePos = new Vector3(heart.getFloorX(), heart.getFloorY(), heart.getFloorZ());
        this.homeDimensionId = heart.getLevel().getDimension();
        this.fireProof = true;
        this.setPersistent(true);
    }

    /**
     * Clears the controlling creaking heart when it matches the supplied heart.
     *
     * @param heart creaking heart to clear
     */
    public void clearCreakingHeart(BlockEntityCreakingHeart heart) {
        if (this.creakingHeart == heart) {
            this.creakingHeart = null;
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.isCancelled()) return false;
        if (homePos == null) return super.attack(source);
        if (source.getCause() == EntityDamageEvent.DamageCause.VOID) return super.attack(source);
        if (isClosed() || !isAlive()) return false;

        startSwaying();

        if (source instanceof EntityDamageByEntityEvent damage
                && !(source instanceof EntityDamageByChildEntityEvent)
                && damage.getDamager() instanceof Player
                && creakingHeart != null) {
            creakingHeart.onCreakingDamagedByPlayer(this);
        }

        return true;
    }

    private void startSwaying() {
        swayingTicks = 1;
        setIntEntityProperty(PROPERTY_SWAYING_TICKS, swayingTicks);
        getLevel().addSound(this, Sound.MOB_CREAKING_SWAY);
        getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, getVector3(), VibrationType.ENTITY_SHAKING));
    }

    private void tickSwaying() {
        if (swayingTicks <= 0) return;

        if (swayingTicks >= 6) {
            swayingTicks = 0;
        } else {
            swayingTicks++;
        }

        setIntEntityProperty(PROPERTY_SWAYING_TICKS, swayingTicks);
    }

    /**
     * Starts the creaking twitching transition before it crumbles.
     */
    public void startTwitching() {
        String state = getEnumEntityProperty(PROPERTY_CREAKING);
        if ("twitching".equals(state) || "crumbling".equals(state)) return;

        getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
        getMemoryStorage().clear(CoreMemoryTypes.STARING_PLAYER);
        setEnumEntityProperty(PROPERTY_CREAKING, "twitching");
        setImmobile(true);
        twitchEndTick = getLevel().getTick() + 45;
        getLevel().addSound(this, Sound.MOB_CREAKING_TWITCH);
    }

    /**
     * Starts the creaking crumble sequence and removes the entity.
     */
    public void crumble() {
        if (crumbling) return;
        crumbling = true;

        setEnumEntityProperty(PROPERTY_CREAKING, "crumbling");
        getLevel().addParticle(new GenericParticle(this, ParticleType.CREAKING_CRUMBLE));
        getLevel().addSound(this, Sound.MOB_CREAKING_DEATH);
        getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, getVector3(), VibrationType.ENTITY_DIE));

        bloomNearbySculkCatalysts();

        BlockEntityCreakingHeart heart = creakingHeart;
        creakingHeart = null;
        if (heart != null) heart.onLinkedCreakingCrumbling(this);

        despawnFromAll();
        remove();
    }

    private void bloomNearbySculkCatalysts() {
        int radius = 8;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;

                    Block block = getLevel().getBlock(
                            getFloorX() + x,
                            getFloorY() + y,
                            getFloorZ() + z);

                    if (block instanceof BlockSculkCatalyst catalyst) catalyst.bloom();
                }
            }
        }
    }

    public void sendParticleTrail() {
        Vector3 heart = creakingHeart != null ? creakingHeart : homePos;
        if (heart == null) return;

        Vector3 center = new Vector3(
                (boundingBox.getMinX() + boundingBox.getMaxX()) * 0.5,
                (boundingBox.getMinY() + boundingBox.getMaxY()) * 0.5,
                (boundingBox.getMinZ() + boundingBox.getMaxZ()) * 0.5
        );

        final LevelEventGenericPacket packet = new LevelEventGenericPacket();
        packet.setType(LevelEvent.PARTICLE_CREAKING_HEART_TRIAL);
        packet.setTag(NbtMap.builder()
                .putInt("CreakingAmount", 0)
                .putFloat("CreakingX", (float) center.x)
                .putFloat("CreakingY", (float) center.y)
                .putFloat("CreakingZ", (float) center.z)
                .putInt("HeartAmount", 20)
                .putFloat("HeartX", (float) heart.x)
                .putFloat("HeartY", (float) heart.y)
                .putFloat("HeartZ", (float) heart.z)
                .build());
        Server.broadcastPacket(getViewers().values(), packet);
    }

    @Override
    public void kill() {
        //ToDo: Creaking Death Animation
        super.kill();
        if (creakingHeart != null && creakingHeart.isBlockEntityValid()) {
            creakingHeart.setLinkedCreaking(null);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (homePos != null) {
            ListTag<FloatTag> home = new ListTag<>();

            home.add(new FloatTag((float) homePos.x));
            home.add(new FloatTag((float) homePos.y));
            home.add(new FloatTag((float) homePos.z));

            this.nbt.putList("HomePos", home);
            this.nbt.putInt("HomeDimensionId", homeDimensionId);
        } else {
            this.nbt.remove("HomePos", "HomeDimensionId");
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        tickSwaying();

        if (twitchEndTick >= 0) {
            if (currentTick >= twitchEndTick) {
                crumble();
                return true;
            }
            return super.onUpdate(currentTick);
        }

        if (homePos != null) {
            long time = Math.floorMod(getLevel().getTime(), 24000);

            if (!hasCustomName() && (time <= 12600 || time > 23400 || distance(homePos) > 34)) {
                crumble();
                return true;
            }

            resolveCreakingHeart();
            if (crumbling) return true;

            if (creakingHeart != null) {
                if (!creakingHeart.getHeart().isActive()) {
                    startTwitching();
                } else {
                    if (distance(creakingHeart) > 32) {
                        setMoveTarget(creakingHeart);
                        setLookTarget(creakingHeart);
                    }

                    tickPlayerIntersection();
                }
            }
        }

        return super.onUpdate(currentTick);
    }

    private void resolveCreakingHeart() {
        if (creakingHeart != null || homePos == null || twitchEndTick >= 0) return;

        if (homeDimensionId != getLevel().getDimension()) {
            crumble();
            return;
        }

        if (!(getLevel().getBlock(homePos, true) instanceof BlockCreakingHeart heart)) {
            crumble();
            return;
        }

        BlockEntityCreakingHeart blockEntity = heart.getOrCreateBlockEntity();
        blockEntity.setLinkedCreaking(this);

        if (blockEntity.getLinkedCreaking() != this) crumble();
    }

    private void tickPlayerIntersection() {
        boolean intersects = false;

        for (Player player : getLevel().getPlayers().values()) {
            if (!player.isAlive() || player.isSpectator()) continue;

            Vector3 eye = player.add(0, player.getEyeHeight(), 0);
            if (boundingBox.isVectorInside(eye)) {
                intersects = true;
                break;
            }
        }

        if (!intersects) {
            playerIntersectionTicks = 0;
            return;
        }

        if (++playerIntersectionTicks >= 100) startTwitching();
    }

    @Override
    public float getHeight() {
        return 2.7F;
    }

    @Override
    public float getWidth() {
        return 0.9F;
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }

    @Override
    public boolean isDimensionBound() {
        return homePos != null;
    }

    @Override
    public void updateMovement() {
        if (!isAlive() || isClosed()) return;

        super.updateMovement();
        if (ticksLived < 5) return;

        try {
            if (creakingHeart != null && creakingHeart.isBlockEntityValid()) {
                creakingHeart.getHeart().updateAroundRedstone(BlockFace.UP, BlockFace.DOWN);
            }
        } catch (Exception ignored) {
        }
    }

    private class PlayerStaringCreakingSensor extends PlayerStaringSensor {

        public PlayerStaringCreakingSensor(double range, double triggerDiff, boolean ignoreRotation) {
            super(range, triggerDiff, ignoreRotation);
        }

        @Override
        public void sense(EntityIntelligent entity) {
            String state = getEnumEntityProperty(PROPERTY_CREAKING);
            if ("twitching".equals(state) || "crumbling".equals(state)) {
                entity.getMemoryStorage().clear(CoreMemoryTypes.STARING_PLAYER);
                return;
            }

            Entity target = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
            Player targetPlayer = target instanceof Player player ? player : null;

            if (targetPlayer != null
                    && (!targetPlayer.isAlive() || targetPlayer.distance(EntityCreaking.this) > 24)) {
                entity.getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
                entity.getMemoryStorage().clear(CoreMemoryTypes.STARING_PLAYER);
                setEnumEntityProperty(PROPERTY_CREAKING, "neutral");
                entity.level.addSound(entity, Sound.MOB_CREAKING_DEACTIVATE);
                return;
            }

            if (targetPlayer == null) {
                Player activator = findLookingPlayer(12, false);
                if (activator == null) {
                    setEnumEntityProperty(PROPERTY_CREAKING, "neutral");
                    return;
                }

                entity.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, activator);
                entity.getMemoryStorage().put(CoreMemoryTypes.STARING_PLAYER, activator);
                setEnumEntityProperty(PROPERTY_CREAKING, "hostile_observed");
                entity.level.addSound(entity, Sound.MOB_CREAKING_ACTIVATE);
                return;
            }

            Player observer = findLookingPlayer(24, true);
            if (observer != null) {
                entity.getMemoryStorage().put(CoreMemoryTypes.STARING_PLAYER, observer);

                if ("hostile_unobserved".equals(state)) {
                    entity.level.addSound(entity, Sound.MOB_CREAKING_FREEZE);
                }

                setEnumEntityProperty(PROPERTY_CREAKING, "hostile_observed");
            } else {
                entity.getMemoryStorage().clear(CoreMemoryTypes.STARING_PLAYER);

                if ("hostile_observed".equals(state)) {
                    entity.level.addSound(entity, Sound.MOB_CREAKING_UNFREEZE);
                }

                setEnumEntityProperty(PROPERTY_CREAKING, "hostile_unobserved");
            }
        }

        private Player findLookingPlayer(double range, boolean pumpkinStopsObservation) {
            double rangeSquared = range * range;

            for (Player player : getLevel().getPlayers().values()) {
                if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                if (player.distanceSquared(EntityCreaking.this) > rangeSquared) continue;

                if (pumpkinStopsObservation
                        && player.getInventory() != null
                        && Block.CARVED_PUMPKIN.equals(player.getInventory().getHelmet().getId())) {
                    continue;
                }

                if (player.isLookingAt(add(0, getEyeHeight(), 0), triggerDiff, true)) return player;
            }

            return null;
        }
    }
}
