package cn.nukkit.block;


public class BlockTrapdoorAcacia extends BlockTrapdoor {

    public BlockTrapdoorAcacia() {
        this(0);
    }


    public BlockTrapdoorAcacia(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return ACACIA_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Acacia Trapdoor";
    }

}
