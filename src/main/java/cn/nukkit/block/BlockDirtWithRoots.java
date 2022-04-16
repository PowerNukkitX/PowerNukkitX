package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author CoolLoong
 * @since 02.12.2022
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockDirtWithRoots extends BlockSolid {

    public BlockDirtWithRoots() {
    }

    @Override
    public int getId() {
        return DIRT_WITH_ROOTS;
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
    public boolean onActivate(@Nonnull Item item, Player player) {
        Vector3 vector = new Vector3(this.x, this.y - 1, this.z);
        if (!this.up().canBeReplaced()) {
            return false;
        }
        if (item.isFertilizer() && this.level.getBlock(vector).getId() == BlockID.AIR) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }
            this.level.addParticle(new BoneMealParticle(this));
            this.level.setBlock(vector, Block.get(BlockID.HANGING_ROOTS));
            return true;
        }
        if (item.isHoe()) {
            vector.setY(this.y + 1);
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(BlockID.DIRT), true);
            this.getLevel().dropItem(vector, new ItemBlock(Block.get(BlockID.HANGING_ROOTS)));
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        } else if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH));
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
        return new Item[]{new ItemBlock(Block.get(BlockID.DIRT_WITH_ROOTS))};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
