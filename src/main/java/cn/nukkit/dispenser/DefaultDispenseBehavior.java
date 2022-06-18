package cn.nukkit.dispenser;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class DefaultDispenseBehavior implements DispenseBehavior {

    @PowerNukkitOnly
    public boolean success = true;

    @PowerNukkitOnly
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 dispensePos = block.getDispensePosition();

        if (face.getAxis() == Axis.Y) {
            dispensePos.y -= 0.125;
        } else {
            dispensePos.y -= 0.15625;
        }

        Random rand = ThreadLocalRandom.current();
        Vector3 motion = new Vector3();

        double offset = rand.nextDouble() * 0.1 + 0.2;

        motion.x = face.getXOffset() * offset;
        motion.y = 0.20000000298023224;
        motion.z = face.getZOffset() * offset;

        motion.x += rand.nextGaussian() * 0.007499999832361937 * 6;
        motion.y += rand.nextGaussian() * 0.007499999832361937 * 6;
        motion.z += rand.nextGaussian() * 0.007499999832361937 * 6;

        Item clone = item.clone();
        clone.count = 1;


        Vector3 dropPos = dispensePos.add(face.getXOffset(), face.getYOffset(), face.getZOffset());
        AxisAlignedBB bb = new SimpleAxisAlignedBB(dropPos.getX() - 0.5, dropPos.getY() - 1, dropPos.getZ() - 0.5, dropPos.getX() + 0.5, dropPos.getY() + 1, dropPos.getZ() + 0.5);
        for (Entity e : block.level.getNearbyEntities(bb)) {
            if (e instanceof EntityInventoryHolder inventoryHolder && inventoryHolder.canEquipByDispenser()) {
                EntityArmorInventory armorInventory = inventoryHolder.getArmorInventory();
                if (clone.isHelmet() && armorInventory.getHelmet().getId() == Item.AIR) {
                    armorInventory.setHelmet(clone);
                    return null;
                } else if (clone.isChestplate() && armorInventory.getChestplate().getId() == Item.AIR) {
                    armorInventory.setChestplate(clone);
                    return null;
                } else if (clone.isLeggings() && armorInventory.getLeggings().getId() == Item.AIR) {
                    armorInventory.setLeggings(clone);
                    return null;
                } else if (clone.isBoots() && armorInventory.getBoots().getId() == Item.AIR) {
                    armorInventory.setBoots(clone);
                    return null;
                } else if (inventoryHolder.getEquipmentInventory().getItemInHand().getId() == Item.AIR) {
                    inventoryHolder.getEquipmentInventory().setItemInHand(clone, true);
                    return null;
                }
            } else if (e instanceof Player p) {
                PlayerInventory armorInventory = p.getInventory();
                if (clone.isHelmet() && armorInventory.getHelmet().getId() == Item.AIR) {
                    armorInventory.setHelmet(clone);
                    return null;
                } else if (clone.isChestplate() && armorInventory.getChestplate().getId() == Item.AIR) {
                    armorInventory.setChestplate(clone);
                    return null;
                } else if (clone.isLeggings() && armorInventory.getLeggings().getId() == Item.AIR) {
                    armorInventory.setLeggings(clone);
                    return null;
                } else if (clone.isBoots() && armorInventory.getBoots().getId() == Item.AIR) {
                    armorInventory.setBoots(clone);
                    return null;
                }
            }
        }
        block.level.dropItem(dispensePos, clone, motion);
        return null;
    }

    private int getParticleMetadataForFace(BlockFace face) {
        return face.getXOffset() + 1 + (face.getZOffset() + 1) * 3;
    }
}
