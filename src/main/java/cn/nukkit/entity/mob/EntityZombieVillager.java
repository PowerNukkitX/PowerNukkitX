package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.JumpExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.NearestBlockIncementExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.MemorizedBlockSensor;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemGoldenApple;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EntityZombieVillager extends EntityZombie implements EntityWalkable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return ZOMBIE_VILLAGER;
    }

    public EntityZombieVillager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new NearestBlockIncementExecutor(), entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK) && getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK) instanceof BlockTurtleEgg, 1, 1)
                ),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ZOMBIE_VILLAGER_SAY, isBaby() ? 1.3f : 0.8f, isBaby() ? 1.7f : 1.2f, 1, 1), new RandomSoundEvaluator(), 7, 1),
                        new Behavior(new JumpExecutor(), all(entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK), entity -> entity.getCollisionBlocks().stream().anyMatch(block -> block instanceof BlockTurtleEgg)), 6, 1, 10),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.3f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK), 5, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_GOLEM, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_GOLEM), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 0),
                        new NearestEntitySensor(EntityGolem.class, CoreMemoryTypes.NEAREST_GOLEM, 42, 0),
                        new MemorizedBlockSensor(11, 5, 20)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 v) {
        if(item instanceof ItemGoldenApple) {
            if(hasEffect(EffectType.WEAKNESS)) {
                if(!getDataFlag(EntityFlag.SHAKING)) {
                    setDataFlag(EntityFlag.SHAKING);
                    if(!player.isCreative()) {
                        this.namedTag.putString("purifyPlayer", player.getLoginChainData().getXUID());
                        player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                    }
                    getLevel().addSound(this, Sound.MOB_ZOMBIE_REMEDY);
                }
            }
        }
        return false;
    }

    private int curingTick = 0;

    @Override
    public boolean onUpdate(int currentTick) {
        if(getDataFlag(EntityFlag.SHAKING)) {
            if(curingTick < 2000) {
                curingTick++;
            } else transformVillager();
        }
        return super.onUpdate(currentTick);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
        getMemoryStorage().put(CoreMemoryTypes.LOOKING_BLOCK, BlockTurtleEgg.class);
    }

    @Override
    public String getOriginalName() {
        return "Zombie Villager";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombie", "zombie_villager", "undead", "monster", "mob");
    }

    protected void transformVillager() {
        this.close();
        getArmorInventory().getContents().values().forEach(i -> getLevel().dropItem(this, i));
        getEquipmentInventory().getContents().values().forEach(i -> getLevel().dropItem(this, i));
        EntityVillagerV2 villager = new EntityVillagerV2(this.getChunk(), this.namedTag);
        villager.addEffect(Effect.get(EffectType.NAUSEA).setDuration(200));
        villager.setPosition(this);
        villager.setRotation(this.yaw, this.pitch);
        villager.spawnToAll();
        villager.level.addSound(villager, Sound.MOB_ZOMBIE_UNFECT);
    }

}
