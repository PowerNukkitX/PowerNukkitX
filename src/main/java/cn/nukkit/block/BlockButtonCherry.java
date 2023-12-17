package cn.nukkit.block;


public class BlockButtonCherry extends BlockButtonWooden {

    public BlockButtonCherry() {
        this(0);
    }


    public BlockButtonCherry(int meta) {
        super(meta);
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