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
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;
import java.util.Random;

public class ItemEndCrystal extends Item {

    public ItemEndCrystal() {
        this(0, 1);
    }

    public ItemEndCrystal(Integer meta) {
        this(meta, 1);
    }

    public ItemEndCrystal(Integer meta, int count) {
        super(END_CRYSTAL, meta, count, "End Crystal");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (!(target instanceof BlockBedrock) && !(target instanceof BlockObsidian)) return false;
        IChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);
        Entity[] entities = level.getNearbyEntities(new SimpleAxisAlignedBB(target.x, target.y, target.z, target.x + 1, target.y + 2, target.z + 1));
        Block up = target.up();

        if (chunk == null || !up.getId().equals(BlockID.AIR) || !up.up().getId().equals(BlockID.AIR) || entities.length != 0) {
            return false;
        }

        NbtMap nbt = NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                target.x + 0.5,
                                up.y,
                                target.z + 0.5
                        )
                ).putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0)
                ).putList("Rotation", NbtType.FLOAT, Arrays.asList(new Random().nextFloat() * 360, 0f)
                ).build();

        if (this.hasCustomName()) {
            nbt = nbt.toBuilder().putString("CustomName", this.getCustomName()).build();
        }

        Entity entity = Entity.createEntity(Entity.ENDER_CRYSTAL, chunk, nbt);

        if (entity != null) {
            if (player.isAdventure() || player.isSurvival()) {
                Item item = player.getInventory().getItemInMainHand();
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInMainHand(item);
            }
            entity.spawnToAll();
            return true;
        }
        return false;
    }
}
