package cn.nukkit.block;

public class BlockCommandBlockChain extends BlockCommandBlock {

    public BlockCommandBlockChain() {
        this(0);
    }

    public BlockCommandBlockChain(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return BlockID.CHAIN_COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Chain Command Block";
    }

}
