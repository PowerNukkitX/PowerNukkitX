package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;


public abstract class BlockSegmented extends BlockFlowable implements Faceable {

    public BlockSegmented(BlockState blockState) {
        super(blockState);
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    public abstract boolean isSupportValid(Block block);

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if(down.getId().equals(item.getId()))  {
            block = down;
            down = block.down();
        } else setBlockFace(player != null ? player.getDirection().rotateYCCW() : BlockFace.SOUTH);
        if (isSupportValid(down)) {
            int currentMeta = -1;
            if (block instanceof BlockSegmented) {
                currentMeta = block.getPropertyValue(CommonBlockProperties.GROWTH);
            }
            if(currentMeta >= 3) return false;
            setPropertyValue(CommonBlockProperties.GROWTH, currentMeta+1);
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(this.getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    public Item[] getDrops(Item item) {
        Item drop = toItem();
        drop.setCount(getPropertyValue(CommonBlockProperties.GROWTH));
        return new Item[] {
                drop
        };
    }

}
