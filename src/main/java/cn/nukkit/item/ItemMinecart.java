package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRail;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecart;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Rail;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemMinecart extends Item {

    public ItemMinecart() {
        this(0, 1);
    }

    public ItemMinecart(Integer meta) {
        this(meta, 1);
    }

    public ItemMinecart(Integer meta, int count) {
        super(MINECART, meta, count, "Minecart");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (Rail.isRailBlock(target)) {
            Rail.Orientation type = ((BlockRail) target).getOrientation();
            double adjacent = 0.0D;
            if (type.isAscending()) {
                adjacent = 0.5D;
            }
            EntityMinecart minecart = (EntityMinecart) Entity.createEntity(Entity.MINECART,
                    level.getChunk(target.getFloorX() >> 4, target.getFloorZ() >> 4), NbtMap.builder()
                            .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                            target.getX() + 0.5,
                                            target.getY() + 0.0625D + adjacent,
                                            target.getZ() + 0.5
                                    )
                            ).putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0)
                            ).putList("Rotation", NbtType.FLOAT, Arrays.asList(0f, 0f)
                            ).build()
            );

            if (minecart == null) {
                return false;
            }

            if (player.isAdventure() || player.isSurvival()) {
                Item item = player.getInventory().getItemInMainHand();
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInMainHand(item);
            }

            minecart.spawnToAll();
            return true;
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
