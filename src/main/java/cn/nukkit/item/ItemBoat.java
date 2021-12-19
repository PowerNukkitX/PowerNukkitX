package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * @author yescallop
 * @since 2016/2/13
 */
public class ItemBoat extends Item {

    public ItemBoat() {
        this(0, 1);
    }

    public ItemBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemBoat(Integer meta, int count) {
        this(BOAT, meta, count, "Boat");
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected ItemBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
        adjustName();
    }

    @Override
    public void setDamage(Integer meta) {
        super.setDamage(meta);
        adjustName();
    }

    private void adjustName() {
        switch (getDamage()) {
            case 0:
                name = "Oak Boat";
                return;
            case 1:
                name = "Spruce Boat";
                return;
            case 2:
                name = "Birch Boat";
                return;
            case 3:
                name = "Jungle Boat";
                return;
            case 4:
                name = "Acacia Boat";
                return;
            case 5:
                name = "Dark Oak Boat";
                return;
            default:
                name = "Boat";
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (face != BlockFace.UP && !(block instanceof BlockWater)) return false;
        EntityBoat boat = (EntityBoat) Entity.createEntity("Boat",
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), new CompoundTag("")
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", block.getX() + 0.5))
                        .add(new DoubleTag("", block.getY() - (target instanceof BlockWater ? 0.375 : 0)))
                        .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) ((player.yaw + 90f) % 360)))
                        .add(new FloatTag("", 0)))
                .putInt("Variant", getDamage())
        );

        if (boat == null) {
            return false;
        }

        if (player.isSurvival() || player.isAdventure()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        boat.spawnToAll();
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
