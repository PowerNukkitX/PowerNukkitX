package cn.nukkit.item.enchantment;

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
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionAll;
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionExplosion;
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionFall;
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionFire;
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionProjectile;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentChanneling;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentImpaling;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentLoyalty;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentRiptide;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.registry.RegisterException;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.OK;
import cn.nukkit.utils.TextFormat;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static cn.nukkit.utils.Utils.dynamic;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V17;

/**
 * An enchantment that can be to applied to an item.
 *
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Enchantment implements Cloneable {
    public static final Enchantment[] EMPTY_ARRAY = new Enchantment[0];
    public static final int CUSTOM_ENCHANTMENT_ID = dynamic(256);
    protected static Enchantment[] enchantments;
    protected static Map<Identifier, Enchantment> namedEnchantments = new Object2ObjectLinkedOpenHashMap<>();

    public static final int ID_PROTECTION_ALL = 0;
    public static final String NAME_PROTECTION_ALL = "protection";
    public static final int ID_PROTECTION_FIRE = 1;
    public static final String NAME_PROTECTION_FIRE = "fire_protection";
    public static final int ID_PROTECTION_FALL = 2;
    public static final String NAME_PROTECTION_FALL = "feather_falling";
    public static final int ID_PROTECTION_EXPLOSION = 3;
    public static final String NAME_PROTECTION_EXPLOSION = "blast_protection";
    public static final int ID_PROTECTION_PROJECTILE = 4;
    public static final String NAME_PROTECTION_PROJECTILE = "projectile_protection";
    public static final int ID_THORNS = 5;
    public static final String NAME_THORNS = "thorns";
    public static final int ID_WATER_BREATHING = 6;
    public static final String NAME_WATER_BREATHING = "respiration";
    public static final int ID_WATER_WALKER = 7;
    public static final String NAME_WATER_WALKER = "depth_strider";
    public static final int ID_WATER_WORKER = 8;
    public static final String NAME_WATER_WORKER = "aqua_affinity";
    public static final int ID_DAMAGE_ALL = 9;
    public static final String NAME_DAMAGE_ALL = "sharpness";
    public static final int ID_DAMAGE_SMITE = 10;
    public static final String NAME_DAMAGE_SMITE = "smite";
    public static final int ID_DAMAGE_ARTHROPODS = 11;
    public static final String NAME_DAMAGE_ARTHROPODS = "bane_of_arthropods";
    public static final int ID_KNOCKBACK = 12;
    public static final String NAME_KNOCKBACK = "knockback";
    public static final int ID_FIRE_ASPECT = 13;
    public static final String NAME_FIRE_ASPECT = "fire_aspect";
    public static final int ID_LOOTING = 14;
    public static final String NAME_LOOTING = "looting";
    public static final int ID_EFFICIENCY = 15;
    public static final String NAME_EFFICIENCY = "efficiency";
    public static final int ID_SILK_TOUCH = 16;
    public static final String NAME_SILK_TOUCH = "silk_touch";
    public static final int ID_DURABILITY = 17;
    public static final String NAME_DURABILITY = "unbreaking";
    public static final int ID_FORTUNE_DIGGING = 18;
    public static final String NAME_FORTUNE_DIGGING = "fortune";
    public static final int ID_BOW_POWER = 19;
    public static final String NAME_BOW_POWER = "power";
    public static final int ID_BOW_KNOCKBACK = 20;
    public static final String NAME_BOW_KNOCKBACK = "punch";
    public static final int ID_BOW_FLAME = 21;
    public static final String NAME_BOW_FLAME = "flame";
    public static final int ID_BOW_INFINITY = 22;
    public static final String NAME_BOW_INFINITY = "infinity";
    public static final int ID_FORTUNE_FISHING = 23;
    public static final String NAME_FORTUNE_FISHING = "luck_of_the_sea";
    public static final int ID_LURE = 24;
    public static final String NAME_LURE = "lure";
    public static final int ID_FROST_WALKER = 25;
    public static final String NAME_FROST_WALKER = "frost_walker";
    public static final int ID_MENDING = 26;
    public static final String NAME_MENDING = "mending";
    public static final int ID_BINDING_CURSE = 27;
    public static final String NAME_BINDING_CURSE = "binding";
    public static final int ID_VANISHING_CURSE = 28;
    public static final String NAME_VANISHING_CURSE = "vanishing";
    public static final int ID_TRIDENT_IMPALING = 29;
    public static final String NAME_TRIDENT_IMPALING = "impaling";
    public static final int ID_TRIDENT_RIPTIDE = 30;
    public static final String NAME_TRIDENT_RIPTIDE = "riptide";
    public static final int ID_TRIDENT_LOYALTY = 31;
    public static final String NAME_TRIDENT_LOYALTY = "loyalty";
    public static final int ID_TRIDENT_CHANNELING = 32;
    public static final String NAME_TRIDENT_CHANNELING = "channeling";
    public static final int ID_CROSSBOW_MULTISHOT = 33;
    public static final String NAME_CROSSBOW_MULTISHOT = "multishot";
    public static final int ID_CROSSBOW_PIERCING = 34;
    public static final String NAME_CROSSBOW_PIERCING = "piercing";
    public static final int ID_CROSSBOW_QUICK_CHARGE = 35;
    public static final String NAME_CROSSBOW_QUICK_CHARGE = "quick_charge";
    public static final int ID_SOUL_SPEED = 36;
    public static final String NAME_SOUL_SPEED = "soul_speed";
    public static final int ID_SWIFT_SNEAK = 37;
    public static final String NAME_SWIFT_SNEAK = "swift_sneak";

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
        enchantments[ID_MENDING] = new EnchantmentMending();
        enchantments[ID_BINDING_CURSE] = new EnchantmentBindingCurse();
        enchantments[ID_VANISHING_CURSE] = new EnchantmentVanishingCurse();
        enchantments[ID_TRIDENT_IMPALING] = new EnchantmentTridentImpaling();
        enchantments[ID_TRIDENT_RIPTIDE] = new EnchantmentTridentRiptide();
        enchantments[ID_TRIDENT_LOYALTY] = new EnchantmentTridentLoyalty();
        enchantments[ID_TRIDENT_CHANNELING] = new EnchantmentTridentChanneling();
        enchantments[ID_CROSSBOW_MULTISHOT] = new EnchantmentCrossbowMultishot();
        enchantments[ID_CROSSBOW_PIERCING] = new EnchantmentCrossbowPiercing();
        enchantments[ID_CROSSBOW_QUICK_CHARGE] = new EnchantmentCrossbowQuickCharge();
        enchantments[ID_SOUL_SPEED] = new EnchantmentSoulSpeed();
        enchantments[ID_SWIFT_SNEAK] = new EnchantmentSwiftSneak();
        //custom
        namedEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_ALL), enchantments[0]);
        namedEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_FIRE), enchantments[1]);
        namedEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_FALL), enchantments[2]);
        namedEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_EXPLOSION), enchantments[3]);
        namedEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_PROJECTILE), enchantments[4]);
        namedEnchantments.put(new Identifier("minecraft", NAME_THORNS), enchantments[5]);
        namedEnchantments.put(new Identifier("minecraft", NAME_WATER_BREATHING), enchantments[6]);
        namedEnchantments.put(new Identifier("minecraft", NAME_WATER_WORKER), enchantments[7]);
        namedEnchantments.put(new Identifier("minecraft", NAME_WATER_WALKER), enchantments[8]);
        namedEnchantments.put(new Identifier("minecraft", NAME_DAMAGE_ALL), enchantments[9]);
        namedEnchantments.put(new Identifier("minecraft", NAME_DAMAGE_SMITE), enchantments[10]);
        namedEnchantments.put(new Identifier("minecraft", NAME_DAMAGE_ARTHROPODS), enchantments[11]);
        namedEnchantments.put(new Identifier("minecraft", NAME_KNOCKBACK), enchantments[12]);
        namedEnchantments.put(new Identifier("minecraft", NAME_FIRE_ASPECT), enchantments[13]);
        namedEnchantments.put(new Identifier("minecraft", NAME_LOOTING), enchantments[14]);
        namedEnchantments.put(new Identifier("minecraft", NAME_EFFICIENCY), enchantments[15]);
        namedEnchantments.put(new Identifier("minecraft", NAME_SILK_TOUCH), enchantments[16]);
        namedEnchantments.put(new Identifier("minecraft", NAME_DURABILITY), enchantments[17]);
        namedEnchantments.put(new Identifier("minecraft", NAME_FORTUNE_DIGGING), enchantments[18]);
        namedEnchantments.put(new Identifier("minecraft", NAME_BOW_POWER), enchantments[19]);
        namedEnchantments.put(new Identifier("minecraft", NAME_BOW_KNOCKBACK), enchantments[20]);
        namedEnchantments.put(new Identifier("minecraft", NAME_BOW_FLAME), enchantments[21]);
        namedEnchantments.put(new Identifier("minecraft", NAME_BOW_INFINITY), enchantments[22]);
        namedEnchantments.put(new Identifier("minecraft", NAME_FORTUNE_FISHING), enchantments[23]);
        namedEnchantments.put(new Identifier("minecraft", NAME_LURE), enchantments[24]);
        namedEnchantments.put(new Identifier("minecraft", NAME_FROST_WALKER), enchantments[25]);
        namedEnchantments.put(new Identifier("minecraft", NAME_MENDING), enchantments[26]);
        namedEnchantments.put(new Identifier("minecraft", NAME_BINDING_CURSE), enchantments[27]);
        namedEnchantments.put(new Identifier("minecraft", NAME_VANISHING_CURSE), enchantments[28]);
        namedEnchantments.put(new Identifier("minecraft", NAME_TRIDENT_IMPALING), enchantments[29]);
        namedEnchantments.put(new Identifier("minecraft", NAME_TRIDENT_RIPTIDE), enchantments[30]);
        namedEnchantments.put(new Identifier("minecraft", NAME_TRIDENT_LOYALTY), enchantments[31]);
        namedEnchantments.put(new Identifier("minecraft", NAME_TRIDENT_CHANNELING), enchantments[32]);
        namedEnchantments.put(new Identifier("minecraft", NAME_CROSSBOW_MULTISHOT), enchantments[33]);
        namedEnchantments.put(new Identifier("minecraft", NAME_CROSSBOW_PIERCING), enchantments[34]);
        namedEnchantments.put(new Identifier("minecraft", NAME_CROSSBOW_QUICK_CHARGE), enchantments[35]);
        namedEnchantments.put(new Identifier("minecraft", NAME_SOUL_SPEED), enchantments[36]);
        namedEnchantments.put(new Identifier("minecraft", NAME_SWIFT_SNEAK), enchantments[37]);
    }

    private static String getLevelString(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> "∞";
        };
    }

    public static void reload() {
        enchantments = new Enchantment[256];
        namedEnchantments.clear();
        init();
    }

    public static OK<?> register(Enchantment enchantment, boolean registerItem) {
        Objects.requireNonNull(enchantment);
        Objects.requireNonNull(enchantment.getIdentifier());
        if (namedEnchantments.containsKey(enchantment.getIdentifier())) {
            return new OK<>(false, "This identifier already exists,register custom enchantment failed!");
        }
        if (enchantment.getIdentifier().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
            return new OK<>(false, "Please do not use the reserved namespace `minecraft` !");
        }
        namedEnchantments.put(enchantment.getIdentifier(), enchantment);
        if (registerItem) {
            return registerCustomEnchantBook(enchantment);
        }
        return OK.TRUE;
    }

    public static OK<?> register(Enchantment... enchantments) {
        for (var ench : enchantments) {
            var msg = register(ench, true);
            if (!msg.ok()) {
                return msg;
            }
        }
        return OK.TRUE;
    }

    private static int BOOK_NUMBER = 1;


    @SuppressWarnings("unchecked")
    private static OK<?> registerCustomEnchantBook(Enchantment enchantment) {
        var identifier = enchantment.getIdentifier();
        assert identifier != null;
        for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
            var name = "§eEnchanted Book\n§7" + enchantment.getName() + " " + getLevelString(i);
            ClassWriter classWriter = new ClassWriter(0);
            MethodVisitor methodVisitor;
            String className = "CustomBookEnchanted" + BOOK_NUMBER;
            classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, "cn/nukkit/item/customitem/" + className, null, "cn/nukkit/item/customitem/ItemCustomBookEnchanted", null);
            classWriter.visitSource(className + ".java", null);
            {
                methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                methodVisitor.visitCode();
                Label label0 = new Label();
                methodVisitor.visitLabel(label0);
                methodVisitor.visitLineNumber(5, label0);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitLdcInsn(identifier.toString() + i);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "cn/nukkit/item/customitem/ItemCustomBookEnchanted", "<init>", "(Ljava/lang/String;)V", false);
                Label label1 = new Label();
                methodVisitor.visitLabel(label1);
                methodVisitor.visitLineNumber(6, label1);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitLdcInsn(name);
                methodVisitor.visitFieldInsn(PUTFIELD, "cn/nukkit/item/customitem/" + className, "name", "Ljava/lang/String;");
                Label label2 = new Label();
                methodVisitor.visitLabel(label2);
                methodVisitor.visitLineNumber(7, label2);
                methodVisitor.visitInsn(RETURN);
                Label label3 = new Label();
                methodVisitor.visitLabel(label3);
                methodVisitor.visitLocalVariable("this", "Lcn/nukkit/item/customitem/" + className + ";", null, label0, label3, 0);
                methodVisitor.visitMaxs(2, 1);
                methodVisitor.visitEnd();
            }
            classWriter.visitEnd();
            BOOK_NUMBER++;
            try {
                Class<? extends Item> clazz = (Class<? extends Item>) loadClass(Thread.currentThread().getContextClassLoader(), "cn.nukkit.item.customitem." + className, classWriter.toByteArray());
                Registries.ITEM.registerCustomItem(InternalPlugin.INSTANCE, clazz);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException | AssertionError | RegisterException e) {
                return new OK<>(false, e);
            }
        }
        return OK.TRUE;
    }

    private static WeakReference<Method> defineClassMethodRef = new WeakReference<>(null);


    @SuppressWarnings("DuplicatedCode")
    private static Class<?> loadClass(ClassLoader loader, String className, byte[] b) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InaccessibleObjectException {
        Class<?> clazz;
        java.lang.reflect.Method method;
        if (defineClassMethodRef.get() == null) {
            var cls = Class.forName("java.lang.ClassLoader");
            method = cls.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClassMethodRef = new WeakReference<>(method);
        } else {
            method = defineClassMethodRef.get();
        }
        Objects.requireNonNull(method).setAccessible(true);
        var args = new Object[]{className, b, 0, b.length};
        clazz = (Class<?>) method.invoke(loader, args);
        return clazz;
    }

    /**
     * Returns the enchantment object registered with this ID, any change to the returned object affects
     * the creation of new enchantments as the returned object is not a copy.
     *
     * @param id The enchantment id.
     * @return The enchantment, if no enchantment is found with that id, {@link UnknownEnchantment} is returned.
     * The UnknownEnchantment will be always a new instance and changes to it does not affects other calls.
     */
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
     * Returns the enchantment object registered with this ID
     *
     * @param id The enchantment id.
     * @return The enchantment, if no enchantment is found with that id, {@link UnknownEnchantment} is returned.
     * The UnknownEnchantment will be always a new instance and changes to it does not affects other calls.
     */
    public static Enchantment getEnchantment(int id) {
        Enchantment enchantment = null;
        if (id >= 0 && id < enchantments.length) {
            enchantment = enchantments[id];
        }
        if (enchantment == null) {
            return new UnknownEnchantment(id);
        }
        return enchantment.clone();
    }

    /**
     * 使用附魔标识符来获取附魔，原版附魔可以不加命名空间，但是自定义附魔必须加上命名空间才能获取
     * <p>
     * Gets enchantment.
     *
     * @param name Enchantment Identifier
     * @return the enchantment
     */
    public static Enchantment getEnchantment(String name) {
        if (Identifier.isValid(name)) {
            return namedEnchantments.get(Identifier.tryParse(name)).clone();
        } else return namedEnchantments.get(new Identifier(Identifier.DEFAULT_NAMESPACE, name)).clone();
    }

    public static Enchantment getEnchantment(@NotNull Identifier name) {
        return namedEnchantments.get(name).clone();
    }

    /**
     * Gets an array of all registered enchantments, the objects in the array are linked to the registry,
     * it's not safe to change them. Changing them can cause the same issue as documented in {@link #get(int)}
     *
     * @return An array with the enchantment objects, the array may contain null objects but is very unlikely.
     */
    public static Enchantment[] getEnchantments() {
        return namedEnchantments.values().toArray(EMPTY_ARRAY);
    }

    /**
     * Gets a collection with a safe copy of all enchantments that are currently registered.
     *
     * @return The objects can be modified without affecting the registry and the collection will not have null values.
     */
    public static Collection<Enchantment> getRegisteredEnchantments() {
        return new ArrayList<>(namedEnchantments.values());
    }

    public static Map<String, Integer> getEnchantmentName2IDMap() {
        return namedEnchantments.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().getId()));
    }

    /**
     * The internal ID which this enchantment got registered.
     */
    public final int id;
    private final Rarity rarity;
    /**
     * The group of objects that this enchantment can be applied.
     */
    @NotNull
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
    @Nullable
    protected final Identifier identifier;

    /**
     * Constructs this instance using the given data and with level 1.
     *
     * @param id     The enchantment ID
     * @param name   The translation key without the "%enchantment." suffix
     * @param rarity How rare this enchantment is
     * @param type   Where the enchantment can be applied
     */
    protected Enchantment(int id, String name, Rarity rarity, @NotNull EnchantmentType type) {
        this.identifier = null;
        this.id = id;
        this.rarity = rarity;
        this.type = type;
        this.name = name;
    }

    /**
     * 自定义附魔使用的构造函数
     *
     * @param name   The translation key without the "%enchantment." suffix
     * @param rarity How rare this enchantment is
     * @param type   Where the enchantment can be applied
     */
    protected Enchantment(@NotNull Identifier identifier, String name, Rarity rarity, @NotNull EnchantmentType type) {
        this.identifier = identifier;
        this.id = CUSTOM_ENCHANTMENT_ID;
        this.rarity = rarity;
        this.type = type;
        this.name = name;
    }

    /**
     * 获取该附魔的标识符，只有自定义附魔才有
     *
     * @return the identifier
     */
    public @Nullable Identifier getIdentifier() {
        return identifier;
    }

    /**
     * 获取该附魔在物品描述中的Lore,被自定义附魔用于添加描述,代表物品附魔描述中的一行
     * <p>
     * Get the enchantment in the item description Lore, which is used by the custom enchantment to add a description, representing a line in the item's enchantment description
     *
     * @return the lore
     */
    public String getLore() {
        return TextFormat.GRAY + this.getName() + " " + Enchantment.getLevelString(this.getLevel());
    }

    /**
     * The current level of this enchantment. {@code 0} means that the enchantment is not applied.
     *
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

    @NotNull
    public Enchantment setLevel(int level) {
        return this.setLevel(level, true);
    }

    /**
     * Changes the level of this enchantment.
     * When the {@code safe} param is {@code true}, the level is clamped between the values
     * returned in {@link #getMinLevel()} and {@link #getMaxLevel()}.
     *
     * @param level The level starting from {@code 1}.
     * @param safe  If the level should clamped or applied directly
     * @return This object so you can do chained calls
     */
    @NotNull
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

    @NotNull
    public Rarity getRarity() {
        return this.rarity;
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
     * The minimum enchantability for the given level as described in https://minecraft.wiki/w/Enchanting/Levels
     *
     * @param level The level being checked
     * @return The minimum value
     */
    public int getMinEnchantAbility(int level) {
        return 1 + level * 10;
    }

    /**
     * The maximum enchantability for the given level as described in https://minecraft.wiki/w/Enchanting/Levels
     *
     * @param level The level being checked
     * @return The maximum value
     */
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 5;
    }

    /**
     * 当实体盔甲具有附魔时触发
     * <p>
     * 覆写该方法提供当实体被攻击时盔甲提供的保护值
     * <p>
     * 目前只生效于{@link cn.nukkit.entity.EntityHumanType HumanType} 和 {@link cn.nukkit.entity.mob.EntityMob EntityMob}
     *
     * @param event 该实体被攻击的事件
     * @return the protection factor
     */
    public float getProtectionFactor(EntityDamageEvent event) {
        return 0;
    }

    /**
     * 当实体武器具有附魔时触发
     * <p>
     * 覆写该方法提供当实体使用附魔武器攻击所增益的攻击力
     * <p>
     * 目前只生效于{@link cn.nukkit.Player Player} 和 使用了{@link cn.nukkit.entity.ai.executor.MeleeAttackExecutor MeleeAttackExecutor}行为的实体
     *
     * @param entity 攻击的目标实体
     * @return the damage value
     */
    public double getDamageBonus(Entity entity) {
        return 0;
    }

    /**
     * 当实体entity穿着附魔盔甲，被实体attacker攻击时触发
     * <p>
     * 覆写该方法实现该过程中的逻辑
     *
     * @param attacker the attacker
     * @param entity   the entity
     */
    public void doPostAttack(Entity attacker, Entity entity) {
    }

    /**
     * 当实体attacker使用具有附魔的武器攻击实体entity时触发
     * <p>
     * 覆写该方法实现该过程中的逻辑
     *
     * @param attacker the attacker
     * @param entity   the entity
     */
    public void doAttack(Entity attacker, Entity entity) {
    }

    /**
     * 目前没有任何作用
     *
     * @param attacker the attacker
     * @param entity   the entity
     */

    public void doPostHurt(Entity attacker, Entity entity) {
    }

    /**
     * Returns true if and only if this enchantment is compatible with the other and
     * the other is also compatible with this enchantment.
     *
     * @param enchantment The enchantment which is being checked
     * @return If both enchantments are compatible
     * @implNote Cloudburst Nukkit added the final modifier, PowerNukkit removed it to maintain backward compatibility.
     * The right way to implement compatibility now is to override {@link #checkCompatibility(Enchantment)}
     * and also make sure to keep it protected! Some overrides was incorrectly made public, let's avoid this mistake
     */

    public boolean isCompatibleWith(@NotNull Enchantment enchantment) {
        return this.checkCompatibility(enchantment) && enchantment.checkCompatibility(this);
    }

    /**
     * Checks if this enchantment can be applied to an item that have the give enchantment without doing reverse check.
     *
     * @param enchantment The enchantment to be checked
     * @return If this enchantment is compatible with the other enchantment.
     */
    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment;
    }

    //return the translation key for the enchantment
    public String getName() {
        if (this.identifier == null) return "%enchantment." + this.name;
        else return this.name;
    }

    public String getOriginalName() {
        return this.name;
    }

    /**
     * Checks if the given item have a type which is compatible with this enchantment. This method does not check
     * if the item already have incompatible enchantments.
     *
     * @param item The item to be checked
     * @return If the type of the item is valid for this enchantment
     */
    public boolean canEnchant(@NotNull Item item) {
        return this.type.canEnchantItem(item);
    }

    public boolean isMajor() {
        return false;
    }

    @Override
    protected Enchantment clone() {
        try {
            return (Enchantment) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
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

    public static boolean equal(Enchantment e1, Enchantment e2) {
        if (e1.identifier == null && e2.identifier == null) {
            return e1.id == e2.id;
        } else if (e1.identifier != null && e2.identifier != null) {
            return e1.identifier == e2.identifier;
        } else return false;
    }


    /**
     * How rare an enchantment is.
     */

    public enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        Rarity(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return this.weight;
        }

        /**
         * Converts the weight to the closest rarity using floor semantic.
         *
         * @param weight The enchantment weight
         * @return The closest rarity
         */

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
