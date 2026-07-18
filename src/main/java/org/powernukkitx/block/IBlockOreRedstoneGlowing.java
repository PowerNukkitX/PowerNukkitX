package org.powernukkitx.block;

import org.powernukkitx.event.block.BlockFadeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;

public interface IBlockOreRedstoneGlowing{

    Block getUnlitBlock();

    Block getLitBlock();

    Level getLevel();

    default Item toItem() {
        return getUnlitBlock().toItem();
    }

    default int onUpdate(Block block, int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_RANDOM) {
            Level level = getLevel();
            BlockFadeEvent event = new BlockFadeEvent(block, getUnlitBlock());
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                level.setBlock(block, event.getNewState(), true, true);
            }

            return Level.BLOCK_UPDATE_WEAK;
        }
        return 0;
    }
}
