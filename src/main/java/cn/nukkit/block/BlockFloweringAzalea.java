package cn.nukkit.block;

public class BlockFloweringAzalea extends BlockAzalea{


    public BlockFloweringAzalea() {
        this(0);
    }


    public BlockFloweringAzalea(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getName() {
        return "FloweringAzalea";
    }

    @Override
    public int getId() {
        return FLOWERING_AZALEA;
    }
}
