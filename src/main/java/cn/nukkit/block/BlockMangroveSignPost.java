package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMangroveSign;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockMangroveSignPost extends BlockSignPost {
    @PowerNukkitOnly
    public BlockMangroveSignPost() {
    }

    @PowerNukkitOnly
    public BlockMangroveSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_STANDING_SIGN;
    }

    @PowerNukkitOnly
    @Override
    public int getWallId() {
        return MANGROVE_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Mangrove Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemMangroveSign();
    }
}
