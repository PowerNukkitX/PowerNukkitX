package cn.nukkit.block;

import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_16;

public class BlockSoulFire extends BlockFire {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_FIRE, AGE_16);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulFire() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulFire(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Soul Fire Block";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            String downId = down().getId();
            if (!downId.equals(Block.SOUL_SAND) && !downId.equals(Block.SOUL_SOIL)) {
                this.getLevel().setBlock(this, Block.get(FIRE).setPropertyValue(AGE_16, this.getAge()));
            }
            return type;
        }
        return 0;
    }
}