package org.powernukkitx.entity.mob;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.HoppingController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.passive.EntityFrog;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityMagmaCube extends EntityMob implements EntityWalkable, EntityVariant {

    private static final String TAG_SLIME_SIZE = "SlimeSize";
    public static final int SIZE_SMALL = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_BIG = 4;

    @Override
    @NotNull
    public String getIdentifier() {
        return MAGMA_CUBE;
    }

    public EntityMagmaCube(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getVariant() {
        if (getBehaviorGroup() != null) {
            Integer variant = getMemoryStorage().get(CoreMemoryTypes.VARIANT);
            if (variant != null) return variant;
        }

        if (this.nbt.contains(TAG_SLIME_SIZE)) {
            return this.getNbt().getInt(TAG_SLIME_SIZE);
        }

        return SIZE_BIG;
    }

    @Override
    public void setVariant(int variant) {
        this.nbt.putInt(TAG_SLIME_SIZE, variant);

        if (getBehaviorGroup() != null) {
            getMemoryStorage().put(CoreMemoryTypes.VARIANT, variant);
        }
    }

    @Override
    public boolean hasVariant() {
        if (getBehaviorGroup() != null && getMemoryStorage().notEmpty(CoreMemoryTypes.VARIANT)) {
            return true;
        }

        return this.nbt.contains(TAG_SLIME_SIZE);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestTargetEntitySensor<>(0, 16, 20,
                        List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)),
                Set.of(new HoppingController(40), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        if (!this.nbt.contains(TAG_SLIME_SIZE)) {
            this.nbt.putInt(TAG_SLIME_SIZE, randomVariant());
        }

        super.initEntity();

        if (getBehaviorGroup() != null) {
            getMemoryStorage().put(CoreMemoryTypes.VARIANT, this.getNbt().getInt(TAG_SLIME_SIZE));
        }

        if (getVariant() == SIZE_BIG) {
            this.diffHandDamage = new float[]{4, 6, 9};
        } else if (getVariant() == SIZE_MEDIUM) {
            this.diffHandDamage = new float[]{3, 4, 6};
        } else {
            this.diffHandDamage = new float[]{2.5f, 3, 4.5f};
        }

        recalculateBoundingBox();
    }

    @Override
    public double getFloatingForceFactor() {
        return 0;
    }

    @Override
    public float getWidth() {
        if (getBehaviorGroup() == null) return 0;
        return 0.51f + getVariant() * 0.51f;
    }

    @Override
    public float getHeight() {
        if (getBehaviorGroup() == null) return 0;
        return 0.51f + getVariant() * 0.51f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        if (!hasVariant()) this.setVariant(randomVariant());
        int variantHealth = switch (getVariant()) {
            case SIZE_BIG -> 16;
            case SIZE_MEDIUM -> 4;
            case SIZE_SMALL -> 1;
            default -> 16;
        };

        return HealthComponent.value(variantHealth);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        if (!hasVariant()) this.setVariant(randomVariant());
        float variantMovement = switch (getVariant()) {
            case SIZE_BIG -> 0.75f;
            case SIZE_MEDIUM -> 0.66f;
            case SIZE_SMALL -> 0.6f;
            default -> 0.75f;
        };
        return MovementComponent.value(variantMovement);
    }

    @Override
    public int getFrostbiteInjury() {
        return 5;
    }

    @Override
    public String getOriginalName() {
        return "Magma Cube";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("magmacube", "monster", "mob");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        List<Item> drops = new ArrayList<>();

        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (getVariant() == SIZE_SMALL
                && lastDamageCause instanceof EntityDamageByEntityEvent event
                && event.getDamager() instanceof EntityFrog frog) {

            String froglight = switch (frog.getVariant()) {
                case 0 -> Block.OCHRE_FROGLIGHT;
                case 1 -> Block.VERDANT_FROGLIGHT;
                default -> Block.PEARLESCENT_FROGLIGHT;
            };

            return new Item[]{Item.get(froglight)};
        }

        if (getVariant() != SIZE_SMALL && Utils.rand(0, 2) != 0) {
            int amount = Utils.rand(0, 2 + looting);
            if (amount > 0) {
                drops.add(Item.get(Item.MAGMA_CREAM, 0, amount));
            }
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public Integer getExperienceDrops() {
        return getVariant();
    }

    @Override
    public int[] getAllVariant() {
        return new int[]{1, 2, 4};
    }

    private int getSmaller() {
        return switch (getVariant()) {
            case 4 -> 2;
            default -> getVariant() - 1;
        };
    }

    @Override
    public void kill() {
        if (getVariant() != SIZE_SMALL) {
            for (int i = 1; i < Utils.rand(2, 5); i++) {
                EntityMagmaCube magmaCube = new EntityMagmaCube(this.getChunk(), this.getNbt());
                magmaCube.setPosition(this.add(Utils.rand(-0.5, 0.5), 0, Utils.rand(-0.5, 0.5)));
                magmaCube.setRotation(this.yaw, this.pitch);
                magmaCube.setVariant(getSmaller());
                magmaCube.spawnToAll();
            }
        }
        super.kill();
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return super.attackTarget(entity) || entity instanceof EntityGolem;
    }
}
