package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntitySmallFireBall;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;


public class BlockPowderSnow extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:powder_snow");

    public BlockPowderSnow() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPowderSnow(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Powder Snow";
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public boolean isSolid() {
        return false;
    }


    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }


    @Override
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        if (projectile instanceof EntitySmallFireBall) {
            this.getLevel().useBreakOn(this);
            return true;
        }
        return false;
    }
}
