package org.powernukkitx.entity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFence;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityLeashKnot extends Entity {

    private static final double KNOT_Y_OFFSET = 0.25;
    private static final int EMPTY_GRACE_TICKS = 100;
    private int ticksLived = 0;

    public EntityLeashKnot(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return LEASH_KNOT;
    }

    @Override
    public float getWidth() {
        return 0.375f;
    }

    @Override
    public float getHeight() {
        return 0.375f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setHealthMax(10);
        this.setHealthCurrent(10);
        this.setPersistent(true);
    }

    public Block getAttachedBlock() {
        return this.level.getBlock(new Vector3(this.getFloorX(), (int) Math.floor(this.y - KNOT_Y_OFFSET), this.getFloorZ()));
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }
        if (!this.isAlive()) {
            this.despawnFromAll();
            this.close();
            return false;
        }
        this.ticksLived++;
        if (currentTick % 20 == 0) {
            if (!(this.getAttachedBlock() instanceof BlockFence)) {
                this.breakKnot(true);
                return false;
            }
            if (this.ticksLived > EMPTY_GRACE_TICKS && this.getAttachedMobs().isEmpty()) {
                this.close();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.closed) {
            return false;
        }
        if (source instanceof EntityDamageByEntityEvent byEntity && byEntity.getDamager() instanceof Player) {
            this.breakKnot(true);
            return true;
        }
        return false;
    }

    public void removeIfEmpty() {
        if (!this.closed && this.getAttachedMobs().isEmpty()) {
            this.close();
        }
    }

    public void breakKnot(boolean dropLeads) {
        for (Entity mob : this.getAttachedMobs()) {
            mob.unleash(dropLeads);
        }
        if (this.level != null) {
            this.level.addSound(this, Sound.LEASHKNOT_BREAK);
        }
        this.close();
    }

    public List<Entity> getAttachedMobs() {
        final List<Entity> result = new ArrayList<>();
        if (this.level == null) {
            return result;
        }
        final int bx = this.getFloorX();
        final int by = (int) Math.floor(this.y - KNOT_Y_OFFSET);
        final int bz = this.getFloorZ();
        final AxisAlignedBB search = this.getBoundingBox().grow(11, 11, 11);
        for (Entity e : this.level.getNearbyEntities(search, this)) {
            if (e.getLeashedTo() == this) {
                result.add(e);
                continue;
            }
            final Vector3 knotPos = e.getLeashKnotPos();
            if (knotPos != null && knotPos.getFloorX() == bx && knotPos.getFloorY() == by && knotPos.getFloorZ() == bz) {
                result.add(e);
            }
        }
        return result;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public String getOriginalName() {
        return "Leash Knot";
    }

    @Nullable
    public static EntityLeashKnot getKnotAt(@NotNull Level level, int bx, int by, int bz) {
        final AxisAlignedBB box = new SimpleAxisAlignedBB(bx, by, bz, bx + 1.0, by + 1.5, bz + 1.0);
        for (Entity e : level.getNearbyEntities(box)) {
            if (e instanceof EntityLeashKnot knot
                    && knot.getFloorX() == bx && knot.getFloorZ() == bz
                    && (int) Math.floor(knot.y - KNOT_Y_OFFSET) == by) {
                return knot;
            }
        }
        return null;
    }

    @Nullable
    public static EntityLeashKnot getOrCreate(@NotNull Block fence) {
        final Level level = fence.level;
        final int bx = fence.getFloorX();
        final int by = fence.getFloorY();
        final int bz = fence.getFloorZ();
        final EntityLeashKnot existing = getKnotAt(level, bx, by, bz);
        if (existing != null) {
            return existing;
        }
        final double px = bx + 0.5;
        final double py = by + KNOT_Y_OFFSET;
        final double pz = bz + 0.5;
        final CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(px))
                        .add(new DoubleTag(py))
                        .add(new DoubleTag(pz)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(0))
                        .add(new FloatTag(0)));
        final IChunk chunk = level.getChunk(bx >> 4, bz >> 4);
        if (chunk == null) {
            return null;
        }
        final Entity entity = Entity.createEntity(LEASH_KNOT, chunk, nbt);
        if (!(entity instanceof EntityLeashKnot knot)) {
            if (entity != null) {
                entity.close();
            }
            return null;
        }
        knot.spawnToAll();
        level.addSound(knot, Sound.LEASHKNOT_PLACE);
        return knot;
    }
}
