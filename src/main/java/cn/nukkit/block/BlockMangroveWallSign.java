package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAcaciaSign;

public class BlockMangroveWallSign extends BlockWallSign {
    @PowerNukkitOnly
    public BlockMangroveWallSign() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockMangroveWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_WALL_SIGN;
    }

    @PowerNukkitOnly
    @Override
    protected int getPostId() {
        return MANGROVE_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Mangrove Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemAcaciaSign();
    }
}
