package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockFrogSpawn extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(FROG_SPAWN);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFrogSpawn() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockFrogSpawn(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Frog Spawn";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (supportable(block)){
            if (block.getId().equals(Block.AIR))
                return super.place(item, block, target, face, fx, fy, fz, player);
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (!supportable(this)) this.onBreak(null);
        return super.onUpdate(type);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity instanceof EntityFallingBlock)
            this.onBreak(null);
    }

    public boolean supportable(Position pos){
        Block under = pos.getSide(BlockFace.DOWN).getLevelBlock();
        return under.getId() == FLOWING_WATER
                || under.getId() == WATER
                || under.getLevelBlockAtLayer(1).getId() == FLOWING_WATER
                || under.getLevelBlockAtLayer(1).getId() == WATER;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }
}
