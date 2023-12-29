package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author Angelic47 (Nukkit Project)
 */

public class BlockBeacon extends BlockTransparent implements BlockEntityHolder<BlockEntityBeacon> {
    public static final BlockProperties PROPERTIES = new BlockProperties(BEACON);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBeacon() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBeacon(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull
    @Override
    public Class<? extends BlockEntityBeacon> getBlockEntityClass() {
        return BlockEntityBeacon.class;
    }


    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.BEACON;
    }

    @Override
    public double getHardness() {
        return 3;
    }


    @NotNull
    @Override
    public double calculateBreakTime(@NotNull Item item) {
        return calculateBreakTime(item, null);
    }


    @NotNull
    @Override
    public double calculateBreakTime(@NotNull Item item, @org.jetbrains.annotations.Nullable Player player) {
        return 4.5;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Beacon";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }


    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        if (player == null) {
            return false;
        }

        getOrCreateBlockEntity();
        player.addWindow(new BeaconInventory(player.getUIInventory(), this), Player.BEACON_WINDOW_ID);
        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override

    public  boolean canBePulled() {
        return false;
    }
}
