package cn.nukkit.entity.mob;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityVariant;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.ShulkerAttackExecutor;
import cn.nukkit.entity.ai.executor.ShulkerIdleExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EntityShulker extends EntityMob implements EntityVariant {

    public int color = 0;

    @Override
    @NotNull public String getIdentifier() {
        return SHULKER;
    }
    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(4, 10), 2, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_SHULKER_AMBIENT, 0.8f, 1.2f, 0.8f, 0.8f), new RandomSoundEvaluator(20, 20), 1, 1)
                ),
                Set.of(
                        new Behavior(new ShulkerIdleExecutor(), new RandomSoundEvaluator(20, 10), 2, 1),
                        new Behavior(new ShulkerAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_PLAYER, 16),
                                not(new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 60))
                        ), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20)),
                Set.of(new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    public EntityShulker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public boolean isPeeking() {
        return getDataProperty(EntityDataTypes.SHULKER_PEEK_AMOUNT, 0) == 0;
    }

    public void setPeeking(int height) {
        setDataProperty(EntityDataTypes.SHULKER_PEEK_AMOUNT, height);
    }

    @Override
    public int getAdditionalArmor() {
        return isPeeking() ? 20 : 30;
    }

    @Override
    protected boolean onCollide(int currentTick, List<Entity> collidingEntities) {
        collidingEntities.stream().filter(entity -> entity instanceof EntityProjectile).forEach(entity -> {
            entity.setMotion(entity.getMotion().multiply(-1));
        });
        return super.onCollide(currentTick, collidingEntities);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        Block block = getLevelBlock();
        if(!block.isAir() || block.down().isAir()) teleport();
        return super.onUpdate(currentTick);
    }

    @Override //Shulker doesn't take knockback
    public void knockBack(Entity attacker, double damage, double x, double z, double base) {}

    @Override //Shulker doesn't move
    public void updateMovement() {}

    @Override
    public float getGravity() {
        return 0;
    }

    @Override //Shulker cannot burn
    public void setOnFire(int seconds) {}

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(getHealth() - source.getDamage() < getMaxHealth()/2f) {
            if(Utils.rand(0,4) == 0) {
                teleport();
                return true;
            }
        }
        return super.attack(source);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();
        if(getMemoryStorage().get(CoreMemoryTypes.VARIANT) == null) setVariant(16);
        setDataProperty(EntityDataTypes.SHULKER_ATTACH_POS, getLevelBlock().getSide(BlockFace.UP).asBlockVector3());
    }

    @Override
    public float getWidth() {
        return 0.99f;
    }

    @Override
    public float getHeight() {
        return 0.99f;
    }

    @Override
    public String getOriginalName() {
        return "Shulker";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("shulker", "monster", "mob");
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 1) == 0) {
            int amount = Utils.rand(0, 1 + looting);
            if (amount > 0) {
                return new Item[]{
                        Item.get(Item.SHULKER_SHELL, 0, amount)
                };
            }
        }

        return Item.EMPTY_ARRAY;
    }


    public void teleport() {
        Arrays.stream(getLevel().getCollisionBlocks(getBoundingBox().grow(7, 7, 7))).filter(block -> block.isFullBlock() && block.up().isAir()).findAny().ifPresent(
                block -> {
                    Location location = block.up().getLocation();
                    getLevel().addLevelSoundEvent(this, LevelSoundEvent.TELEPORT, -1, getIdentifier(), false, false);
                    teleport(location, PlayerTeleportEvent.TeleportCause.SHULKER);
                    getLevel().addLevelSoundEvent(location, LevelSoundEvent.SPAWN, -1, getIdentifier(), false, false);
                }
        );
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        boolean superValue = super.teleport(location, cause);
        if(superValue) super.updateMovement();
        return superValue;
    }

    @Override
    public int[] getAllVariant() {
        return new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    }
}
