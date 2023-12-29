package cn.nukkit.block;


public class BlockButtonSpruce extends BlockButtonWooden {

    public BlockButtonSpruce() {
        this(0);
    }


    public BlockButtonSpruce(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return SPRUCE_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Spruce Button";
    }
}
