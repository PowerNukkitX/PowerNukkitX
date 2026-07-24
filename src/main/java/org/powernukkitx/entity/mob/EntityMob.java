package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCanAttack;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.inventory.EntityArmorInventory;
import org.powernukkitx.inventory.EntityEquipmentInventory;
import org.powernukkitx.inventory.EntityInventoryHolder;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.Utils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class EntityMob extends EntityIntelligent implements EntityInventoryHolder, EntityCanAttack {
    private static final String TAG_MAINHAND = "Mainhand";
    private static final String TAG_OFFHAND = "Offhand";
    private static final String TAG_ARMOR = "Armor";
    /**
     * The damage that can be caused by the entity's empty hand at different difficulties.
     */
    protected float[] diffHandDamage;
    @Getter
    private EntityEquipmentInventory equipmentInventory;
    @Getter
    private EntityArmorInventory armorInventory;

    public EntityMob(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.equipmentInventory = new EntityEquipmentInventory(this);
        this.armorInventory = new EntityArmorInventory(this);

        super.initEntity();

        final CompoundTag nbtMap = this.getNbt();
        if (this.nbt.contains(TAG_MAINHAND)) {
            this.equipmentInventory.setItemInHand(ItemHelper.read(nbtMap.getCompound(TAG_MAINHAND)), true);
        }

        if (this.nbt.contains(TAG_OFFHAND)) {
            this.equipmentInventory.setItemInOffhand(ItemHelper.read(nbtMap.getCompound(TAG_OFFHAND)), true);
        }

        if (this.nbt.containsList(TAG_ARMOR)) {
            ListTag<CompoundTag> armorList = nbtMap.getList(TAG_ARMOR, CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                this.armorInventory.setItem(armorTag.getByte("Slot"), ItemHelper.read(armorTag));
            }
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.getServer().getDifficulty() == 0) {
            this.close();
            return true;
        } else return super.onUpdate(currentTick);
    }

    public void spawnToAll() {
        if (this.chunk != null && !this.closed) {
            Collection<Player> chunkPlayers = this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values();
            for (Player chunkPlayer : chunkPlayers) {
                this.spawnTo(chunkPlayer);
            }
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        this.equipmentInventory.sendContents(player);
        this.armorInventory.sendContents(player);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putCompound(TAG_MAINHAND, ItemHelper.write(this.equipmentInventory.getItemInHand(), null))
                .putCompound(TAG_OFFHAND, ItemHelper.write(this.equipmentInventory.getItemInOffhand(), null));

        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>();
            for (int i = 0; i < 4; i++) {
                armorTag.add(ItemHelper.write(this.armorInventory.getItem(i), i));
            }
            this.nbt.putList(TAG_ARMOR, armorTag);
        }
    }

    public int getAdditionalArmor() {
        return 0;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && !(entityDamageByEntityEvent.getDamager() instanceof EntityCreeper)) {
            //Update the aggro target
            getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entityDamageByEntityEvent.getDamager());
        }

        if (source.getCause() != EntityDamageEvent.DamageCause.VOID && source.getCause() != EntityDamageEvent.DamageCause.CUSTOM && source.getCause() != EntityDamageEvent.DamageCause.MAGIC && source.getCause() != EntityDamageEvent.DamageCause.HUNGER) {
            int armorPoints = getAdditionalArmor();
            int epf = 0;
//            int toughness = 0;

            var armorInventory = this.getArmorInventory();
            for (Item armor : armorInventory.getContents().values()) {
                armorPoints += armor.getArmorPoints();
                epf += calculateEnchantmentProtectionFactor(armor, source);
                //toughness += armor.getToughness();
            }

            if (source.canBeReducedByArmor()) {
                source.setDamage(-source.getFinalDamage() * armorPoints * 0.04f, EntityDamageEvent.DamageModifier.ARMOR);
            }

            source.setDamage(-source.getFinalDamage() * Math.min(NukkitMath.ceilFloat(Math.min(epf, 25) * ((float) ThreadLocalRandom.current().nextInt(50, 100) / 100)), 20) * 0.04f,
                    EntityDamageEvent.DamageModifier.ARMOR_ENCHANTMENTS);

            source.setDamage(-Math.min(this.getAbsorption(), source.getFinalDamage()), EntityDamageEvent.DamageModifier.ABSORPTION);
        }

        if (super.attack(source)) {
            Entity damager = null;

            if (source instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) source).getDamager();
            }

            for (int slot = 0; slot < 4; slot++) {
                Item armor = damageArmor(armorInventory.getItem(slot), damager);
                armorInventory.setItem(slot, armor, armor.getId() != BlockID.AIR);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setOnFire(int seconds) {
        int level = 0;

        for (Item armor : this.getArmorInventory().getContents().values()) {
            Enchantment fireProtection = armor.getEnchantment(Enchantment.ID_PROTECTION_FIRE);

            if (fireProtection != null && fireProtection.getLevel() > 0) {
                level = Math.max(level, fireProtection.getLevel());
            }
        }

        seconds = (int) (seconds * (1 - level * 0.15));

        super.setOnFire(seconds);
    }

    protected double calculateEnchantmentProtectionFactor(Item item, EntityDamageEvent source) {
        if (!item.hasEnchantments()) {
            return 0;
        }

        double epf = 0;

        if (item.applyEnchantments()) {
            for (Enchantment ench : item.getEnchantments()) {
                epf += ench.getProtectionFactor(source);
            }
        }

        return epf;
    }

    protected Item damageArmor(Item armor, Entity damager) {
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

        if (shouldDamageArmor(armor)) {
            armor.setDamage(armor.getDamage() + 1);
        }

        if (armor.getMaxDurability() > 0 && armor.getDamage() >= armor.getMaxDurability()) {
            getLevel().addSound(this, Sound.RANDOM_BREAK);
            return Item.get(BlockID.AIR, 0, 0);
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
    public Inventory getInventory() {
        return this.armorInventory;
    }

    @Override
    public boolean canEquipByDispenser() {
        return true;
    }

    @Override
    public float[] getDiffHandDamage() {
        return this.diffHandDamage;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        List<Item> drops = new ArrayList<>();
        for (Item item : getInventory().getContents().values()) {
            if (!item.hasEnchantment(Enchantment.ID_VANISHING_CURSE)) {
                drops.add(item);
            }
        }
        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public Integer getExperienceDrops() {
        return 5;
    }
}
