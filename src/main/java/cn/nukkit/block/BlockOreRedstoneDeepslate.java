package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;

/**
 * @author joserobjr
 * @since 2021-06-13
 */


public class BlockOreRedstoneDeepslate extends BlockOreRedstone {


    public BlockOreRedstoneDeepslate() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DEEPSLATE_REDSTONE_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Redstone Ore";
    }


    @Override
    public BlockState getLitState() {
        return BlockState.of(LIT_DEEPSLATE_REDSTONE_ORE);
    }


    @Override
    public BlockState getUnlitState() {
        return BlockState.of(DEEPSLATE_REDSTONE_ORE);
    }

}
