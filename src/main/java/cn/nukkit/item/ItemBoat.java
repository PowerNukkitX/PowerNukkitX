package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Identifier;

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
        super(BOAT, meta, count);
    }

    public ItemBoat(String id) {
        super(id);
    }

    @Override
    public void internalAdjust() {
        switch (getDamage()) {
            case 0:
                name = "Oak Boat";
                this.id = ItemID.OAK_BOAT;
                this.identifier = new Identifier(ItemID.OAK_BOAT);
                return;
            case 1:
                name = "Spruce Boat";
                this.id = ItemID.SPRUCE_BOAT;
                this.identifier = new Identifier(ItemID.SPRUCE_BOAT);
                return;
            case 2:
                name = "Birch Boat";
                this.id = ItemID.BIRCH_BOAT;
                this.identifier = new Identifier(ItemID.BIRCH_BOAT);
                return;
            case 3:
                name = "Jungle Boat";
                this.id = ItemID.JUNGLE_BOAT;
                this.identifier = new Identifier(ItemID.JUNGLE_BOAT);
                return;
            case 4:
                name = "Acacia Boat";
                this.id = ItemID.ACACIA_BOAT;
                this.identifier = new Identifier(ItemID.ACACIA_BOAT);
                return;
            case 5:
                name = "Dark Oak Boat";
                this.id = ItemID.DARK_OAK_BOAT;
                this.identifier = new Identifier(ItemID.DARK_OAK_BOAT);
                return;
            case 6:
                name = "Mangrove Boat";
                this.id = ItemID.MANGROVE_BOAT;
                this.identifier = new Identifier(ItemID.MANGROVE_BOAT);
                return;
            case 7:
                name = "Bamboo Raft";
                this.id = ItemID.BAMBOO_RAFT;
                this.identifier = new Identifier(ItemID.BAMBOO_RAFT);
                return;
            case 8:
                name = "Cherry Boat";
                this.id = ItemID.CHERRY_BOAT;
                this.identifier = new Identifier(ItemID.CHERRY_BOAT);
        }
        this.meta = 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public int getBoatId() {
        return this.meta;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (face != BlockFace.UP || block instanceof BlockFlowingWater) return false;
        EntityBoat boat = (EntityBoat) Entity.createEntity(Entity.BOAT,
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), new CompoundTag()
                        .putList("Pos", new ListTag<DoubleTag>()
                                .add(new DoubleTag(block.getX() + 0.5))
                                .add(new DoubleTag(block.getY() - (target instanceof BlockFlowingWater ? 0.375 : 0)))
                                .add(new DoubleTag(block.getZ() + 0.5)))
                        .putList("Motion", new ListTag<DoubleTag>()
                                .add(new DoubleTag(0))
                                .add(new DoubleTag(0))
                                .add(new DoubleTag(0)))
                        .putList("Rotation", new ListTag<FloatTag>()
                                .add(new FloatTag((float) ((player.yaw + 90f) % 360)))
                                .add(new FloatTag(0)))
                        .putInt("Variant", getBoatId())
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
