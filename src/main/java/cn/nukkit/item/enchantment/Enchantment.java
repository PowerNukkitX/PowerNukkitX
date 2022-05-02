package cn.nukkit.item.enchantment;

import cn.nukkit.api.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.bow.EnchantmentBowFlame;
import cn.nukkit.item.enchantment.bow.EnchantmentBowInfinity;
import cn.nukkit.item.enchantment.bow.EnchantmentBowKnockback;
import cn.nukkit.item.enchantment.bow.EnchantmentBowPower;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowMultishot;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowPiercing;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowQuickCharge;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageAll;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageArthropods;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageSmite;
import cn.nukkit.item.enchantment.loot.EnchantmentLootDigging;
import cn.nukkit.item.enchantment.loot.EnchantmentLootFishing;
import cn.nukkit.item.enchantment.loot.EnchantmentLootWeapon;
import cn.nukkit.item.enchantment.protection.*;
import cn.nukkit.item.enchantment.sideeffect.SideEffect;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentChanneling;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentImpaling;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentLoyalty;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentRiptide;
import cn.nukkit.math.NukkitMath;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * An enchantment that can be to applied to an item.
 * 
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Enchantment implements Cloneable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Enchantment[] EMPTY_ARRAY = new Enchantment[0];

    protected static Enchantment[] enchantments;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    protected static Map<String,Integer> enchantmentName2IDMap = new Object2IntArrayMap<>();

    public static final int ID_PROTECTION_ALL = 0;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_PROTECTION_ALL = "protection";
    public static final int ID_PROTECTION_FIRE = 1;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_PROTECTION_FIRE = "fire_protection";
    public static final int ID_PROTECTION_FALL = 2;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_PROTECTION_FALL = "feather_falling";
    public static final int ID_PROTECTION_EXPLOSION = 3;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_PROTECTION_EXPLOSION = "blast_protection";
    public static final int ID_PROTECTION_PROJECTILE = 4;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_PROTECTION_PROJECTILE = "projectile_protection";
    public static final int ID_THORNS = 5;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_THORNS = "thorns";
    public static final int ID_WATER_BREATHING = 6;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_WATER_BREATHING = "respiration";
    public static final int ID_WATER_WALKER = 7;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_WATER_WALKER = "depth_strider";
    public static final int ID_WATER_WORKER = 8;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_WATER_WORKER = "aqua_affinity";
    public static final int ID_DAMAGE_ALL = 9;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_DAMAGE_ALL = "sharpness";
    public static final int ID_DAMAGE_SMITE = 10;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_DAMAGE_SMITE = "smite";
    public static final int ID_DAMAGE_ARTHROPODS = 11;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_DAMAGE_ARTHROPODS = "bane_of_arthropods";
    public static final int ID_KNOCKBACK = 12;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_KNOCKBACK = "knockback";
    public static final int ID_FIRE_ASPECT = 13;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_FIRE_ASPECT = "fire_aspect";
    public static final int ID_LOOTING = 14;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_LOOTING = "looting";
    public static final int ID_EFFICIENCY = 15;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_EFFICIENCY = "efficiency";
    public static final int ID_SILK_TOUCH = 16;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_SILK_TOUCH = "silk_touch";
    public static final int ID_DURABILITY = 17;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_DURABILITY = "unbreaking";
    public static final int ID_FORTUNE_DIGGING = 18;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_FORTUNE_DIGGING = "fortune";
    public static final int ID_BOW_POWER = 19;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_BOW_POWER = "power";
    public static final int ID_BOW_KNOCKBACK = 20;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_BOW_KNOCKBACK = "punch";
    public static final int ID_BOW_FLAME = 21;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_BOW_FLAME = "flame";
    public static final int ID_BOW_INFINITY = 22;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_BOW_INFINITY = "infinity";
    public static final int ID_FORTUNE_FISHING = 23;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_FORTUNE_FISHING = "luck_of_the_sea";
    public static final int ID_LURE = 24;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_LURE = "lure";
    public static final int ID_FROST_WALKER = 25;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_FROST_WALKER = "frost_walker";

    public static final int ID_MENDING = 26;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_MENDING = "mending";
    public static final int ID_BINDING_CURSE = 27;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_BINDING_CURSE = "binding";
    public static final int ID_VANISHING_CURSE = 28;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_VANISHING_CURSE = "vanishing";
    public static final int ID_TRIDENT_IMPALING = 29;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_TRIDENT_IMPALING = "impaling";
    public static final int ID_TRIDENT_RIPTIDE = 30;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_TRIDENT_RIPTIDE = "riptide";
    public static final int ID_TRIDENT_LOYALTY = 31;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_TRIDENT_LOYALTY = "loyalty";
    public static final int ID_TRIDENT_CHANNELING = 32;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_TRIDENT_CHANNELING = "channeling";
    @Since("1.4.0.0-PN") public static final int ID_CROSSBOW_MULTISHOT = 33;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_CROSSBOW_MULTISHOT = "multishot";
    @Since("1.4.0.0-PN") public static final int ID_CROSSBOW_PIERCING = 34;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_CROSSBOW_PIERCING = "piercing";
    @Since("1.4.0.0-PN") public static final int ID_CROSSBOW_QUICK_CHARGE = 35;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_CROSSBOW_QUICK_CHARGE = "quick_charge";
    @Since("1.4.0.0-PN") public static final int ID_SOUL_SPEED = 36;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final String NAME_SOUL_SPEED = "soul_speed";

    public static void init() {
        enchantments = new Enchantment[256];

        enchantments[ID_PROTECTION_ALL] = new EnchantmentProtectionAll();
        enchantments[ID_PROTECTION_FIRE] = new EnchantmentProtectionFire();
        enchantments[ID_PROTECTION_FALL] = new EnchantmentProtectionFall();
        enchantments[ID_PROTECTION_EXPLOSION] = new EnchantmentProtectionExplosion();
        enchantments[ID_PROTECTION_PROJECTILE] = new EnchantmentProtectionProjectile();
        enchantments[ID_THORNS] = new EnchantmentThorns();
        enchantments[ID_WATER_BREATHING] = new EnchantmentWaterBreath();
        enchantments[ID_WATER_WORKER] = new EnchantmentWaterWorker();
        enchantments[ID_WATER_WALKER] = new EnchantmentWaterWalker();
        enchantments[ID_DAMAGE_ALL] = new EnchantmentDamageAll();
        enchantments[ID_DAMAGE_SMITE] = new EnchantmentDamageSmite();
        enchantments[ID_DAMAGE_ARTHROPODS] = new EnchantmentDamageArthropods();
        enchantments[ID_KNOCKBACK] = new EnchantmentKnockback();
        enchantments[ID_FIRE_ASPECT] = new EnchantmentFireAspect();
        enchantments[ID_LOOTING] = new EnchantmentLootWeapon();
        enchantments[ID_EFFICIENCY] = new EnchantmentEfficiency();
        enchantments[ID_SILK_TOUCH] = new EnchantmentSilkTouch();
        enchantments[ID_DURABILITY] = new EnchantmentDurability();
        enchantments[ID_FORTUNE_DIGGING] = new EnchantmentLootDigging();
        enchantments[ID_BOW_POWER] = new EnchantmentBowPower();
        enchantments[ID_BOW_KNOCKBACK] = new EnchantmentBowKnockback();
        enchantments[ID_BOW_FLAME] = new EnchantmentBowFlame();
        enchantments[ID_BOW_INFINITY] = new EnchantmentBowInfinity();
        enchantments[ID_FORTUNE_FISHING] = new EnchantmentLootFishing();
        enchantments[ID_LURE] = new EnchantmentLure();
        enchantments[ID_FROST_WALKER] = new EnchantmentFrostWalker();
        enchantments[ID_MENDING]  = new EnchantmentMending();
        enchantments[ID_BINDING_CURSE]  = new EnchantmentBindingCurse();
        enchantments[ID_VANISHING_CURSE]  = new EnchantmentVanishingCurse();
        enchantments[ID_TRIDENT_IMPALING]  = new EnchantmentTridentImpaling();
        enchantments[ID_TRIDENT_RIPTIDE]  = new EnchantmentTridentRiptide();
        enchantments[ID_TRIDENT_LOYALTY]  = new EnchantmentTridentLoyalty();
        enchantments[ID_TRIDENT_CHANNELING]  = new EnchantmentTridentChanneling();
        enchantments[ID_CROSSBOW_MULTISHOT]  = new EnchantmentCrossbowMultishot();
        enchantments[ID_CROSSBOW_PIERCING]  = new EnchantmentCrossbowPiercing();
        enchantments[ID_CROSSBOW_QUICK_CHARGE]  = new EnchantmentCrossbowQuickCharge();
        enchantments[ID_SOUL_SPEED]  = new EnchantmentSoulSpeed();


        enchantmentName2IDMap.put(NAME_PROTECTION_ALL, ID_PROTECTION_ALL);
        enchantmentName2IDMap.put(NAME_PROTECTION_FIRE, ID_PROTECTION_FIRE);
        enchantmentName2IDMap.put(NAME_PROTECTION_FALL, ID_PROTECTION_FALL);
        enchantmentName2IDMap.put(NAME_PROTECTION_EXPLOSION, ID_PROTECTION_EXPLOSION);
        enchantmentName2IDMap.put(NAME_PROTECTION_PROJECTILE, ID_PROTECTION_PROJECTILE);
        enchantmentName2IDMap.put(NAME_THORNS, ID_THORNS);
        enchantmentName2IDMap.put(NAME_WATER_BREATHING, ID_WATER_BREATHING);
        enchantmentName2IDMap.put(NAME_WATER_WORKER, ID_WATER_WORKER);
        enchantmentName2IDMap.put(NAME_WATER_WALKER, ID_WATER_WALKER);
        enchantmentName2IDMap.put(NAME_DAMAGE_ALL, ID_DAMAGE_ALL);
        enchantmentName2IDMap.put(NAME_DAMAGE_SMITE, ID_DAMAGE_SMITE);
        enchantmentName2IDMap.put(NAME_DAMAGE_ARTHROPODS, ID_DAMAGE_ARTHROPODS);
        enchantmentName2IDMap.put(NAME_KNOCKBACK, ID_KNOCKBACK);
        enchantmentName2IDMap.put(NAME_FIRE_ASPECT, ID_FIRE_ASPECT);
        enchantmentName2IDMap.put(NAME_LOOTING, ID_LOOTING);
        enchantmentName2IDMap.put(NAME_EFFICIENCY, ID_EFFICIENCY);
        enchantmentName2IDMap.put(NAME_SILK_TOUCH, ID_SILK_TOUCH);
        enchantmentName2IDMap.put(NAME_DURABILITY, ID_DURABILITY);
        enchantmentName2IDMap.put(NAME_FORTUNE_DIGGING, ID_FORTUNE_DIGGING);
        enchantmentName2IDMap.put(NAME_BOW_POWER, ID_BOW_POWER);
        enchantmentName2IDMap.put(NAME_BOW_KNOCKBACK, ID_BOW_KNOCKBACK);
        enchantmentName2IDMap.put(NAME_BOW_FLAME, ID_BOW_FLAME);
        enchantmentName2IDMap.put(NAME_BOW_INFINITY, ID_BOW_INFINITY);
        enchantmentName2IDMap.put(NAME_FORTUNE_FISHING, ID_FORTUNE_FISHING);
        enchantmentName2IDMap.put(NAME_LURE, ID_LURE);
        enchantmentName2IDMap.put(NAME_FROST_WALKER, ID_FROST_WALKER);
        enchantmentName2IDMap.put(NAME_MENDING, ID_MENDING);
        enchantmentName2IDMap.put(NAME_BINDING_CURSE, ID_BINDING_CURSE);
        enchantmentName2IDMap.put(NAME_VANISHING_CURSE, ID_VANISHING_CURSE);
        enchantmentName2IDMap.put(NAME_TRIDENT_IMPALING, ID_TRIDENT_IMPALING);
        enchantmentName2IDMap.put(NAME_TRIDENT_RIPTIDE, ID_TRIDENT_RIPTIDE);
        enchantmentName2IDMap.put(NAME_TRIDENT_LOYALTY, ID_TRIDENT_LOYALTY);
        enchantmentName2IDMap.put(NAME_TRIDENT_CHANNELING, ID_TRIDENT_CHANNELING);
        enchantmentName2IDMap.put(NAME_CROSSBOW_MULTISHOT, ID_CROSSBOW_MULTISHOT);
        enchantmentName2IDMap.put(NAME_CROSSBOW_PIERCING, ID_CROSSBOW_PIERCING);
        enchantmentName2IDMap.put(NAME_CROSSBOW_QUICK_CHARGE, ID_CROSSBOW_QUICK_CHARGE);
        enchantmentName2IDMap.put(NAME_SOUL_SPEED, ID_SOUL_SPEED);
    }

    /**
     * Returns the enchantment object registered with this ID, any change to the returned object affects 
     * the creation of new enchantments as the returned object is not a copy.
     * @param id The enchantment id.
     * @return The enchantment, if no enchantment is found with that id, {@link UnknownEnchantment} is returned.
     * The UnknownEnchantment will be always a new instance and changes to it does not affects other calls.
     */
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", reason = "This is very insecure and can break the environment", since = "1.4.0.0-PN",
            replaceWith = "getEnchantment(int)")
    public static Enchantment get(int id) {
        Enchantment enchantment = null;
        if (id >= 0 && id < enchantments.length) {
            enchantment = enchantments[id];
        }
        if (enchantment == null) {
            return new UnknownEnchantment(id);
        }
        return enchantment;
    }

    /**
     * The same as {@link #get(int)} but returns a safe copy of the enchantment.
     * @param id The enchantment id
     * @return A new enchantment object.
     */
    public static Enchantment getEnchantment(int id) {
        return get(id).clone();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static Enchantment getEnchantment(String name) {
        return getEnchantment(enchantmentName2IDMap.get(name));
    }

    /**
     * Gets an array of all registered enchantments, the objects in the array are linked to the registry,
     * it's not safe to change them. Changing them can cause the same issue as documented in {@link #get(int)}
     * @return An array with the enchantment objects, the array may contain null objects but is very unlikely.
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", 
            reason = "The objects returned by this method are not safe to use and the implementation may skip some enchantments",
            replaceWith = "getRegisteredEnchantments()"
    )
    public static Enchantment[] getEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<>();
        for (Enchantment enchantment : enchantments) {
            if (enchantment == null) {
                break;
            }

            list.add(enchantment);
        }

        return list.toArray(Enchantment.EMPTY_ARRAY);
    }

    /**
     * Gets a collection with a safe copy of all enchantments that are currently registered.
     * @return The objects can be modified without affecting the registry and the collection will not have null values.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static Collection<Enchantment> getRegisteredEnchantments() {
        return Arrays.stream(enchantments)
                .filter(Objects::nonNull)
                .map(Enchantment::clone)
                .collect(Collectors.toList());
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static Map<String,Integer> getEnchantmentName2IDMap() {
        return enchantmentName2IDMap;
    }

    /**
     * The internal ID which this enchantment got registered.
     */
    public final int id;
    private final Rarity rarity;

    /**
     * The group of objects that this enchantment can be applied.
     */
    @Nonnull
    public EnchantmentType type;

    /**
     * The level of this enchantment. Starting from {@code 1}.
     */
    protected int level = 1;

    /**
     * The name visible by the player, this is used in conjunction with {@link #getName()}, 
     * unless modified with an override, the getter will automatically add 
     * "%enchantment." as prefix to grab the translation key 
     */
    protected final String name;

    /**
     * Constructs this instance using the given data and with level 1.
     * @param id The enchantment ID
     * @param name The translation key without the "%enchantment." suffix
     * @param weight How rare this enchantment is, from {@code 1} to {@code 10} both inclusive where {@code 1} is the rarest
     * @param type Where the enchantment can be applied
     */
    @PowerNukkitOnly("Was removed from Nukkit in 1.4.0.0-PN, keeping it in PowerNukkit for backward compatibility")
    @Deprecated @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.4.0.0-PN", reason = "Changed the signature without backward compatibility",
            replaceWith = "Enchantment(int, String, Rarity, EnchantmentType)")
    protected Enchantment(int id, String name, int weight, EnchantmentType type) {
        this(id, name, Rarity.fromWeight(weight), type);
    }

    /**
     * Constructs this instance using the given data and with level 1.
     * @param id The enchantment ID
     * @param name The translation key without the "%enchantment." suffix
     * @param rarity How rare this enchantment is
     * @param type Where the enchantment can be applied
     */
    @Since("1.4.0.0-PN")
    protected Enchantment(int id, String name, Rarity rarity, EnchantmentType type) {
        this.id = id;
        this.rarity = rarity;
        this.type = type;

        this.name = name;
    }

    /**
     * The current level of this enchantment. {@code 0} means that the enchantment is not applied.
     * @return The level starting from {@code 1}.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Changes the level of this enchantment.
     * The level is clamped between the values returned in {@link #getMinLevel()} and {@link #getMaxLevel()}.
     * 
     * @param level The level starting from {@code 1}.
     * @return This object so you can do chained calls
     */

    @Nonnull
    public Enchantment setLevel(int level) {
        return this.setLevel(level, true);
    }

    /**
     * Changes the level of this enchantment.
     * When the {@code safe} param is {@code true}, the level is clamped between the values 
     * returned in {@link #getMinLevel()} and {@link #getMaxLevel()}.
     *
     * @param level The level starting from {@code 1}.
     * @param safe If the level should clamped or applied directly
     * @return This object so you can do chained calls
     */
    @Nonnull
    public Enchantment setLevel(int level, boolean safe) {
        if (!safe) {
            this.level = level;
            return this;
        }

        this.level = NukkitMath.clamp(level, this.getMinLevel(), this.getMaxLevel());

        return this;
    }

    /**
     * The ID of this enchantment. 
     */
    public int getId() {
        return id;
    }

    /**
     * How rare this enchantment is.
     */
    @Since("1.4.0.0-PN")
    @Nonnull
    public Rarity getRarity() {
        return this.rarity;
    }

    /**
     * How rare this enchantment is, from {@code 1} to {@code 10} where {@code 1} is the rarest.
     * @deprecated use {@link Rarity#getWeight()} instead
     */
    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", 
            reason = "Refactored enchantments and now uses a Rarity enum", 
            replaceWith = "getRarity().getWeight()")
    @Deprecated
    public int getWeight() {
        return this.rarity.getWeight();
    }

    /**
     * The minimum safe level which is possible with this enchantment. It is usually {@code 1}.
     */
    public int getMinLevel() {
        return 1;
    }

    /**
     * The maximum safe level which is possible with this enchantment.
     */
    public int getMaxLevel() {
        return 1;
    }

    /**
     * The maximum level that can be obtained using an enchanting table.
     */
    public int getMaxEnchantableLevel() {
        return getMaxLevel();
    }

    /**
     * The minimum enchantability for the given level as described in https://minecraft.gamepedia.com/Enchanting/Levels
     * @param level The level being checked
     * @return The minimum value
     */
    public int getMinEnchantAbility(int level) {
        return 1 + level * 10;
    }

    /**
     * The maximum enchantability for the given level as described in https://minecraft.gamepedia.com/Enchanting/Levels
     * @param level The level being checked
     * @return The maximum value
     */
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 5;
    }

    public float getProtectionFactor(EntityDamageEvent event) {
        return 0;
    }

    public double getDamageBonus(Entity entity) {
        return 0;
    }

    public void doPostAttack(Entity attacker, Entity entity) {

    }

    @Since("FUTURE")
    public void doAttack(Entity attacker, Entity entity) {

    }

    public void doPostHurt(Entity attacker, Entity entity) {

    }

    /**
     * Returns true if and only if this enchantment is compatible with the other and 
     * the other is also compatible with this enchantment. 
     * @param enchantment The enchantment which is being checked
     * @return If both enchantments are compatible
     * @implNote Cloudburst Nukkit added the final modifier, PowerNukkit removed it to maintain backward compatibility.
     * The right way to implement compatibility now is to override {@link #checkCompatibility(Enchantment)}
     *  and also make sure to keep it protected! Some overrides was incorrectly made public, let's avoid this mistake
     */
    @PowerNukkitDifference(since = "1.4.0.0-PN", 
            info = "Cloudburst Nukkit added the final modifier, we removed it to maintain backward compatibility. " +
                    "The right way to implement compatibility now is to override checkCompatibility(Enchantment enchantment) " +
                    "and also make sure to keep it protected! Some overrides was incorrectly made public, let's avoid this mistake."
    )
    public boolean isCompatibleWith(@Nonnull Enchantment enchantment) {
        return this.checkCompatibility(enchantment) && enchantment.checkCompatibility(this);
    }

    /**
     * Checks if this enchantment can be applied to an item that have the give enchantment without doing reverse check.
     * @param enchantment The enchantment to be checked
     * @return If this enchantment is compatible with the other enchantment.
     */
    @Since("1.4.0.0-PN")
    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment;
    }

    //return the translation key for the enchantment
    public String getName() {
        return "%enchantment." + this.name;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public String getOriginalName(){
        return this.name;
    }

    /**
     * Checks if the given item have a type which is compatible with this enchantment. This method does not check
     * if the item already have incompatible enchantments.
     * @param item The item to be checked
     * @return If the type of the item is valid for this enchantment
     */
    public boolean canEnchant(@Nonnull Item item) {
        return this.type.canEnchantItem(item);
    }

    public boolean isMajor() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Nonnull
    public SideEffect[] getAttackSideEffects(@Nonnull Entity attacker, @Nonnull Entity entity) {
        return SideEffect.EMPTY_ARRAY;
    }

    @Override
    protected Enchantment clone() {
        try {
            return (Enchantment) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Checks if an item can have this enchantment. It's not strict to the enchantment table.
     */
    @PowerNukkitOnly @Since("1.2.1.0-PN")
    @Deprecated @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", 
            reason = "Does the same as canEnchant(item)", replaceWith = "canEnchant(item)")
    public boolean isItemAcceptable(Item item) {
        return canEnchant(item);
    }

    public static final String[] words = {"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale"};

    public static String getRandomName() {
        int count = ThreadLocalRandom.current().nextInt(3, 6);
        HashSet<String> set = new LinkedHashSet<>();
        while (set.size() < count) {
            set.add(Enchantment.words[ThreadLocalRandom.current().nextInt(0, Enchantment.words.length)]);
        }

        String[] words = set.toArray(EmptyArrays.EMPTY_STRINGS);
        return String.join(" ", words);
    }

    private static class UnknownEnchantment extends Enchantment {

        protected UnknownEnchantment(int id) {
            super(id, "unknown", Rarity.VERY_RARE, EnchantmentType.ALL);
        }
    }

    /**
     * How rare an enchantment is.
     */
    @Since("1.4.0.0-PN")
    public enum Rarity {
        @Since("1.4.0.0-PN") COMMON(10),
        @Since("1.4.0.0-PN") UNCOMMON(5),
        @Since("1.4.0.0-PN") RARE(2),
        @Since("1.4.0.0-PN") VERY_RARE(1);

        private final int weight;

        Rarity(int weight) {
            this.weight = weight;
        }

        @Since("1.4.0.0-PN")
        public int getWeight() {
            return this.weight;
        }

        /**
         * Converts the weight to the closest rarity using floor semantic.
         * @param weight The enchantment weight
         * @return The closest rarity
         */
        @Since("1.4.0.0-PN")
        public static Rarity fromWeight(int weight) {
            if (weight < 2) {
                return VERY_RARE;
            } else if (weight < 5) {
                return RARE;
            } else if (weight < 10) {
                return UNCOMMON;
            }
            return COMMON;
        }
    }
}
