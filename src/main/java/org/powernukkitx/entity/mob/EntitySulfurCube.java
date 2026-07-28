package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.HoppingController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntitySulfurCube extends EntityMob implements EntityWalkable, EntityVariant {

    public static final int SIZE_SMALL = 1;
    public static final int SIZE_LARGE = 2;

    private static final String TAG_SIZE = "SulfurCubeSize";
    private static final String TAG_ABSORBED_ID = "AbsorbedBlockId";
    private static final String TAG_GROW_AGE = "SulfurGrowAge";

    private static final int GROW_TICKS = 24000;

    @Override
    @NotNull
    public String getIdentifier() {
        return SULFUR_CUBE;
    }

    public EntitySulfurCube(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getVariant() {
        if (getBehaviorGroup() != null) {
            Integer variant = getMemoryStorage().get(CoreMemoryTypes.VARIANT);
            if (variant != null) return variant;
        }
        if (this.nbt.contains(TAG_SIZE)) {
            return this.getNbt().getInt(TAG_SIZE);
        }
        return SIZE_LARGE;
    }

    @Override
    public void setVariant(int variant) {
        this.nbt.putInt(TAG_SIZE, variant);
        if (getBehaviorGroup() != null) {
            getMemoryStorage().put(CoreMemoryTypes.VARIANT, variant);
        }
    }

    @Override
    public boolean hasVariant() {
        if (getBehaviorGroup() != null && getMemoryStorage().notEmpty(CoreMemoryTypes.VARIANT)) {
            return true;
        }
        return this.nbt.contains(TAG_SIZE);
    }

    @Override
    public int[] getAllVariant() {
        return new int[]{SIZE_SMALL, SIZE_LARGE};
    }

    public boolean isLarge() {
        return getVariant() == SIZE_LARGE;
    }

    public boolean hasAbsorbedBlock() {
        return this.nbt.contains(TAG_ABSORBED_ID);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(new FlatRandomRoamExecutor(0.4f, 12, 40, false, -1, true, 10),
                                entity -> !((EntitySulfurCube) entity).hasAbsorbedBlock(), 1, 1)
                )
                .controllers(new HoppingController(40), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    @Override
    protected void initEntity() {
        if (!this.nbt.contains(TAG_SIZE)) {
            this.nbt.putInt(TAG_SIZE, SIZE_LARGE);
        }
        super.initEntity();
        if (getBehaviorGroup() != null) {
            getMemoryStorage().put(CoreMemoryTypes.VARIANT, this.getNbt().getInt(TAG_SIZE));
        }
        if (hasAbsorbedBlock()) {
            showAbsorbedBlock(Block.get(this.getNbt().getString(TAG_ABSORBED_ID)).toItem());
        }
        recalculateBoundingBox();
    }

    @Override
    public double getFloatingForceFactor() {
        return 0;
    }

    @Override
    public float getWidth() {
        if (getBehaviorGroup() == null) return 0.98f;
        return isLarge() ? 0.98f : 0.49f;
    }

    @Override
    public float getHeight() {
        if (getBehaviorGroup() == null) return 0.98f;
        return isLarge() ? 0.98f : 0.49f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(getVariant() == SIZE_LARGE ? 8 : 4);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(getVariant() == SIZE_LARGE ? 0.4f : 0.3f);
    }

    @Override
    public String getOriginalName() {
        return "Sulfur Cube";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("sulfur_cube", "mob");
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (isLarge() && !hasAbsorbedBlock() && !item.isNull() && item.isBlock()) {
            absorbBlock(item.getBlock());
            return true;
        }
        if (!isLarge() && !item.isNull() && item.getId().equals(ItemID.SLIME_BALL)) {
            this.nbt.putInt(TAG_GROW_AGE, this.getNbt().getInt(TAG_GROW_AGE) + 2400);
            return true;
        }
        return super.onInteract(player, item, clickedPos);
    }

    private void absorbBlock(Block block) {
        this.getNbt().putString(TAG_ABSORBED_ID, block.getId());
        showAbsorbedBlock(block.toItem());
    }

    private void showAbsorbedBlock(Item blockItem) {
        this.getArmorInventory().setBody(blockItem);
        this.getArmorInventory().sendContents(this.getViewers().values().toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (hasAbsorbedBlock()
                && source.getCause() != EntityDamageEvent.DamageCause.VOID
                && source.getCause() != EntityDamageEvent.DamageCause.SUICIDE) {
            source.setCancelled();
            return false;
        }
        return super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean result = super.onUpdate(currentTick);
        if (this.closed || this.level == null) return result;

        if (!isLarge()) {
            int age = this.getNbt().getInt(TAG_GROW_AGE) + 1;
            this.nbt.putInt(TAG_GROW_AGE, age);
            if (age >= GROW_TICKS) {
                spawnCube(SIZE_LARGE);
                this.close();
            }
        }
        return result;
    }

    private void spawnCube(int size) {
        CompoundTag childNBT = Entity.getDefaultNBT(this.getLocation());
        childNBT.putInt(TAG_SIZE, size);
        EntitySulfurCube cube = new EntitySulfurCube(this.getChunk(), childNBT);
        cube.setPosition(this.add(Utils.rand(-0.5, 0.5), 0, Utils.rand(-0.5, 0.5)));
        cube.setRotation(this.yaw, this.pitch);
        cube.spawnToAll();
    }

    @Override
    public void kill() {
        if (isLarge()) {
            for (int i = 0; i < 2; i++) {
                spawnCube(SIZE_SMALL);
            }
        }
        super.kill();
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        List<Item> drops = new ArrayList<>();
        if (hasAbsorbedBlock()) {
            drops.add(Block.get(this.getNbt().getString(TAG_ABSORBED_ID)).toItem());
        }
        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public Integer getExperienceDrops() {
        return isLarge() ? Utils.rand(1, 2) : 0;
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (hasAbsorbedBlock() && entity instanceof Player) {
            double dx = this.x - entity.x;
            double dz = this.z - entity.z;
            double distSq = dx * dx + dz * dz;
            if (distSq > 0.0001) {
                double dist = Math.sqrt(distSq);
                double force = 0.25;
                this.motionX += (dx / dist) * force;
                this.motionZ += (dz / dist) * force;
                this.setDataFlag(ActorFlags.MOVING, true);
            }
            return;
        }
        super.applyEntityCollision(entity);
    }
}
