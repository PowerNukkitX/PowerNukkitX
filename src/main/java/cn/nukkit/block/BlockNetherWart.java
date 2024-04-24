package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_4;

/**
 * @author Leonidius20
 * @since 22.03.17
 */
public class BlockNetherWart extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_WART, AGE_4);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherWart() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherWart(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (down.getId().equals(SOUL_SAND)) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.down().getId().equals(SOUL_SAND)) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (new Random().nextInt(10) == 1) {
                if (this.getAge() < 0x03) {
                    BlockNetherWart block = (BlockNetherWart) this.clone();
                    block.setAge(block.getAge() + 1);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public String getName() {
        return "Nether Wart Block";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getAge() == 0x03) {
            return new Item[]{
                    new ItemBlock(this, 0, 2 + (int) (Math.random() * ((4 - 2) + 1)))
            };
        } else {
            return new Item[]{
                    toItem()
            };
        }
    }

    public int getAge() {
        return getPropertyValue(AGE_4);
    }

    public void setAge(int age) {
        setPropertyValue(AGE_4, age);
    }
}


