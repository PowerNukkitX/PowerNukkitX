package cn.nukkit.block;

import static cn.nukkit.block.property.CommonBlockProperties.DIRECTION;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRedstoneRepeater;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockRedstoneRepeater extends BlockRedstoneDiode {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected static final IntBlockProperty REPEATER_DELAY = new IntBlockProperty("repeater_delay", false, 3);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES =
            new BlockProperties(CommonBlockProperties.CARDINAL_DIRECTION, REPEATER_DELAY);

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockRedstoneRepeater() {
        super(0);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        int repeaterDelay = getPropertyValue(REPEATER_DELAY);
        if (repeaterDelay == 3) {
            setPropertyValue(REPEATER_DELAY, 0);
        } else {
            setPropertyValue(REPEATER_DELAY, repeaterDelay + 1);
        }

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @Override
    public boolean place(
            @NotNull Item item,
            @NotNull Block block,
            @NotNull Block target,
            @NotNull BlockFace face,
            double fx,
            double fy,
            double fz,
            Player player) {
        if (!isSupportValid(down())) {
            return false;
        }

        setPropertyValue(
                DIRECTION,
                player != null
                        ? BlockFace.fromHorizontalIndex(
                                player.getDirection().getOpposite().getHorizontalIndex())
                        : BlockFace.SOUTH);
        if (!this.getLevel().setBlock(block, this, true, true)) {
            return false;
        }

        if (this.getLevel().getServer().isRedstoneEnabled()) {
            if (shouldBePowered()) {
                this.getLevel().scheduleUpdate(this, 1);
            }
        }
        return true;
    }

    @Override
    public BlockFace getFacing() {
        return getPropertyValue(DIRECTION);
    }

    @Override
    protected boolean isAlternateInput(Block block) {
        return isDiode(block);
    }

    @Override
    public Item toItem() {
        return new ItemRedstoneRepeater();
    }

    @Override
    protected int getDelay() {
        return (1 + getIntValue(REPEATER_DELAY)) * 2;
    }

    @Override
    public boolean isLocked() {
        return this.getPowerOnSides() > 0;
    }
}
