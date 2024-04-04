package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLodestone;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemLodestoneCompass;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

/**
 * @author joserobjr
 */


@Slf4j
public class BlockLodestone extends BlockSolid implements BlockEntityHolder<BlockEntityLodestone> {
    public static final BlockProperties PROPERTIES = new BlockProperties(LODESTONE);
    public BlockLodestone() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockLodestone(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public Class<? extends BlockEntityLodestone> getBlockEntityClass() {
        return BlockEntityLodestone.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.LODESTONE;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null || item.isNull() || !Objects.equals(item.getId(), ItemID.COMPASS) && !Objects.equals(item.getId(), ItemID.LODESTONE_COMPASS)) {
            return false;
        }

        ItemLodestoneCompass compass = (ItemLodestoneCompass) Item.get(ItemID.LODESTONE_COMPASS);
        if (item.hasCompoundTag()) {
            compass.setCompoundTag(item.getCompoundTag().clone());
        }
        
        int trackingHandle;
        try {
            trackingHandle = getOrCreateBlockEntity().requestTrackingHandler();
            compass.setTrackingHandle(trackingHandle);
        } catch (Exception e) {
            log.warn("Could not create a lodestone compass to {} for {}", getLocation(), player.getName(), e);
            return false;
        }

        boolean added = true;
        if (item.getCount() == 1) {
            player.getInventory().setItemInHand(compass);
        } else {
            Item clone = item.clone();
            clone.count--;
            player.getInventory().setItemInHand(clone);
            for (Item failed : player.getInventory().addItem(compass)) {
                added = false;
                player.getLevel().dropItem(player.getPosition(), failed);
            }
        }
        
        getLevel().addSound(player.getPosition(), Sound.LODESTONE_COMPASS_LINK_COMPASS_TO_LODESTONE);
        
        if (added) {
            try {
                getLevel().getServer().getPositionTrackingService().startTracking(player, trackingHandle, false);
            } catch (IOException e) {
                log.warn("Failed to make the player {} track {} at {}", player.getName(), trackingHandle, getLocation(),  e);
            }
            getLevel().getServer().getScheduler().scheduleTask(null, player::updateTrackingPositions);
        }
        
        return true;
    }

    @Override
    public String getName() {
        return "Lodestone";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public  boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}
