package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * @author CoolLoong
 * @since 02.12.2022
 */
public class BlockDirtWithRoots extends BlockDirt {
    public static final BlockProperties PROPERTIES = new BlockProperties(DIRT_WITH_ROOTS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDirtWithRoots() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDirtWithRoots(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Dirt With Roots";
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        Vector3 vector = new Vector3(this.x, this.y - 1, this.z);
        if (!this.up().canBeReplaced()) {
            return false;
        }
        if (item.isFertilizer() && this.level.getBlock(vector).isAir()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }
            this.level.addParticle(new BoneMealParticle(this));
            this.level.setBlock(vector, Block.get(HANGING_ROOTS));
            return true;
        }
        if (item.isHoe()) {
            vector.setY(this.y+1);
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(DIRT), true);
            this.getLevel().dropItem(vector, new ItemBlock(Block.get(HANGING_ROOTS)));
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        } else if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(GRASS_PATH));
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new ItemBlock(Block.get(DIRT_WITH_ROOTS))};
    }
}
