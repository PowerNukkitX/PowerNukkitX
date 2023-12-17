package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.IMutableBlockState;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public interface IBlockOreRedstoneGlowing extends IMutableBlockState {


    BlockState getUnlitState();


    BlockState getLitState();


    Level getLevel();


    default Item toItem() {
        return getUnlitState().asItemBlock();
    }


    default int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_RANDOM) {
            Block block = getBlock();
            Level level = getLevel();
            BlockFadeEvent event = new BlockFadeEvent(block, getUnlitState().getBlock());
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                level.setBlock(block, event.getNewState(), false, true);
            }

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }
}
