package cn.nukkit.block;


public class BlockButtonCherry extends BlockButtonWooden {

    public BlockButtonCherry() {
        this(0);
    }


    public BlockButtonCherry(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return CHERRY_BUTTON;
    }

    @Override
    public String getName() {
        return "Cherry Button";
    }
}