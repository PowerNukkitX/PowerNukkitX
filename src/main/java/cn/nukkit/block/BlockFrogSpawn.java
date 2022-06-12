package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockFrogSpawn extends BlockFlowable{

    @Override
    public String getName() {
        return "Frog Spawn";
    }

    @Override
    public int getId() {
        return FROG_SPAWN;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (supportable(block)){
            if (block.getId() == Block.AIR)
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
                || under.getId() == STILL_WATER
                || under.getLevelBlockAtLayer(1).getId() == FLOWING_WATER
                || under.getLevelBlockAtLayer(1).getId() == STILL_WATER;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
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
