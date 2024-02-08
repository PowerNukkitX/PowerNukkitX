package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityJukebox;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemMusicDisc;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 * @since 7.8.2017
 */
public class BlockJukebox extends BlockSolid implements BlockEntityHolder<BlockEntityJukebox> {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUKEBOX);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJukebox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJukebox(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jukebox";
    }

    @Override
    @NotNull public Class<? extends BlockEntityJukebox> getBlockEntityClass() {
        return BlockEntityJukebox.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.JUKEBOX;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        BlockEntityJukebox jukebox = getOrCreateBlockEntity();
        if (!jukebox.getRecordItem().isNull()) {
            jukebox.dropItem();
            return true;
        }

        if (!item.isNull() && item instanceof ItemMusicDisc) {
            Item record = item.clone();
            record.count = 1;
            item.count--;
            jukebox.setRecordItem(record);
            jukebox.play();
            return true;
        }

        return false;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

}
