package cn.nukkit.entity.mob;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityVariant;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.HoppingController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.passive.EntityFrog;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityMagmaCube extends EntityMob implements EntityWalkable, EntityVariant {

    public static final int SIZE_SMALL = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_BIG = 4;

    @Override
    @NotNull public String getIdentifier() {
        return MAGMA_CUBE;
    }

    public EntityMagmaCube(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
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
        super.initEntity();
        if (!hasVariant()) {
            this.setVariant(randomVariant());
        }

        if (getVariant() == SIZE_BIG) {
            this.diffHandDamage = new float[] {4, 6, 9};
        } else if (getVariant() == SIZE_MEDIUM) {
            this.diffHandDamage = new float[] {3, 4, 6};
        } else {
            this.diffHandDamage = new float[] {2.5f, 3, 4.5f};
        }
        if (getVariant() == SIZE_BIG) {
            this.setMaxHealth(16);
        } else if (getVariant() == SIZE_MEDIUM) {
            this.setMaxHealth(4);
        } else if (getVariant() == SIZE_SMALL) {
            this.setMaxHealth(1);
        }
        setHealth(getMaxHealth());
        recalculateBoundingBox();
    }

    @Override
    public double getFloatingForceFactor() {
        return 0;
    }

    @Override
    public float getWidth() {
        if(getBehaviorGroup() == null) return 0;
        return 0.51f + getVariant() * 0.51f;
    }

    @Override
    public float getHeight() {
        if(getBehaviorGroup() == null) return 0;
        return 0.51f + getVariant() * 0.51f;
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
            default -> getVariant()-1;
        };
    }

    @Override
    public void kill() {
        if(getVariant() != SIZE_SMALL) {
            for(int i = 1; i < Utils.rand(2, 5); i++) {
                EntityMagmaCube magmaCube = new EntityMagmaCube(this.getChunk(), this.namedTag);
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
