package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author xtypr, joserobjr
 * @since 2015/12/6
 */

public class BlockSnowLayer extends BlockFallableMeta {


    public static final IntBlockProperty SNOW_HEIGHT = new IntBlockProperty("height", true, 7);


    public static final BooleanBlockProperty COVERED = new BooleanBlockProperty("covered_bit", false);


    public static final BlockProperties PROPERTIES = new BlockProperties(SNOW_HEIGHT, COVERED);

    public BlockSnowLayer() {
        // Does nothing
    }

    public BlockSnowLayer(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Top Snow";
    }

    @Override
    public int getId() {
        return SNOW_LAYER;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }


    public int getSnowHeight() {
        return getIntValue(SNOW_HEIGHT);
    }


    public void setSnowHeight(int snowHeight) {
        setIntValue(SNOW_HEIGHT, snowHeight);
    }


    public boolean isCovered() {
        return getBooleanValue(COVERED);
    }


    public void setCovered(boolean covered) {
        setBooleanValue(COVERED, covered);
    }

    
    @Override
    public double getMaxY() {
        return y + (Math.min(16, getSnowHeight() + 1) * 2) / 16.0;
    }

    
    @Override
    @Nullable
    protected AxisAlignedBB recalculateBoundingBox() {
        int snowHeight = getSnowHeight();
        if (snowHeight < 3) {
            return null;
        }
        if (snowHeight == 3 || snowHeight == SNOW_HEIGHT.getMaxValue()) {
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
        return getSnowHeight() < SNOW_HEIGHT.getMaxValue();
    }

    
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Optional<BlockSnowLayer> increment = Stream.of(target, block)
                .filter(b -> b.getId() == SNOW_LAYER).map(BlockSnowLayer.class::cast)
                .filter(b -> b.getSnowHeight() < SNOW_HEIGHT.getMaxValue())
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
            case BARRIER:
            case STRUCTURE_VOID:
                return false;
            case GRASS:
                setCovered(true);
                break;
            case TALL_GRASS:
                if (!level.setBlock(this, 0, this, true)) {
                    return false;
                }
                level.setBlock(block, 1, block, true, false);
                return true;
            default:
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
        if (layer != 0 || newBlock.getId() == getId()) {
            return;
        }

        Block layer1 = getLevelBlockAtLayer(1);
        if (layer1.getId() != TALL_GRASS) {
            return;
        }

        // Clear the layer1 block and do a small hack as workaround a vanilla client rendering bug
        Level level = getLevel();
        level.setBlock(this, 0, layer1, true, false);
        level.setBlock(this, 1, get(AIR), true, false);
        level.setBlock(this, 0, newBlock, true, false);
        Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
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
            Biome biome = Biome.getBiome(getLevel().getBiomeId(getFloorX(), getFloorZ()));
            if (biome.isDry() || this.getLevel().getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) >= 10) {
                melt();
                return Level.BLOCK_UPDATE_RANDOM;
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            boolean covered = down().getId() == GRASS;
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
        while (toMelt.getIntValue(SNOW_HEIGHT) == SNOW_HEIGHT.getMaxValue()) {
            Block up = toMelt.up();
            if (up.getId() != SNOW_LAYER) {
                break;
            }

            toMelt = up;
        }

        int snowHeight = toMelt.getIntValue(SNOW_HEIGHT) - layers;
        Block newState = snowHeight < 0 ? get(AIR) : getBlockState().withProperty(SNOW_HEIGHT, snowHeight).getBlock(toMelt);
        BlockFadeEvent event = new BlockFadeEvent(toMelt, newState);
        level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        return level.setBlock(toMelt, event.getNewState(), true);
    }

    
    @Override
    public Item toItem() {
        return BlockState.of(this.getPersistenceName()).asItemBlock();
    }

    
    @Override
    public Item[] getDrops(Item item) {
        if (!item.isShovel() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY;
        }

        int amount;
        switch (getSnowHeight()) {
            case 0:
            case 1:
            case 2:
                amount = 1;
                break;
            case 3:
            case 4:
                amount = 2;
                break;
            case 5:
            case 6:
                amount = 3;
                break;
            default:
            case 7:
                amount = 4;
        }
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
        return side == BlockFace.UP && getSnowHeight() == SNOW_HEIGHT.getMaxValue();
    }
}
