package org.powernukkitx.block.dispenser;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.inventory.EntityArmorInventory;
import org.powernukkitx.inventory.EntityInventoryHolder;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockFace.Axis;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class DefaultDispenseBehavior implements DispenseBehavior {


    public boolean success = true;


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
                if (clone.isHelmet() && armorInventory.getHelmet().getId() == BlockID.AIR) {
                    armorInventory.setHelmet(clone);
                    return null;
                } else if (clone.isChestplate() && armorInventory.getChestplate().getId() == BlockID.AIR) {
                    armorInventory.setChestplate(clone);
                    return null;
                } else if (clone.isLeggings() && armorInventory.getLeggings().getId() == BlockID.AIR) {
                    armorInventory.setLeggings(clone);
                    return null;
                } else if (clone.isBoots() && armorInventory.getBoots().getId() == BlockID.AIR) {
                    armorInventory.setBoots(clone);
                    return null;
                } else if (inventoryHolder.getEquipmentInventory().getItemInHand().getId() == BlockID.AIR) {
                    inventoryHolder.getEquipmentInventory().setItemInHand(clone, true);
                    return null;
                }
            } else if (e instanceof Player p) {
                HumanInventory armorInventory = p.getInventory();
                if (clone.isHelmet() && armorInventory.getHelmet().getId() == BlockID.AIR) {
                    armorInventory.setHelmet(clone);
                    return null;
                } else if (clone.isChestplate() && armorInventory.getChestplate().getId() == BlockID.AIR) {
                    armorInventory.setChestplate(clone);
                    return null;
                } else if (clone.isLeggings() && armorInventory.getLeggings().getId() == BlockID.AIR) {
                    armorInventory.setLeggings(clone);
                    return null;
                } else if (clone.isBoots() && armorInventory.getBoots().getId() == BlockID.AIR) {
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
