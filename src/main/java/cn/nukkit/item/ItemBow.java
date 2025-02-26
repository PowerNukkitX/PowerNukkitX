package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityDataTypes;
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

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBow extends ItemTool {

    public ItemBow() {
        this(0, 1);
    }

    public ItemBow(Integer meta) {
        this(meta, 1);
    }

    public ItemBow(Integer meta, int count) {
        super(BOW, meta, count, "Bow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_BOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.isCreative() ||
                player.getInventory().getContents().values().stream().filter(item -> item instanceof ItemArrow).findFirst().isPresent() ||
                player.getOffhandInventory().getContents().values().stream().filter(item -> item instanceof ItemArrow).findFirst().isPresent();
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        Optional<Map.Entry<Integer, Item>> inventoryOptional = player.getInventory().getContents().entrySet().stream().filter(item -> item.getValue() instanceof ItemArrow).findFirst();
        Optional<Map.Entry<Integer, Item>> offhandOptional = player.getOffhandInventory().getContents().entrySet().stream().filter(item -> item.getValue() instanceof ItemArrow).findFirst();


        if (offhandOptional.isEmpty() && inventoryOptional.isEmpty() && (player.isAdventure() || player.isSurvival())) {
            player.getOffhandInventory().sendContents(player);
            player.getInventory().sendContents(player);
            return false;
        }

        double damage = 2;

        Enchantment bowDamage = this.getEnchantment(Enchantment.ID_BOW_POWER);
        if (bowDamage != null && bowDamage.getLevel() > 0) {
            damage += (double) bowDamage.getLevel() * 0.5 + 0.5;
        }

        Enchantment flameEnchant = this.getEnchantment(Enchantment.ID_BOW_FLAME);
        boolean flame = flameEnchant != null && flameEnchant.getLevel() > 0;

        Location arrowLocation = player.getLocation();
        Vector3 directionVector = player.getDirectionVector().multiply(1.1);
        arrowLocation = arrowLocation.add(directionVector.getX(), 0, directionVector.getZ());
        arrowLocation.setY(player.y + player.getEyeHeight() + directionVector.getY());

        ItemArrow itemArrow = (ItemArrow) (offhandOptional.isPresent() ?  offhandOptional.get().getValue() : inventoryOptional.get().getValue());

        CompoundTag nbt = new CompoundTag()
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

        double p = (double) ticksUsed / 20;
        final double maxForce = 3.5;
        double f = Math.min((p * p + p * 2) / 3, 1) * maxForce;

        EntityArrow arrow = (EntityArrow) Entity.createEntity(Entity.ARROW, player.chunk, nbt, player, f == maxForce);
        ItemArrow copy = (ItemArrow) itemArrow.clone();
        copy.setCount(1);
        arrow.setItem(copy);
        if (arrow == null) {
            return false;
        }
        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, arrow, f);

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
            Enchantment infinityEnchant = this.getEnchantment(Enchantment.ID_BOW_INFINITY);
            boolean infinity = infinityEnchant != null && infinityEnchant.getLevel() > 0;
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
                    if(offhandOptional.isPresent()) {
                        int index = offhandOptional.get().getKey();
                        player.getOffhandInventory().setItem(index, player.getOffhandInventory().getItem(index).decrement(1));
                    } else {
                        int index = inventoryOptional.get().getKey();
                        player.getInventory().setItem(index, player.getInventory().getItem(index).decrement(1));
                    }
                }
                if (!this.isUnbreakable()) {
                    Enchantment durability = this.getEnchantment(Enchantment.ID_DURABILITY);
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
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile(), player);
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
