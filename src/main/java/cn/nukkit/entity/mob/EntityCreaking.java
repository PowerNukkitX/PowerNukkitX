package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCreakingHeart;
import cn.nukkit.block.BlockPaleOakLog;
import cn.nukkit.block.BlockResinClump;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntityCreakingHeart;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.DoNothingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.PlayerStaringSensor;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.entity.data.property.EnumEntityProperty;
import cn.nukkit.entity.data.property.IntEntityProperty;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventGenericPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EntityCreaking extends EntityMob {
    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
        new EnumEntityProperty("minecraft:creaking_state", new String[]{
            "neutral",
            "hostile_observed",
            "hostile_unobserved",
            "twitching",
            "crumbling"
        }, "neutral", true),
        new IntEntityProperty("minecraft:creaking_swaying_ticks", 0, 0, 6, true)
    };
    private final static String PROPERTY_CREAKING = "minecraft:creaking_state";
    private final static String PROPERTY_SWAYING_TICKS = "minecraft:creaking_swaying_ticks";

    @Override @NotNull public String getIdentifier() {
        return CREAKING;
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("creaking", "monster", "mob");
    }

    @Setter
    protected BlockEntityCreakingHeart creakingHeart;

    public EntityCreaking(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                ),
                Set.of(
                        new Behavior(new DoNothingExecutor(), new EntityCheckEvaluator(CoreMemoryTypes.STARING_PLAYER), 5, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_CREAKING_AMBIENT), all(new RandomSoundEvaluator(), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET)), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new PlayerStaringCreakingSensor(40, 70, true),
                        new NearestPlayerCreakingSensor(40, 0, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(1);
        this.diffHandDamage = new float[]{2.5f, 3, 4.5f};
        if(namedTag.containsCompound("creakingHeart")) {
            CompoundTag tag = namedTag.getCompound("creakingHeart");
            Vector3 vec = new Vector3(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            if(getLevel().getBlock(vec, true) instanceof BlockCreakingHeart heart) {
                heart.getOrCreateBlockEntity().setLinkedCreaking(this);
            }
        }
        super.initEntity();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.isCancelled()) return false;
        if(creakingHeart == null) return super.attack(source);
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }
        if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && !(entityDamageByEntityEvent.getDamager() instanceof EntityCreeper)) {
            getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entityDamageByEntityEvent.getDamager());
        }
        var storage = getMemoryStorage();
        if (storage != null) {
            storage.put(CoreMemoryTypes.BE_ATTACKED_EVENT, source);
            storage.put(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, getLevel().getTick());
        }

        Block[] paleLogs = Arrays.stream(getLevel().getCollisionBlocks(creakingHeart.getLevelBlock().getBoundingBox().grow(2, 2, 2))).filter(block -> block instanceof BlockPaleOakLog).toArray(Block[]::new);
        int maxResinSpawn = ThreadLocalRandom.current().nextInt(1, 3);
        int resinSpawned = 0;
        logs:
        for(Block log : paleLogs) {
            for(BlockFace face : BlockFace.values()) {
                Block side = log.getSide(face);
                if(side.isAir()) {
                    BlockResinClump clump = (BlockResinClump) Block.get(Block.RESIN_CLUMP);
                    clump.setPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS, clump.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS) | (0b000001 << face.getOpposite().getDUSWNEIndex()));
                    side.getLevel().setBlock(side, clump);
                    resinSpawned++;
                    if(resinSpawned >= maxResinSpawn) break logs;
                }
            }
        }
        return true;
    }

    public void sendParticleTrail() {
        LevelEventGenericPacket packet = new LevelEventGenericPacket();
        packet.eventId = LevelEventPacket.EVENT_PARTICLE_CREAKING_HEART_TRIAL;
        CompoundTag tag = new CompoundTag();
        tag.putInt("CreakingAmount", 1);
        tag.putFloat("CreakingX", (float) this.x);
        tag.putFloat("CreakingY", (float) this.y);
        tag.putFloat("CreakingZ", (float) this.z);
        tag.putInt("HeartAmount", 1);
        tag.putFloat("HeartX", (float) creakingHeart.x);
        tag.putFloat("HeartY", (float) creakingHeart.y);
        tag.putFloat("HeartZ", (float) creakingHeart.z);
        packet.tag = tag;
        Server.broadcastPacket(this.getViewers().values(), packet);
    }

    public void spawnResin() {

    }

    @Override
    public void kill() {
        //ToDo: Creaking Death Animation
        super.kill();
        if(creakingHeart != null && creakingHeart.isBlockEntityValid()) {
            creakingHeart.setLinkedCreaking(null);
        }
    }

    @Override
    public void saveNBT() {
        if(creakingHeart != null) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", creakingHeart.getFloorX());
            tag.putInt("y", creakingHeart.getFloorY());
            tag.putInt("z", creakingHeart.getFloorZ());
            this.namedTag.putCompound("creakingHeart", tag);
        }
        super.saveNBT();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(!(!getLevel().isDay() || getLevel().isRaining() || getLevel().isThundering())) {
            this.kill();
        }
        if(creakingHeart != null) {
            if(this.distance(creakingHeart) > 32) {
                setMoveTarget(creakingHeart);
                setLookTarget(creakingHeart);
            }
            if(getMemoryStorage().notEmpty(CoreMemoryTypes.LAST_BE_ATTACKED_TIME)) {
                if(getLevel().getTick() - getMemoryStorage().get(CoreMemoryTypes.LAST_BE_ATTACKED_TIME) < 51) {
                    sendParticleTrail();
                }
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public float getHeight() {
        return 2.5F;
    }

    @Override
    public float getWidth() {
        return 1F;
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }

    @Override
    public void updateMovement() {
        if (!this.isAlive() || this.isClosed()) return;

        super.updateMovement();

        if (this.ticksLived < 5) return;

        try {
            if(creakingHeart != null && creakingHeart.isBlockEntityValid()) {
                creakingHeart.getHeart().updateAroundRedstone(BlockFace.UP, BlockFace.DOWN);
            } else kill();
        } catch (Exception e) {
            //can happen when you regenerate a chunk with debug command.
            kill();
        }
    }

    private class NearestPlayerCreakingSensor extends NearestPlayerSensor {

        public NearestPlayerCreakingSensor(double range, double minRange, int period) {
            super(range, minRange, period);
        }

        @Override
        public void sense(EntityIntelligent entity) {
            Player before = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
            super.sense(entity);
            Player after = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
            if(before != after) {
                if(before == null) {
                    entity.level.addSound(entity, Sound.MOB_CREAKING_ACTIVATE);
                } else if(after == null) {
                    entity.level.addSound(entity, Sound.MOB_CREAKING_DEACTIVATE);
                }
            }
        }
    }

    private class PlayerStaringCreakingSensor extends PlayerStaringSensor {

        public PlayerStaringCreakingSensor(double range, double triggerDiff, boolean ignoreRotation) {
            super(range, triggerDiff, ignoreRotation);
        }

        @Override
        public void sense(EntityIntelligent entity) {
            Player before = entity.getMemoryStorage().get(CoreMemoryTypes.STARING_PLAYER);
            super.sense(entity);
            Player after = entity.getMemoryStorage().get(CoreMemoryTypes.STARING_PLAYER);
            if(before != after) {
                if(before == null) {
                    entity.level.addSound(entity, Sound.MOB_CREAKING_FREEZE);
                } else if(after == null) {
                    entity.level.addSound(entity, Sound.MOB_CREAKING_UNFREEZE);
                }
            }
        }

    }
}
