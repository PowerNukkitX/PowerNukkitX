package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DirtType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author MagicDroidX (Nukkit Project), kvetinac97
 */

public class BlockDirt extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dirt", CommonBlockProperties.DIRT_TYPE);

    public BlockDirt() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDirt(BlockState blockState) {
        super(blockState);
    }


    @NotNull
    @Override
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
        return 0.5;
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
    public boolean onActivate(@NotNull Item item, Player player) {
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
        return new Item[]{new ItemBlock(Block.get(BlockID.DIRT))};
    }

}
