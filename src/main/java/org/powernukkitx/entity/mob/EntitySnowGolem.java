package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.BlockPumpkin;
import org.powernukkitx.block.BlockSnow;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.SnowGolemShootExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemShears;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.DestroyBlockParticle;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.registry.Registries;

import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


public class EntitySnowGolem extends EntityGolem {
    @Override
    @NotNull
    public String getIdentifier() {
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
                        new Behavior(new SnowGolemShootExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, 16, true, 20, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                entity -> !(getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof EntitySnowGolem)),
                                3, 1),
                        new Behavior(new SnowGolemShootExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.4f, 10, true, 20, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                entity -> attackTarget(getMemoryStorage().get(CoreMemoryTypes.NEAREST_SHARED_ENTITY))
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
        if (item instanceof ItemShears) {
            if (!isSheared()) {
                this.setSheared(true);
                this.level.addLevelSoundEvent(this, SoundEvent.SHEAR);
                if (player.getGamemode() != Player.CREATIVE)
                    player.getInventory().getItemInMainHand().setDamage(item.getDamage() + 1);
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
    public Set<String> typeFamily() {
        return Set.of("snowgolem", "mob");
    }

    @Override
    public boolean isPersistent() {
        return true;
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
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(4);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.2f);
    }

    @Override
    protected void initEntity() {
        setSheared(false);
        super.initEntity();
    }

    public void setSheared(boolean sheared) {
        setDataFlag(ActorFlags.SHEARED, sheared);
    }

    public boolean isSheared() {
        return getDataFlag(ActorFlags.SHEARED);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        this.waterTicks++;
        if (this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
            if (this.getLevelBlock().isAir()) {
                Block support = this.getLevelBlock().down();
                if (support.isFullBlock() && !support.isAir()) {
                    this.getLevel().setBlock(this.getLevelBlock(), Block.get(Block.SNOW_LAYER));
                }
            }
        }
        if (this.waterTicks >= 20) {
            if ((this.level.isRaining() && !this.isUnderBlock()) || this.getLevelBlock() instanceof BlockLiquid || Registries.BIOME.get(getLevel().getBiomeId(getFloorX(), this.getFloorY(), getFloorZ())).second().getTemperature() > 1.0) {
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.WEATHER, 1));
            }
            this.waterTicks = 0;
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randDrop = random.nextInt(3);

        switch (randDrop) {
            case 0:
                return new Item[]{
                        Item.get(ItemID.SNOWBALL, 0, random.nextInt(0, 9))
                };
            case 1:
                return new Item[]{
                        Item.get(ItemID.SNOWBALL, 0, random.nextInt(8, 17))
                };
            case 2:
                return new Item[0];
            default:
                return new Item[0];
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.FALL) return false;
        return super.attack(source);
    }

    public static void checkAndSpawnGolem(Block block) {
        if (block.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            if (block instanceof BlockPumpkin) {
                faces:
                for (BlockFace blockFace : BlockFace.values()) {
                    for (int i = 1; i <= 2; i++) {
                        if (!(block.getSide(blockFace, i) instanceof BlockSnow)) {
                            continue faces;
                        }
                    }
                    for (int i = 0; i <= 2; i++) {
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
