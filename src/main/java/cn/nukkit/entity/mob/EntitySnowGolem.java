package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockPumpkin;
import cn.nukkit.block.BlockSnow;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.SnowGolemShootExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShears;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class EntitySnowGolem extends EntityGolem {
    @Override
    @NotNull public String getIdentifier() {
        return SNOW_GOLEM;
    }

    public int waterTicks = 0;

    public EntitySnowGolem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new SnowGolemShootExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, 16, true, 20, 0), all(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), entity -> !(getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof EntitySnowGolem)), 3, 1),
                        new Behavior(new SnowGolemShootExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.4f, 10, true, 20, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                entity -> attackTarget(getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET))
                        ), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestEntitySensor(EntityMob.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 16, 0)),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if(item instanceof ItemShears) {
            if(!isSheared()) {
                this.setSheared(true);
                this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SHEAR);
                if(player.getGamemode() != Player.CREATIVE) player.getInventory().getItemInHand().setDamage(item.getDamage() + 1);
                this.level.dropItem(this.add(0, this.getEyeHeight(), 0), Item.get(Block.CARVED_PUMPKIN));
            }
        }
        return super.onInteract(player, item);
    }

    @Override
    public String getOriginalName() {
        return "Snow Golem";
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(4);
        setSheared(false);
        super.initEntity();
    }

    public void setSheared(boolean sheared) {
        setDataFlag(EntityFlag.SHEARED, sheared);
    }

    public boolean isSheared() {
        return getDataFlag(EntityFlag.SHEARED);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        this.waterTicks++;
        if(this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
            if(this.getLevelBlock().isAir()) {
                if(this.getLevelBlock().down().isFullBlock() && this.isOnGround()){
                    this.getLevel().setBlock(this.getLevelBlock(), Block.get(Block.SNOW_LAYER));
                }
            }
        }
        if(this.waterTicks >= 20) {
            if((this.level.isRaining() && !this.isUnderBlock()) || this.getLevelBlock() instanceof BlockLiquid || Registries.BIOME.get(getLevel().getBiomeId(getFloorX(), this.getFloorY(), getFloorZ())).temperature() > 1.0) {
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.WEATHER, 1));
            }
            this.waterTicks = 0;
        }
        return super.onUpdate(currentTick);
    }

    public static void checkAndSpawnGolem(Block block) {
        if(block.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            if(block instanceof BlockPumpkin) {
                faces:
                for(BlockFace blockFace : BlockFace.values()) {
                    for(int i = 1; i<=2; i++) {
                        if(!(block.getSide(blockFace, i) instanceof BlockSnow)) {
                            continue faces;
                        }
                    }
                    for(int i = 0; i<=2; i++) {
                        Block location = block.getSide(blockFace, i);
                        block.level.setBlock(location, Block.get(Block.AIR));
                        block.level.addParticle(new DestroyBlockParticle(location.add(0.5, 0.5, 0.5), block));
                        block.level.getVibrationManager().callVibrationEvent(new VibrationEvent(null, location.add(0.5, 0.5, 0.5), VibrationType.BLOCK_DESTROY));

                    }
                    Block pos = block.getSide(blockFace, 2);
                    CompoundTag nbt = new CompoundTag()
                            .putList("Pos", new ListTag<DoubleTag>()
                                    .add(new DoubleTag(pos.x + 0.5))
                                    .add(new DoubleTag(pos.y))
                                    .add(new DoubleTag(pos.z + 0.5)))
                            .putList("Motion", new ListTag<DoubleTag>()
                                    .add(new DoubleTag(0))
                                    .add(new DoubleTag(0))
                                    .add(new DoubleTag(0)))
                            .putList("Rotation", new ListTag<FloatTag>()
                                    .add(new FloatTag(0f))
                                    .add(new FloatTag(0f)));

                    Entity snowgolem = Entity.createEntity(EntityID.SNOW_GOLEM, block.level.getChunk(block.getChunkX(), block.getChunkZ()), nbt);
                    snowgolem.spawnToAll();
                    return;
                }
            }
        }
    }
}
