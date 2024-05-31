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
    public static final BlockProperties $1 = new BlockProperties(SNOW_LAYER, COVERED_BIT, HEIGHT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSnowLayer() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSnowLayer(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Top Snow";
    }
    /**
     * @deprecated 
     */
    

    public int getSnowHeight() {
        return getPropertyValue(HEIGHT);
    }
    /**
     * @deprecated 
     */
    

    public void setSnowHeight(int snowHeight) {
        setPropertyValue(HEIGHT, snowHeight);
    }
    /**
     * @deprecated 
     */
    

    public boolean isCovered() {
        return getPropertyValue(COVERED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setCovered(boolean covered) {
        setPropertyValue(COVERED_BIT, covered);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return y + (Math.min(16, getSnowHeight() + 1) * 2) / 16.0;
    }

    @Override
    protected @Nullable AxisAlignedBB recalculateBoundingBox() {
        int $2 = getSnowHeight();
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
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return getSnowHeight() < HEIGHT.getMax();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Optional<BlockSnowLayer> increment = Stream.of(target, block)
                .filter(b -> b.getId().equals(SNOW_LAYER)).map(BlockSnowLayer.class::cast)
                .filter(b -> b.getSnowHeight() < HEIGHT.getMax())
                .findFirst();

        if (increment.isPresent()) {
            BlockSnowLayer $3 = increment.get();
            if (Arrays.stream(level.getCollidingEntities(new SimpleAxisAlignedBB(
                    other.x, other.y, other.z,
                    other.x + 1, other.y + 1, other.z + 1
            ))).anyMatch(e -> e instanceof EntityLiving)) {
                return false;
            }
            other.setSnowHeight(other.getSnowHeight() + 1);
            return level.setBlock(other, other, true);
        }

        Block $4 = down();
        if (!down.isSolid()) {
            return false;
        }

        switch (down.getId()) {
            case BARRIER, STRUCTURE_VOID -> {
                return false;
            }
            case GRASS_BLOCK -> setCovered(true);
            case TALLGRASS -> {
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
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        if (layer != 0) {
            return super.onBreak(item);
        }
        return this.getLevel().setBlock(this, 0, getLevelBlockAtLayer(1), true, true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void afterRemoval(Block newBlock, boolean update) {
        if (layer != 0 || newBlock.getId().equals(getId())) {
            return;
        }

        Block $5 = getLevelBlockAtLayer(1);
        if (!layer1.getId().equals(TALLGRASS)) {
            return;
        }

        // Clear the layer1 block and do a small hack as workaround a vanilla client rendering bug
        Level $6 = getLevel();
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
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        super.onUpdate(type);
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            BiomeRegistry.BiomeDefinition $7 = Registries.BIOME.get(getLevel().getBiomeId(getFloorX(), this.getFloorY(), getFloorZ()));
            if (biomeDefinition.tags().contains(BiomeTags.WARM) || this.getLevel().getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) >= 10) {
                melt();
                return Level.BLOCK_UPDATE_RANDOM;
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            boolean $8 = down().getId().equals(GRASS_BLOCK);
            if (isCovered() != covered) {
                setCovered(covered);
                level.setBlock(this, this, true);
                return type;
            }
        }
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean melt() {
        return melt(2);
    }
    /**
     * @deprecated 
     */
    

    public boolean melt(int layers) {
        Preconditions.checkArgument(layers > 0, "Layers must be positive, got {}", layers);
        Block $9 = this;
        while (toMelt.getPropertyValue(HEIGHT) == HEIGHT.getMax()) {
            Block $10 = toMelt.up();
            if (!up.getId().equals(SNOW_LAYER)) {
                break;
            }

            toMelt = up;
        }

        int $11 = toMelt.getPropertyValue(HEIGHT) - layers;
        Block $12 = snowHeight < 0 ? get(AIR) : Block.get(getBlockState().setPropertyValue(PROPERTIES, HEIGHT, snowHeight));
        BlockFadeEvent $13 = new BlockFadeEvent(toMelt, newState);
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

        int $14 = switch (getSnowHeight()) {
            case 0, 1, 2 -> 1;
            case 3, 4 -> 2;
            case 5, 6 -> 3;
            default -> 4;
        };
        return new Item[]{Item.get(ItemID.SNOWBALL, 0, amount)};
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return getSnowHeight() < 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return $15 == BlockFace.UP && getSnowHeight() == HEIGHT.getMax();
    }
}
