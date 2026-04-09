package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockPumpkin;
import cn.nukkit.block.BlockSnow;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
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
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemShears;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


public class EntitySnowGolem extends EntityGolem {
    @Override
    @NotNull
    public String getIdentifier() {
        return SNOW_GOLEM;
    }

    public int waterTicks = 0;

    public EntitySnowGolem(IChunk chunk, NbtMap nbt) {
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
                    player.getInventory().getItemInHand().setDamage(item.getDamage() + 1);
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
                    final NbtMap nbt = NbtMap.builder()
                            .putList("Pos", NbtType.DOUBLE, Arrays.asList(pos.x + 0.5, pos.y, pos.z + 0.5))
                            .putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0))
                            .putList("Rotation", NbtType.FLOAT, Arrays.asList(0f, 0f))
                            .build();
                    Entity snowgolem = Entity.createEntity(EntityID.SNOW_GOLEM, block.level.getChunk(block.getChunkX(), block.getChunkZ()), nbt);
                    snowgolem.spawnToAll();
                    return;
                }
            }
        }
    }
}
