package cn.nukkit.block;

import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

public class BlockClosedEyeblossom extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(CLOSED_EYEBLOSSOM);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockClosedEyeblossom() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockClosedEyeblossom(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int onUpdate(int type) {
        if(type == Level.BLOCK_UPDATE_RANDOM || type == Level.BLOCK_UPDATE_SCHEDULED) {
            boolean changed = getId().equals(CLOSED_EYEBLOSSOM) && level.isNight() || getId().equals(OPEN_EYEBLOSSOM) && level.isDay();
            if(changed) {
                changeState();
                for(int x = -3; x <= 3; x++) {
                    for(int z = -3; z <= 3; z++) {
                        for(int y = -2; y <= 2; y++) {
                            Block block = level.getBlock(this.add(x, y, z));
                            if(block instanceof BlockClosedEyeblossom) {
                                level.scheduleUpdate(block, 1);
                            }
                        }
                    }
                }
            }
        }
        return type;
    }

    public void changeState() {
        this.getLevel().setBlock(this, Block.get(Block.OPEN_EYEBLOSSOM));
    }
}