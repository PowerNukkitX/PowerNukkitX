package cn.nukkit.block;

public class BlockCommandBlockRepeating extends BlockCommandBlock {

    public BlockCommandBlockRepeating() {
        this(0);
    }

    public BlockCommandBlockRepeating(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return BlockID.REPEATING_COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Repeating Command Block";
    }

}
