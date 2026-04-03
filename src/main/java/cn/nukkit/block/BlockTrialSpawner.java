package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityID;
import cn.nukkit.blockentity.BlockEntityMobSpawner;
import cn.nukkit.blockentity.BlockEntityTrialSpawner;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class BlockTrialSpawner extends Block implements BlockEntityHolder<BlockEntityTrialSpawner> {

    public static final BlockProperties PROPERTIES = new BlockProperties(TRIAL_SPAWNER, CommonBlockProperties.OMINOUS, CommonBlockProperties.TRIAL_SPAWNER_STATE);

     @Override
     public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
         BlockEntityTrialSpawner spawner = BlockEntityHolder.setBlockAndCreateEntity(this, false, true);
         if(spawner != null) {
             spawner.spawnToAll();
             return true;
         }
         return false;
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTrialSpawner(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 50;
    }

    @Override
    public double getResistance() {
        return 50;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public @NotNull Class<? extends BlockEntityTrialSpawner> getBlockEntityClass() {
        return BlockEntityTrialSpawner.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntityID.TRIAL_SPAWNER;
    }
}