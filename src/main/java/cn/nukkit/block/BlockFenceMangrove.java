package cn.nukkit.block;


public class BlockFenceMangrove extends BlockFenceBase {


    public BlockFenceMangrove() {
        this(0);
    }


    public BlockFenceMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Fence";
    }

    @Override
    public int getId() {
        return MANGROVE_FENCE;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

}
