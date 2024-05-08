package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DirtType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project), kvetinac97
 */
public class BlockDirt extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DIRT, CommonBlockProperties.DIRT_TYPE);

    public BlockDirt() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDirt(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @NotNull
    public DirtType getDirtType() {
        return getPropertyValue(CommonBlockProperties.DIRT_TYPE);
    }

    public void setDirtType(@Nullable DirtType dirtType) throws Exception {
        setPropertyValue(CommonBlockProperties.DIRT_TYPE, dirtType);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        //Although the hardness on the wiki is 0.5, after testing, a hardness of 0.6 is more suitable for the vanilla
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return this.getDirtType() == DirtType.NORMAL ? "Dirt" : "Coarse Dirt";
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!this.up().canBeReplaced()) {
            return false;
        }

        if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, this.getDirtType() == DirtType.NORMAL ? get(FARMLAND) : get(DIRT), true);
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        } else if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH));
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{this.toItem()};
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }
}
