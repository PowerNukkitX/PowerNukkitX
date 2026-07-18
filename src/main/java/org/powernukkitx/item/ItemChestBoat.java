package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityChestBoat;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

public class ItemChestBoat extends Item {
    public ItemChestBoat() {
        this(0, 1);
    }

    public ItemChestBoat(Integer meta) {
        this(meta, 1);
    }

    //legacy chest boat , have aux
    public ItemChestBoat(Integer meta, int count) {
        super(CHEST_BOAT, meta, count);
        adjustName();
    }

    //chest boat item after split aux
    public ItemChestBoat(String id) {
        super(id);
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        adjustName();
    }

    private void adjustName() {
        switch (getDamage()) {
            case 0 -> name = "Oak Chest Boat";
            case 1 -> name = "Spruce Chest Boat";
            case 2 -> name = "Birch Chest Boat";
            case 3 -> name = "Jungle Chest Boat";
            case 4 -> name = "Acacia Chest Boat";
            case 5 -> name = "Dark Oak Chest Boat";
            case 6 -> name = "Mangrove Chest Boat";
            case 7 -> name = "Bamboo Chest Raft";
            case 8 -> name = "Cherry Chest Boat";
            case 9 -> name = "Pale Oak Chest Boat";
            default -> name = "Chest Boat";
        }
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
        if (face != BlockFace.UP) return false;
        if (block instanceof BlockFlowingWater) block = block.up();
        EntityChestBoat boat = (EntityChestBoat) Entity.createEntity(Entity.CHEST_BOAT,
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
            Item item = player.getInventory().getItemInMainHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInMainHand(item);
        }

        boat.spawnToAll();
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
