package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRepeater;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.REPEATER_DELAY;


public abstract class BlockRedstoneRepeater extends BlockRedstoneDiode {
    /**
     * @deprecated 
     */
    
    public BlockRedstoneRepeater(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        int $1 = getPropertyValue(REPEATER_DELAY);
        if (repeaterDelay == 3) {
            setPropertyValue(REPEATER_DELAY, 0);
        } else {
            setPropertyValue(REPEATER_DELAY, repeaterDelay + 1);
        }

        this.level.setBlock(this, this, true, true);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isSupportValid(down())) {
            return false;
        }
        BlockFace $2 = player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) : BlockFace.SOUTH;
        setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(blockFace));
        if (!this.level.setBlock(block, this, true, true)) {
            return false;
        }

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            if (shouldBePowered()) {
                this.level.scheduleUpdate(this, 1);
            }
        }
        return true;
    }

    @Override
    public BlockFace getFacing() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected boolean isAlternateInput(Block block) {
        return isDiode(block);
    }

    @Override
    public Item toItem() {
        return new ItemRepeater();
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected int getDelay() {
        return (1 + getPropertyValue(REPEATER_DELAY)) * 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLocked() {
        return this.getPowerOnSides() > 0;
    }
}
