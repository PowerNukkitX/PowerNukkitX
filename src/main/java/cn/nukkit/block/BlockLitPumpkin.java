package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.mob.EntityIronGolem;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockLitPumpkin extends BlockPumpkin {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_PUMPKIN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitPumpkin() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitPumpkin(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jack o'Lantern";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if(!super.place(item, block, target, face, fx, fy, fz, player)) return false;
        EntityIronGolem.checkAndSpawnGolem(this, player);
        return true;
    }

    @Override
    public boolean canBePickedUp() {
        return false;
    }
}