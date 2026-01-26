package cn.nukkit.item;


import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBeehive;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.item.EntityAreaEffectCloud;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle() {
        this(0, 1);
    }

    public ItemGlassBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemGlassBottle(Integer meta, int count) {
        super(GLASS_BOTTLE, meta, count, "Glass Bottle");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face,
                              double fx, double fy, double fz) {
        if (player == null) {
            return false;
        }

        Item filled = null;

        var box = player.getBoundingBox().grow(1.1, 1.1, 1.1);
        var collidingEntities = level.getCollidingEntities(box);

        for (var entity : collidingEntities) {
            if (entity instanceof EntityAreaEffectCloud cloud && cloud.isDragonBreath()) {
                filled = new ItemDragonBreath();
                cloud.setRadius(cloud.getRadius() - 1, true);
                break;
            }
        }

        if (filled == null) {
            String targetId = target.getId();
            if (targetId.equals(BlockID.WATER) || targetId.equals(BlockID.FLOWING_WATER)) {
                filled = new ItemPotion();
            } else if (target instanceof BlockBeehive beehive && beehive.isFull()) {
                filled = Item.get(HONEY_BOTTLE);
                beehive.honeyCollected(player);
                level.addSound(player, Sound.BUCKET_FILL_WATER);
            }
        }

        if (filled == null) {
            return false;
        }

        if (this.count == 1) {
            player.getInventory().setItemInHand(filled);
        } else {
            this.count--;
            player.getInventory().setItemInHand(this);

            if (player.getInventory().canAddItem(filled)) {
                player.getInventory().addItem(filled);
            } else {
                level.dropItem(player.add(0, 1.3, 0), filled, player.getDirectionVector().multiply(0.4));
            }
        }

        level.getVibrationManager().callVibrationEvent(
                new VibrationEvent(
                        player,
                        target.add(0.5, 0.5, 0.5),
                        VibrationType.FLUID_PICKUP
                )
        );

        return false;
    }
}
