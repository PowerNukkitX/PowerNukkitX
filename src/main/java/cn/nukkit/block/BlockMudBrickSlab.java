package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickSlab extends BlockSlab{
    public BlockMudBrickSlab(int doubleSlab) {
        super(doubleSlab);
    }

    @Override
    public int getId() {
        return MUD_BRICK_SLAB;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return BlockSlab.SIMPLE_SLAB_PROPERTIES;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public String getSlabName() {
        return "Mud Brick Slab";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId();
    }
}
