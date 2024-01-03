package cn.nukkit.block;


public class BlockBlueIce extends BlockIcePacked {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_ICE);

    public BlockBlueIce() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBlueIce(BlockState blockState) {
        super(blockState);
    }
    
    @Override
    public String getName() {
        return "Blue Ice";
    }
    
    @Override
    public double getFrictionFactor() {
        return 0.989;
    }
    
    @Override
    public double getHardness() {
        return 2.8;
    }

    @Override
    public double getResistance() {
        return 14;
    }

    @Override
    public boolean isTransparent() {
        return false;
    }

    @Override
    public int getLightLevel() {
        return 4;
    }
}
