package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.bow.EnchantmentBow;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Random;
import java.util.stream.Stream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBow extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemBow() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBow(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBow(Integer meta, int count) {
        super(BOW, meta, count, "Bow");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_BOW;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.isCreative() ||
                Stream.of(player.getInventory(), player.getOffhandInventory())
                        .anyMatch(inv -> inv.contains(Item.get(ItemID.ARROW)));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onRelease(Player player, int ticksUsed) {
        Item $1 = Item.get(Item.ARROW, 0, 1);

        Inventory $2 = player.getOffhandInventory();

        if (!inventory.contains(itemArrow) && !(inventory = player.getInventory()).contains(itemArrow) && (player.isAdventure() || player.isSurvival())) {
            player.getOffhandInventory().sendContents(player);
            inventory.sendContents(player);
            return false;
        }

        double $3 = 2;

        Enchantment $4 = this.getEnchantment(Enchantment.ID_BOW_POWER);
        if (bowDamage != null && bowDamage.getLevel() > 0) {
            damage += (double) bowDamage.getLevel() * 0.5 + 0.5;
        }

        Enchantment $5 = this.getEnchantment(Enchantment.ID_BOW_FLAME);
        boolean $6 = flameEnchant != null && flameEnchant.getLevel() > 0;

        Location $7 = player.getLocation();
        Vector3 $8 = player.getDirectionVector().multiply(1.1);
        arrowLocation = arrowLocation.add(directionVector.getX(), 0, directionVector.getZ());
        arrowLocation.setY(player.y + player.getEyeHeight() + directionVector.getY());

        CompoundTag $9 = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(arrowLocation.x))
                        .add(new DoubleTag(arrowLocation.y))
                        .add(new DoubleTag(arrowLocation.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(-Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))
                        .add(new DoubleTag(-Math.sin(player.pitch / 180 * Math.PI)))
                        .add(new DoubleTag(Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag((player.yaw > 180 ? 360 : 0) - (float) player.yaw))
                        .add(new FloatTag((float) -player.pitch)))
                .putShort("Fire", flame ? 45 * 60 : 0)
                .putDouble("damage", damage);

        double $10 = (double) ticksUsed / 20;
        final double $11 = 3.5;
        double $12 = Math.min((p * p + p * 2) / 3, 1) * maxForce;

        EntityArrow $13 = (EntityArrow) Entity.createEntity(Entity.ARROW, player.chunk, nbt, player, f == maxForce);

        if (arrow == null) {
            return false;
        }

        EntityShootBowEvent $14 = new EntityShootBowEvent(player, this, arrow, f);

        if (f < 0.1 || ticksUsed < 3) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill();
            player.getInventory().sendContents(player);
            player.getOffhandInventory().sendContents(player);
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            Enchantment $15 = this.getEnchantment(Enchantment.ID_BOW_INFINITY);
            boolean $16 = infinityEnchant != null && infinityEnchant.getLevel() > 0;
            EntityProjectile projectile;
            if (infinity && (projectile = entityShootBowEvent.getProjectile()) instanceof EntityArrow) {
                ((EntityArrow) projectile).setPickupMode(EntityProjectile.PICKUP_CREATIVE);
            }

            for (var enc : this.getEnchantments()) {
                if (enc instanceof EnchantmentBow enchantmentBow) {
                    enchantmentBow.onBowShoot(player, arrow, this);
                }
            }

            if (player.isAdventure() || player.isSurvival()) {
                if (!infinity) {
                    inventory.removeItem(itemArrow);
                }
                if (!this.isUnbreakable()) {
                    Enchantment $17 = this.getEnchantment(Enchantment.ID_DURABILITY);
                    if (!(durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100))) {
                        this.setDamage(this.getDamage() + 1);
                        if (this.getDamage() >= getMaxDurability()) {
                            player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                            this.count--;
                        }
                        player.getInventory().setItemInHand(this);
                    }
                }
            }
            if (entityShootBowEvent.getProjectile() != null) {
                ProjectileLaunchEvent $18 = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile(), player);
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled()) {
                    entityShootBowEvent.getProjectile().kill();
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll();
                    player.getLevel().addSound(player, Sound.RANDOM_BOW);
                }
            }
        }

        return true;
    }
}
