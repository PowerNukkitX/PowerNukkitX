package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.ClimateVariant;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FollowRiderExecutor;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityPig extends EntityAnimal implements EntityWalkable, EntityRideable, ClimateVariant {

    @Override
    @NotNull public String getIdentifier() {
        return PIG;
    }
    

    public EntityPig(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        //用于刷新InLove状态的核心行为
                        new Behavior(
                                new InLoveExecutor(400),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FEED_TIME, 0, 400),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                ),
                                1, 1, 1, false
                        ),
                        //生长
                        new Behavior(
                                new AnimalGrowExecutor(),
                                //todo：Growth rate
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.ENTITY_SPAWN_TIME, 20 * 60 * 20, Integer.MAX_VALUE),
                                        entity -> entity instanceof EntityAnimal animal && animal.isBaby()
                                )
                                , 1, 1, 1200
                        ),
                        new Behavior(new PigBoostExecutor(), entity -> getMemoryStorage().get(CoreMemoryTypes.PIG_BOOST) != 0, 1, 1)
                ),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIG_SAY), new RandomSoundEvaluator(), 6,1),
                        new Behavior(new FollowRiderExecutor(), new RiderEvaluator(), 5, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100), 4, 1),
                        new Behavior(new EntityBreedingExecutor<>(EntityPig.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 3, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_FEEDING_PLAYER, 0.4f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 2, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(4, 10), 1, 1, 100),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("saddled", isSaddled());
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 1.85001f, 0);
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
        if(this.namedTag.contains("saddled")) {
            setSaddled(this.namedTag.getBoolean("saddled"));
        }
        if(namedTag.contains("variant")) {
            setVariant(Variant.get(namedTag.getString("variant")));
        } else setVariant(getBiomeVariant(getLevel().getBiomeId((int) x, (int) y, (int) z)));

    }

    public void setSaddled(boolean saddled) {
        setDataFlag(EntityFlag.SADDLED, saddled);
    }

    public boolean isSaddled() {
        return getDataFlag(EntityFlag.SADDLED);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if(!isBaby()) {
            if (isSaddled()) {
                mountEntity(player);
            } else if (item.getId().equals(Item.SADDLE)) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SADDLE, -1, getIdentifier(), false, false);
                setSaddled(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getOriginalName() {
        return "Pig";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_PORKCHOP : Item.PORKCHOP)), isSaddled() ? Item.get(Item.SADDLE) : Item.AIR};
    }

    @Override
    public boolean isBreedingItem(Item item) {
        String id = item.getId();
        return Objects.equals(id, Item.CARROT) || Objects.equals(id, Item.POTATO) || Objects.equals(id, BlockID.BEETROOT);
    }

    protected class RiderEvaluator implements IBehaviorEvaluator {

        @Override
        public boolean evaluate(EntityIntelligent entity) {
            Entity rider = entity.getPassenger();
            if(rider == null) return false;
            if(rider instanceof Player player) {
                if(player.getInventory().getItemInHand().getId().equals(Item.CARROT_ON_A_STICK)) {
                    return true;
                }
            }
            return false;
        }
    }

    protected class PigBoostExecutor implements IBehaviorExecutor {

        @Override
        public boolean execute(EntityIntelligent entity) {
            entity.getMemoryStorage().put(CoreMemoryTypes.PIG_BOOST, entity.getMemoryStorage().get(CoreMemoryTypes.PIG_BOOST)-1);
            entity.setMovementSpeed(0.5f);
            return true;
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            entity.setMovementSpeed(0.2f);
        }
    }
}
