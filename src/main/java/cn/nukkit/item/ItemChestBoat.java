package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityChestBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;

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
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), NbtMap.builder()
                        .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                        block.getX() + 0.5,
                                        block.getY() - (target instanceof BlockFlowingWater ? 0.375 : 0),
                                        block.getZ() + 0.5
                                )
                        ).putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0)
                        ).putList("Rotation", NbtType.FLOAT, Arrays.asList(
                                        (float) ((player.yaw + 90f) % 360), 0f
                                )
                        ).putInt("Variant", this.getBoatId())
                        .build()
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
