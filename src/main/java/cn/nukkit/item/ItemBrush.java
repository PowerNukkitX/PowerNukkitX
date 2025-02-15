package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

/**
 * todo feature
 */
public class ItemBrush extends ItemTool {
    public ItemBrush() {
        super(BRUSH);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        player.setDataFlag(EntityFlag.USING_ITEM, true);
        return super.onActivate(level, player, block, target, face, fx, fy, fz);
    }
}