package cn.nukkit.block;


public class BlockButtonDarkOak extends BlockButtonWooden {

    public BlockButtonDarkOak() {
        this(0);
    }


    public BlockButtonDarkOak(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return DARK_OAK_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Dark Oak Button";
    }
}
