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

import java.util.Arrays;

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
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        Item filled = null;
        if(player != null && Arrays.stream(level.getCollidingEntities(player.getBoundingBox().grow(1.1, 1.1, 1.1))).anyMatch(entity -> entity instanceof EntityAreaEffectCloud cloud && cloud.isDragonBreath())) {
            filled = new ItemDragonBreath();
            Arrays.stream(level.getCollidingEntities(player.getBoundingBox().grow(1.1, 1.1, 1.1))).filter(entity -> entity instanceof EntityAreaEffectCloud cloud && cloud.isDragonBreath()).findAny().ifPresent(entity -> ((EntityAreaEffectCloud) entity).setRadius(((EntityAreaEffectCloud) entity).getRadius()-1, true));
        } else if (target.getId().equals(BlockID.FLOWING_WATER) || target.getId().equals(BlockID.WATER)) {
            filled = new ItemPotion();
        } else if (target instanceof BlockBeehive && ((BlockBeehive) target).isFull()) {
            filled = Item.get(HONEY_BOTTLE);
            ((BlockBeehive) target).honeyCollected(player);
            level.addSound(player, Sound.BUCKET_FILL_WATER);
        }
        
        if (filled != null) {
            if (this.count == 1) {
                player.getInventory().setItemInHand(filled);
            } else if (this.count > 1) {
                this.count--;
                player.getInventory().setItemInHand(this);
                if (player.getInventory().canAddItem(filled)) {
                    player.getInventory().addItem(filled);
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), filled, player.getDirectionVector().multiply(0.4));
                }
            }
            
            level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
        }
        
        return false;
    }
}
