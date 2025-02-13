package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPortal;
import cn.nukkit.block.BlockRespawnAnchor;
import cn.nukkit.block.BlockWarpedFungus;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.HoglinTransformExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Erik Miller | EinBexiii
 */


public class EntityHoglin extends EntityMob implements EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return HOGLIN;
    }

    public EntityHoglin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(
                                new InLoveExecutor(400),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FEED_TIME, 0, 400),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                ),1, 1
                        )
                ),
                Set.of(
                        new Behavior(new EntityBreedingExecutor<>(EntityHoglin.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 9, 1),
                        new Behavior(new HoglinTransformExecutor(), all(
                                entity -> entity.getLevel().getDimension() != Level.DIMENSION_NETHER,
                                entity -> !isImmobile(),
                                entity -> !entity.namedTag.getBoolean("IsImmuneToZombification")
                        ), 8, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_HOGLIN_ANGRY, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> isAngry()), 7, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_HOGLIN_AMBIENT, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> !isAngry()), 6, 1),
                        new Behavior(new HoglinMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.5f, 40, true, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                not(entity -> getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof Player player && isBreedingItem(player.getInventory().getItemInHand()))
                        ), 5, 1),
                        new Behavior(new HoglinMeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.5f, 40, false, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                not(entity -> getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER) instanceof Player player && isBreedingItem(player.getInventory().getItemInHand()))
                        ), 4, 1),
                        new Behavior(new HoglinFleeFromTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK)
                        ), 3, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.PARENT, 0.7f, true, 64, 3), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.PARENT),
                                new DistanceEvaluator(CoreMemoryTypes.PARENT, 5),
                                entity -> isBaby()
                        ), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20),
                        new BlockSensor(BlockPortal.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2, 20),
                        new BlockSensor(BlockWarpedFungus.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2, 20),
                        new BlockSensor(BlockRespawnAnchor.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2, 20)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    public boolean isAngry() {
        return getDataFlag(EntityFlag.ANGRY);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(40);
        this.diffHandDamage = new float[]{1f, 1f, 1f};
        super.initEntity();
    }

    @Override
    public float[] getDiffHandDamage() {
        if(isBaby()) {
            return super.getDiffHandDamage();
        } else return new float[] {
                Utils.rand(2.5f, 5f),
                Utils.rand(3f, 8f),
                Utils.rand(4.5f, 12f),
        };
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 1.4f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 1.4f;
    }

    public boolean isBreedingItem(Item item) {
        return item.getId().equals(BlockID.CRIMSON_FUNGUS);
    }

    @Override
    public String getOriginalName() {
        return "Hoglin";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_PORKCHOP : Item.PORKCHOP), 0, Utils.rand(1, 3))};
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (isBreedingItem(item)) {
            getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
            getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FEED_TIME, getLevel().getTick());
            sendBreedingAnimation(item);
            item.count--;
            return player.getInventory().setItemInHand(item) && superResult;
        }
        return superResult;
    }

    protected void sendBreedingAnimation(Item item) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.event = EntityEventPacket.EATING_ITEM;
        pk.eid = this.getId();
        pk.data =  item.getFullId();
        Server.broadcastPacket(this.getViewers().values(), pk);
    }

    @Override
    public Integer getExperienceDrops() {
        return isBaby() ? 0 : Utils.rand(1,3);
    }

    protected static class HoglinFleeFromTargetExecutor extends FleeFromTargetExecutor {

        public HoglinFleeFromTargetExecutor(MemoryType<? extends Vector3> memory) {
            super(memory, 0.5f, true, 8);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            if(entity.distance(entity.getMemoryStorage().get(getMemory())) < 8) {
                entity.getLevel().addSound(entity, Sound.MOB_HOGLIN_RETREAT);
            }
        }
    }

    protected static class HoglinMeleeAttackExecutor extends MeleeAttackExecutor {

        public HoglinMeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
            super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            entity.setDataProperty(EntityDataTypes.TARGET_EID, entity.getMemoryStorage().get(memory).getId());
            entity.setDataFlag(EntityFlag.ANGRY);
            entity.level.addLevelSoundEvent(entity, LevelSoundEventPacket.SOUND_ANGRY, -1, Entity.HOGLIN, false, false);
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            entity.setDataFlag(EntityFlag.ANGRY, false);
            entity.setDataProperty(EntityDataTypes.TARGET_EID, 0L);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            entity.setDataFlag(EntityFlag.ANGRY, false);
            entity.setDataProperty(EntityDataTypes.TARGET_EID, 0L);
        }
    }
}
