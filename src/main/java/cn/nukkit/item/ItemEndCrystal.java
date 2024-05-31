package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockObsidian;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Random;

public class ItemEndCrystal extends Item {
    /**
     * @deprecated 
     */
    

    public ItemEndCrystal() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEndCrystal(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEndCrystal(Integer meta, int count) {
        super(END_CRYSTAL, meta, count, "End Crystal");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (!(target instanceof BlockBedrock) && !(target instanceof BlockObsidian)) return false;
        IChunk $1 = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);
        Entity[] entities = level.getNearbyEntities(new SimpleAxisAlignedBB(target.x, target.y, target.z, target.x + 1, target.y + 2, target.z + 1));
        Block $2 = target.up();

        if (chunk == null || !up.getId().equals(BlockID.AIR) || !up.up().getId().equals(BlockID.AIR) || entities.length != 0) {
            return false;
        }

        CompoundTag $3 = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(target.x + 0.5))
                        .add(new DoubleTag(up.y))
                        .add(new DoubleTag(target.z + 0.5)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(new Random().nextFloat() * 360))
                        .add(new FloatTag(0)));

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        Entity $4 = Entity.createEntity(Entity.ENDER_CRYSTAL, chunk, nbt);

        if (entity != null) {
            if (player.isAdventure() || player.isSurvival()) {
                Item $5 = player.getInventory().getItemInHand();
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInHand(item);
            }
            entity.spawnToAll();
            return true;
        }
        return false;
    }
}
