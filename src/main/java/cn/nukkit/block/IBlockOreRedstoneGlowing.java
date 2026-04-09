package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Dimension;

public interface IBlockOreRedstoneGlowing{

    Block getUnlitBlock();

    Block getLitBlock();

    Dimension getLevel();

    default Item toItem() {
        return getUnlitBlock().toItem();
    }

    default int onUpdate(Block block, int type) {
        if (type == Dimension.BLOCK_UPDATE_SCHEDULED || type == Dimension.BLOCK_UPDATE_RANDOM) {
            Dimension level = getLevel();
            BlockFadeEvent event = new BlockFadeEvent(block, getUnlitBlock());
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                level.setBlock(block, event.getNewState(), true, true);
            }

            return Dimension.BLOCK_UPDATE_WEAK;
        }
        return 0;
    }
}
