package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.inventory.HumanOffHandInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.HumanEnderChestInventory;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShield;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class EntityHumanType extends EntityCreature implements IHuman, InventoryHolder {

    protected HumanInventory inventory;
    protected HumanEnderChestInventory enderChestInventory;
    protected HumanOffHandInventory offhandInventory;

    public EntityHumanType(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public HumanInventory getInventory() {
        return inventory;
    }

    public HumanOffHandInventory getOffhandInventory() {
        return offhandInventory;
    }

    public HumanEnderChestInventory getEnderChestInventory() {
        return enderChestInventory;
    }

    @Override
    public void setInventories(Inventory[] inventory) {
        this.inventory = (HumanInventory) inventory[0];
        this.offhandInventory = (HumanOffHandInventory) inventory[1];
        this.enderChestInventory = (HumanEnderChestInventory) inventory[2];
    }

    @Override
    public Item[] getDrops() {
        if (this.inventory != null) {
            List<Item> drops = new ArrayList<>(this.inventory.getContents().values());
            drops.addAll(this.offhandInventory.getContents().values());
            return drops.stream().filter(item -> !item.keepOnDeath()).toList().toArray(Item.EMPTY_ARRAY);
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }

        if (source.getCause() != DamageCause.VOID && source.getCause() != DamageCause.CUSTOM && source.getCause() != DamageCause.MAGIC && source.getCause() != DamageCause.HUNGER) {
            int armorPoints = 0;
            int epf = 0;

            for (Item armor : inventory.getArmorContents()) {
                armorPoints += armor.getArmorPoints();
                epf += calculateEnchantmentProtectionFactor(armor, source);
            }

            if (source.canBeReducedByArmor()) {
                source.setDamage(-source.getFinalDamage() * armorPoints * 0.04f, DamageModifier.ARMOR);
            }

            source.setDamage(-source.getFinalDamage() * Math.min(Math.min(NukkitMath.ceilFloat(Math.min(epf, 25)), 20) * 0.04f, 0.8f),
                    DamageModifier.ARMOR_ENCHANTMENTS);

            source.setDamage(-Math.min(this.getAbsorption(), source.getFinalDamage()), DamageModifier.ABSORPTION);
        }

        if (super.attack(source)) {
            Entity damager = null;

            if (source instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) source).getDamager();
            }

            for (int slot = 0; slot < 4; slot++) {
                Item armorOld = this.inventory.getArmorItem(slot);
                if (armorOld.isArmor()) {
                    Item armor = damageArmor(armorOld, damager, source);
                    inventory.setArmorItem(slot, armor, armor.getId() != BlockID.AIR);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected double calculateEnchantmentProtectionFactor(Item item, EntityDamageEvent source) {
        if (!item.hasEnchantments()) {
            return 0;
        }

        double epf = 0;

        if (item.applyEnchantments()) {
            for (Enchantment ench : item.getEnchantments()) {
                if(ench.canEnchant(item)) { //only apply enchantment when applicable to the item
                    epf += ench.getProtectionFactor(source);
                }
            }
        }

        return epf;
    }

    @Override
    public void setOnFire(int seconds) {
        int level = 0;

        for (Item armor : this.inventory.getArmorContents()) {
            Enchantment fireProtection = armor.getEnchantment(Enchantment.ID_PROTECTION_FIRE);

            if (fireProtection != null && fireProtection.getLevel() > 0) {
                level = Math.max(level, fireProtection.getLevel());
            }
        }

        seconds = (int) (seconds * (1 - level * 0.15));

        super.setOnFire(seconds);
    }

    @Override
    protected boolean applyNameTag(@NotNull Player player, @NotNull Item item) {
        return false;
    }

    protected Item damageArmor(Item armor, Entity damager, EntityDamageEvent event) {
        if (armor.hasEnchantments()) {
            if (damager != null) {
                if (armor.applyEnchantments()) {
                    for (Enchantment enchantment : armor.getEnchantments()) {
                        enchantment.doPostAttack(damager, this);
                    }
                }
            }

            Enchantment durability = armor.getEnchantment(Enchantment.ID_DURABILITY);
            if (durability != null
                    && durability.getLevel() > 0
                    && (100 / (durability.getLevel() + 1)) <= Utils.random.nextInt(100)) {
                return armor;
            }
        }

        if (shouldDamageArmor(armor) &&
                event.getCause() != EntityDamageEvent.DamageCause.VOID &&
                event.getCause() != EntityDamageEvent.DamageCause.MAGIC &&
                event.getCause() != EntityDamageEvent.DamageCause.HUNGER &&
                event.getCause() != EntityDamageEvent.DamageCause.DROWNING &&
                event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION &&
                event.getCause() != EntityDamageEvent.DamageCause.SUICIDE &&
                event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK &&
                event.getCause() != EntityDamageEvent.DamageCause.FALL) {

            if (armor instanceof ItemShield) {
                armor.setDamage(armor.getDamage() + (event.getDamage() >= 3 ? (int) event.getDamage() + 1 : 0));
            } else {
                armor.setDamage(armor.getDamage() + Math.max(1, (int) (event.getDamage() / 4.0f)));
            }

            if (armor.getMaxDurability() > 0 && armor.getDamage() >= armor.getMaxDurability()) {
                getLevel().addSound(this, Sound.RANDOM_BREAK);
                return Item.get(BlockID.AIR, 0, 0);
            }
        }

        return armor;
    }

    public boolean shouldDamageArmor(Item armor) {
        if (armor.isUnbreakable() || armor.getMaxDurability() <= 0) return false;

        int min = armor.getDamageChanceMin();
        int max = armor.getDamageChanceMax();
        int chance = (min == max) ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
        return ThreadLocalRandom.current().nextInt(100) < chance;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public @NotNull String getIdentifier() {
        return PLAYER;
    }
}
