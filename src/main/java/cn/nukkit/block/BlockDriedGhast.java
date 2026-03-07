package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BlockDriedGhast extends BlockTransparent {

    public static final BlockProperties PROPERTIES = new BlockProperties(DRIED_GHAST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.REHYDRATION_LEVEL);

    public BlockDriedGhast(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    // 20 minutes incubation
    private static final int HYDRATION_STEP_TICKS = 20 * 60 * 20; // 24000 (20 minutes)
    private static final int HYDRATION_MAX = 3;                   // 0..3 stages

    @Override
    public String getName() {
        return "Dried Ghast";
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            MinecraftCardinalDirection direction = CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(player.getHorizontalFacing().getOpposite());
            if (direction != null) {
                setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, direction);
            }
        }

        getLevel().setBlock(this, this, true);

        startHydrationTicking();
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_SCHEDULED) return type;
        Level level = getLevel();
        if (level == null) return type;
        if (!level.isChunkLoaded(getFloorX() >> 4, getFloorZ() >> 4)) return type;
        Block now = level.getBlock(this);
        if (now == null || now.getId() != this.getId()) return type;

        Integer lvlObj = getPropertyValue(CommonBlockProperties.REHYDRATION_LEVEL);
        int lvl = (lvlObj == null) ? 0 : lvlObj;
        if (lvl < 0) lvl = 0;
        if (lvl > HYDRATION_MAX) lvl = HYDRATION_MAX;

        boolean waterlogged = this.isWaterLogged();

        if (waterlogged) {
            if (lvl < HYDRATION_MAX) {
                lvl++;
                setPropertyValue(CommonBlockProperties.REHYDRATION_LEVEL, lvl);
                this.level.addSound(this, Sound.BLOCK_DRIED_GHAST_STATE_CHANGE);
                level.setBlock(this, this, true);
            } else {
                spawnGhastling(level);
                level.cancelScheduledUpdate(this, this);
                return 1;
            }
        } else {
            if (lvl > 0) {
                lvl--;
                setPropertyValue(CommonBlockProperties.REHYDRATION_LEVEL, lvl);
                this.level.addSound(this, Sound.BLOCK_DRIED_GHAST_STATE_CHANGE);
                level.setBlock(this, this, true);
            }
        }

        level.scheduleUpdate(this, HYDRATION_STEP_TICKS, false);
        return type;
    }

    @Override
    public void onNeighborChange(@NotNull BlockFace side) {
        super.onNeighborChange(side);
        startHydrationTicking();
    }

    private void spawnGhastling(Level level) {
        double sx = this.getFloorX() + 0.5;
        double sy = this.getFloorY() + 0.1;
        double sz = this.getFloorZ() + 0.5;

        int homeX = this.getFloorX();
        int homeY = this.getFloorY() - 1;
        int homeZ = this.getFloorZ();

        MinecraftCardinalDirection cd = getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
        float yaw = cardinalToYaw(cd);

        CompoundTag nbt = new CompoundTag()
            .putList("Pos", new ListTag<DoubleTag>()
                .add(new DoubleTag(sx))
                .add(new DoubleTag(sy))
                .add(new DoubleTag(sz)))
            .putList("Motion", new ListTag<DoubleTag>()
                .add(new DoubleTag(0))
                .add(new DoubleTag(0))
                .add(new DoubleTag(0)))
            .putList("Rotation", new ListTag<FloatTag>()
                .add(new FloatTag(yaw))
            .add(new FloatTag(0f)))
            // Set home location
            .putDouble("HomeX", homeX)
            .putDouble("HomeY", homeY)
            .putDouble("HomeZ", homeZ);

        Entity baby = Entity.createEntity(EntityID.HAPPY_GHAST, level.getChunk((int) sx >> 4, (int) sz >> 4), nbt);
        if (baby == null) return;

        baby.setBaby(true);
        baby.spawnToAll();
        baby.level.addSound(this, Sound.MOB_GHASTLING_SPAWN);
        level.setBlock(this, Block.get(BlockID.AIR), true);
    }

    private float cardinalToYaw(@Nullable MinecraftCardinalDirection cd) {
        if (cd == null) return 0f;

        return switch (cd) {
            case SOUTH -> 0f;
            case WEST  -> 90f;
            case NORTH -> 180f;
            case EAST  -> 270f;
            default    -> 0f;
        };
    }


    private void startHydrationTicking() {
        Level level = getLevel();
        if (level == null) return;

        Position pos = this.floor();
        Block at = level.getBlock(pos);
        if (level.isUpdateScheduled(pos, at)) return;
        level.scheduleUpdate(at, pos, HYDRATION_STEP_TICKS, 0, true, false);
    }
}
