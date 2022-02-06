package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.IMutableBlockState;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public interface IBlockOreRedstoneGlowing extends IMutableBlockState {
    @PowerNukkitOnly
    @Since("FUTURE")
    BlockState getUnlitState();

    @PowerNukkitOnly
    @Since("FUTURE")
    BlockState getLitState();

    @PowerNukkitOnly
    @Since("FUTURE")
    Level getLevel();

    @PowerNukkitOnly
    @Since("FUTURE")
    default Item toItem() {
        return getUnlitState().asItemBlock();
    }

    @PowerNukkitOnly
    @Since("FUTURE")
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
