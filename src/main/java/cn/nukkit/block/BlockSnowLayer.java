package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static cn.nukkit.block.property.CommonBlockProperties.COVERED_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.HEIGHT;

/**
 * @author xtypr, joserobjr
 * @since 2015/12/6
 */

public class BlockSnowLayer extends BlockFallable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SNOW_LAYER, COVERED_BIT, HEIGHT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSnowLayer() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSnowLayer(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Top Snow";
    }

    public int getSnowHeight() {
        return getPropertyValue(HEIGHT);
    }

    public void setSnowHeight(int snowHeight) {
        setPropertyValue(HEIGHT, snowHeight);
    }

    public boolean isCovered() {
        return getPropertyValue(COVERED_BIT);
    }

    public void setCovered(boolean covered) {
        setPropertyValue(COVERED_BIT, covered);
    }

    @Override
    public double getMaxY() {
        return y + (Math.min(16, getSnowHeight() + 1) * 2) / 16.0;
    }

    @Override
    protected @Nullable AxisAlignedBB recalculateBoundingBox() {
        int snowHeight = getSnowHeight();
        if (snowHeight < 3) {
            return null;
        }
        if (snowHeight == 3 || snowHeight == HEIGHT.getMax()) {
            return this;
        }
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 8 / 16.0, z + 1);
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public boolean canBeReplaced() {
        return getSnowHeight() < HEIGHT.getMax();
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Optional<BlockSnowLayer> increment = Stream.of(target, block)
                .filter(b -> b.getId().equals(SNOW_LAYER)).map(BlockSnowLayer.class::cast)
                .filter(b -> b.getSnowHeight() < HEIGHT.getMax())
                .findFirst();

        if (increment.isPresent()) {
            BlockSnowLayer other = increment.get();
            if (Arrays.stream(level.getCollidingEntities(new SimpleAxisAlignedBB(
                    other.x, other.y, other.z,
                    other.x + 1, other.y + 1, other.z + 1
            ))).anyMatch(e -> e instanceof EntityLiving)) {
                return false;
            }
            other.setSnowHeight(other.getSnowHeight() + 1);
            return level.setBlock(other, other, true);
        }

        Block down = down();
        if (!down.isSolid()) {
            return false;
        }

        switch (down.getId()) {
            case BARRIER, STRUCTURE_VOID -> {
                return false;
            }
            case GRASS_BLOCK -> setCovered(true);
            case TALL_GRASS -> {
                if (!level.setBlock(this, 0, this, true)) {
                    return false;
                }
                level.setBlock(block, 1, block, true, false);
                return true;
            }
            default -> {
            }
        }

        return this.getLevel().setBlock(block, this, true);
    }

    @Override
    public boolean onBreak(Item item) {
        if (layer != 0) {
            return super.onBreak(item);
        }
        return this.getLevel().setBlock(this, 0, getLevelBlockAtLayer(1), true, true);
    }

    @Override
    public void afterRemoval(Block newBlock, boolean update) {
        if (layer != 0 || newBlock.getId().equals(getId())) {
            return;
        }

        Block layer1 = getLevelBlockAtLayer(1);
        if (!layer1.getId().equals(TALL_GRASS)) {
            return;
        }

        // Clear the layer1 block and do a small hack as workaround a vanilla client rendering bug
        Level level = getLevel();
        level.setBlock(this, 0, layer1, true, false);
        level.setBlock(this, 1, get(AIR), true, false);
        level.setBlock(this, 0, newBlock, true, false);
        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            Player[] target = level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY);
            Vector3[] blocks = {getLocation()};
            level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0, false);
            level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1, false);
        }, 10);

        Player[] target = level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY);
        Vector3[] blocks = {getLocation()};
        level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0, false);
        level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1, false);
    }

    @Override
    public int onUpdate(int type) {
        super.onUpdate(type);
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            BiomeRegistry.BiomeDefinition biomeDefinition = Registries.BIOME.get(getLevel().getBiomeId(getFloorX(), this.getFloorY(), getFloorZ()));
            if (biomeDefinition.tags().contains(BiomeTags.WARM) || this.getLevel().getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) >= 10) {
                melt();
                return Level.BLOCK_UPDATE_RANDOM;
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            boolean covered = down().getId().equals(GRASS_BLOCK);
            if (isCovered() != covered) {
                setCovered(covered);
                level.setBlock(this, this, true);
                return type;
            }
        }
        return 0;
    }

    public boolean melt() {
        return melt(2);
    }

    public boolean melt(int layers) {
        Preconditions.checkArgument(layers > 0, "Layers must be positive, got {}", layers);
        Block toMelt = this;
        while (toMelt.getPropertyValue(HEIGHT) == HEIGHT.getMax()) {
            Block up = toMelt.up();
            if (!up.getId().equals(SNOW_LAYER)) {
                break;
            }

            toMelt = up;
        }

        int snowHeight = toMelt.getPropertyValue(HEIGHT) - layers;
        Block newState = snowHeight < 0 ? get(AIR) : Block.get(getBlockState().setPropertyValue(PROPERTIES, HEIGHT, snowHeight));
        BlockFadeEvent event = new BlockFadeEvent(toMelt, newState);
        level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        return level.setBlock(toMelt, event.getNewState(), true);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!item.isShovel() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY;
        }

        int amount = switch (getSnowHeight()) {
            case 0, 1, 2 -> 1;
            case 3, 4 -> 2;
            case 5, 6 -> 3;
            default -> 4;
        };
        return new Item[]{Item.get(ItemID.SNOWBALL, 0, amount)};
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return getSnowHeight() < 3;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP && getSnowHeight() == HEIGHT.getMax();
    }
}
