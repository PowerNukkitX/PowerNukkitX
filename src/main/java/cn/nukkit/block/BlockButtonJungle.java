package cn.nukkit.block;


public class BlockButtonJungle extends BlockButtonWooden {

    public BlockButtonJungle() {
        this(0);
    }


    public BlockButtonJungle(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return JUNGLE_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Jungle Button";
    }
}
