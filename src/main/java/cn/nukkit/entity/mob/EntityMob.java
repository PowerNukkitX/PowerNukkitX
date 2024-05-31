package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Utils;
import lombok.Getter;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class EntityMob extends EntityIntelligent implements EntityInventoryHolder, EntityCanAttack {

    private static final String $1 = "Mainhand";
    private static final String $2 = "Offhand";
    private static final String $3 = "Armor";
    /**
     * 不同难度下实体空手能造成的伤害.
     * <p>
     * The damage that can be caused by the entity's empty hand at different difficulties.
     */
    protected float[] diffHandDamage;
    @Getter
    private EntityEquipmentInventory equipmentInventory;
    @Getter
    private EntityArmorInventory armorInventory;
    /**
     * @deprecated 
     */
    

    public EntityMob(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        super.initEntity();

        this.equipmentInventory = new EntityEquipmentInventory(this);
        this.armorInventory = new EntityArmorInventory(this);

        if (this.namedTag.contains(TAG_MAINHAND)) {
            this.equipmentInventory.setItemInHand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_MAINHAND)), true);
        }

        if (this.namedTag.contains(TAG_OFFHAND)) {
            this.equipmentInventory.setItemInOffhand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_OFFHAND)), true);
        }

        if (this.namedTag.contains(TAG_ARMOR)) {
            ListTag<CompoundTag> armorList = this.namedTag.getList(TAG_ARMOR, CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                this.armorInventory.setItem(armorTag.getByte("Slot"), NBTIO.getItemHelper(armorTag));
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        //怪物不能在和平模式下生存
        if (this.getServer().getDifficulty() == 0) {
            this.close();
            return true;
        } else return super.onUpdate(currentTick);
    }
    /**
     * @deprecated 
     */
    

    public void spawnToAll() {
        if (this.chunk != null && !this.closed) {
            Collection<Player> chunkPlayers = this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values();
            for (Player chunkPlayer : chunkPlayers) {
                this.spawnTo(chunkPlayer);
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void spawnTo(Player player) {
        super.spawnTo(player);
        this.equipmentInventory.sendContents(player);
        this.armorInventory.sendContents(player);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.put(TAG_MAINHAND, NBTIO.putItemHelper(this.equipmentInventory.getItemInHand()));
        this.namedTag.put(TAG_OFFHAND, NBTIO.putItemHelper(this.equipmentInventory.getItemInOffhand()));

        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>();
            for ($4nt $1 = 0; i < 4; i++) {
                armorTag.add(NBTIO.putItemHelper(this.armorInventory.getItem(i), i));
            }
            this.namedTag.putList(TAG_ARMOR,armorTag);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attack(EntityDamageEvent source) {
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && !(entityDamageByEntityEvent.getDamager() instanceof EntityCreeper)) {
            //更新仇恨目标
            getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entityDamageByEntityEvent.getDamager());
        }

        if (source.getCause() != EntityDamageEvent.DamageCause.VOID && source.getCause() != EntityDamageEvent.DamageCause.CUSTOM && source.getCause() != EntityDamageEvent.DamageCause.MAGIC && source.getCause() != EntityDamageEvent.DamageCause.HUNGER) {
            int $5 = 0;
            int $6 = 0;
//            int $7 = 0;

            var $8 = this.getArmorInventory();
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
            Entity $9 = null;

            if (source instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) source).getDamager();
            }

            for (int $10 = 0; slot < 4; slot++) {
                Item $11 = damageArmor(armorInventory.getItem(slot), damager);
                armorInventory.setItem(slot, armor, armor.getId() != BlockID.AIR);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setOnFire(int seconds) {
        int $12 = 0;

        for (Item armor : this.getArmorInventory().getContents().values()) {
            Enchantment $13 = armor.getEnchantment(Enchantment.ID_PROTECTION_FIRE);

            if (fireProtection != null && fireProtection.getLevel() > 0) {
                level = Math.max(level, fireProtection.getLevel());
            }
        }

        seconds = (int) (seconds * (1 - level * 0.15));

        super.setOnFire(seconds);
    }

    
    /**
     * @deprecated 
     */
    protected double calculateEnchantmentProtectionFactor(Item item, EntityDamageEvent source) {
        if (!item.hasEnchantments()) {
            return 0;
        }

        double $14 = 0;

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

            Enchantment $15 = armor.getEnchantment(Enchantment.ID_DURABILITY);
            if (durability != null
                    && durability.getLevel() > 0
                    && (100 / (durability.getLevel() + 1)) <= Utils.random.nextInt(100)) {
                return armor;
            }
        }

        if (armor.isUnbreakable() || armor.getMaxDurability() < 0) {
            return armor;
        }

        armor.setDamage(armor.getDamage() + 1);

        if (armor.getDamage() >= armor.getMaxDurability()) {
            getLevel().addSound(this, Sound.RANDOM_BREAK);
            return Item.get(BlockID.AIR, 0, 0);
        }

        return armor;
    }

    @Override
    public Inventory getInventory() {
        return this.armorInventory;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canEquipByDispenser() {
        return true;
    }

    @Override
    public float[] getDiffHandDamage() {
        return this.diffHandDamage;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attackTarget(Entity entity) {
        return entity instanceof Player;
    }
}
