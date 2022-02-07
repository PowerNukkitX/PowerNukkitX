package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.utils.BlockColor;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockOreRedstoneDeepslate extends BlockOreRedstone {

    @PowerNukkitOnly
    @Since("FUTURE")
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

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public BlockState getLitState() {
        return BlockState.of(LIT_DEEPSLATE_REDSTONE_ORE);
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public BlockState getUnlitState() {
        return BlockState.of(DEEPSLATE_REDSTONE_ORE);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }
}
