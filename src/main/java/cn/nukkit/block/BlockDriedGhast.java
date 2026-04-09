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
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMapBuilder;
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
    private static final int TOTAL_HYDRATION_TICKS = 20 * 60 * 20;  // 20 minutes
    private static final int HYDRATION_MAX = 3;                     // visual levels 0..3
    private static final int HYDRATION_STEP_TICKS = Math.max(1, TOTAL_HYDRATION_TICKS / (HYDRATION_MAX + 1));

    @Override
    public String getName() {
        return "Dried Ghast";
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
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

        ensureHydrationTicking(this);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        Level level = getLevel();
        if (level == null) return type;

        if (!level.isChunkLoaded(getFloorX() >> 4, getFloorZ() >> 4)) return type;

        Block now = level.getBlock(this);
        if (!(now instanceof BlockDriedGhast block)) return type;

        Integer lvlObj = block.getPropertyValue(CommonBlockProperties.REHYDRATION_LEVEL);
        int lvl = lvlObj == null ? 0 : Math.max(0, Math.min(HYDRATION_MAX, lvlObj));

        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (block.isWaterLogged() || lvl > 0) {
                ensureHydrationTicking(block);
            } else {
                level.cancelScheduledUpdate(block.floor(), block);
            }
            return type;
        }

        if (type != Level.BLOCK_UPDATE_SCHEDULED) return type;

        boolean waterlogged = block.isWaterLogged();

        if (waterlogged) {
            if (lvl < HYDRATION_MAX) {
                lvl++;
                block.setPropertyValue(CommonBlockProperties.REHYDRATION_LEVEL, lvl);
                level.addSound(block, Sound.BLOCK_DRIED_GHAST_STATE_CHANGE);
                level.setBlock(block, block, true);
            } else {
                spawnGhastling(block, level);
                level.cancelScheduledUpdate(block, block);
                return 1;
            }
        } else {
            if (lvl > 0) {
                lvl--;
                block.setPropertyValue(CommonBlockProperties.REHYDRATION_LEVEL, lvl);
                level.addSound(block, Sound.BLOCK_DRIED_GHAST_STATE_CHANGE);
                level.setBlock(block, block, true);
            }
        }

        if (block.isWaterLogged() || lvl > 0) {
            level.scheduleUpdate(block, HYDRATION_STEP_TICKS, false);
        } else {
            level.cancelScheduledUpdate(block, block);
        }

        return type;
    }

    private void spawnGhastling(@NotNull BlockDriedGhast block, Level level) {
        double sx = block.getFloorX() + 0.5;
        double sy = block.getFloorY() + 0.1;
        double sz = block.getFloorZ() + 0.5;

        int homeX = block.getFloorX();
        int homeY = block.getFloorY() - 1;
        int homeZ = block.getFloorZ();

        MinecraftCardinalDirection cd = block.getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
        float yaw = cardinalToYaw(cd);

        NbtMapBuilder nbt = Entity.getDefaultNBT(new Vector3(sx, sy, sz), null, yaw, 0f).toBuilder();
        nbt.putDouble("HomeX", homeX);
        nbt.putDouble("HomeY", homeY);
        nbt.putDouble("HomeZ", homeZ);

        Entity baby = Entity.createEntity(EntityID.HAPPY_GHAST, level.getChunk((int) sx >> 4, (int) sz >> 4), nbt.build());
        if (baby == null) return;

        baby.setBaby(true);
        baby.spawnToAll();
        baby.level.addSound(block, Sound.MOB_GHASTLING_SPAWN);
        level.setBlock(block, Block.get(BlockID.AIR), true);
    }

    private float cardinalToYaw(@Nullable MinecraftCardinalDirection cd) {
        return switch (cd) {
            case SOUTH -> 0f;
            case WEST  -> 90f;
            case NORTH -> 180f;
            case EAST  -> 270f;
            case null, default -> 0f;
        };
    }

    private void ensureHydrationTicking(@NotNull BlockDriedGhast block) {
        Level level = block.getLevel();
        if (level == null) return;

        Position pos = block.floor();

        if (!level.isUpdateScheduled(pos, block)) {
            level.scheduleUpdate(block, pos, HYDRATION_STEP_TICKS, 0, true, false);
        }
    }
}
