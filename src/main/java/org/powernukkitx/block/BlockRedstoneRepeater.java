package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemRepeater;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.REPEATER_DELAY;


public abstract class BlockRedstoneRepeater extends BlockRedstoneDiode {
    public BlockRedstoneRepeater(BlockState blockState) {
        super(blockState);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        int repeaterDelay = getPropertyValue(REPEATER_DELAY);
        if (repeaterDelay == 3) {
            setPropertyValue(REPEATER_DELAY, 0);
        } else {
            setPropertyValue(REPEATER_DELAY, repeaterDelay + 1);
        }

        this.level.setBlock(this, this, true, true);
        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isSupportValid(down())) {
            return false;
        }
        BlockFace blockFace = player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) : BlockFace.SOUTH;
        setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(blockFace));
        if (!this.level.setBlock(block, this, true, true)) {
            return false;
        }

        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
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
    protected boolean isAlternateInput(Block block) {
        return isDiode(block);
    }

    @Override
    public Item toItem() {
        return new ItemRepeater();
    }

    @Override
    protected int getDelay() {
        return (1 + getPropertyValue(REPEATER_DELAY)) * 2;
    }

    @Override
    public boolean isLocked() {
        return this.getPowerOnSides() > 0;
    }
}
