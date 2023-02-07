package cn.nukkit.item.enchantment;

import cn.nukkit.api.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.CustomItem;
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
import cn.nukkit.item.enchantment.trident.EnchantmentTridentChanneling;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentImpaling;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentLoyalty;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentRiptide;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.OK;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static cn.nukkit.utils.Utils.dynamic;
import static org.objectweb.asm.Opcodes.*;

/**
 * An enchantment that can be to applied to an item.
 *
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public abstract class Enchantment implements Cloneable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Enchantment[] EMPTY_ARRAY = new Enchantment[0];

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public static final int CUSTOM_ENCHANTMENT_ID = dynamic(256);

    protected static Enchantment[] enchantments;

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    protected static Map<Identifier, Enchantment> customEnchantments = new Object2ObjectLinkedOpenHashMap<>();

    public static final int ID_PROTECTION_ALL = 0;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_PROTECTION_ALL = "protection";
    public static final int ID_PROTECTION_FIRE = 1;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_PROTECTION_FIRE = "fire_protection";
    public static final int ID_PROTECTION_FALL = 2;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_PROTECTION_FALL = "feather_falling";
    public static final int ID_PROTECTION_EXPLOSION = 3;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_PROTECTION_EXPLOSION = "blast_protection";
    public static final int ID_PROTECTION_PROJECTILE = 4;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_PROTECTION_PROJECTILE = "projectile_protection";
    public static final int ID_THORNS = 5;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_THORNS = "thorns";
    public static final int ID_WATER_BREATHING = 6;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_WATER_BREATHING = "respiration";
    public static final int ID_WATER_WALKER = 7;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_WATER_WALKER = "depth_strider";
    public static final int ID_WATER_WORKER = 8;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_WATER_WORKER = "aqua_affinity";
    public static final int ID_DAMAGE_ALL = 9;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_DAMAGE_ALL = "sharpness";
    public static final int ID_DAMAGE_SMITE = 10;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_DAMAGE_SMITE = "smite";
    public static final int ID_DAMAGE_ARTHROPODS = 11;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_DAMAGE_ARTHROPODS = "bane_of_arthropods";
    public static final int ID_KNOCKBACK = 12;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_KNOCKBACK = "knockback";
    public static final int ID_FIRE_ASPECT = 13;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_FIRE_ASPECT = "fire_aspect";
    public static final int ID_LOOTING = 14;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_LOOTING = "looting";
    public static final int ID_EFFICIENCY = 15;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_EFFICIENCY = "efficiency";
    public static final int ID_SILK_TOUCH = 16;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_SILK_TOUCH = "silk_touch";
    public static final int ID_DURABILITY = 17;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_DURABILITY = "unbreaking";
    public static final int ID_FORTUNE_DIGGING = 18;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_FORTUNE_DIGGING = "fortune";
    public static final int ID_BOW_POWER = 19;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_BOW_POWER = "power";
    public static final int ID_BOW_KNOCKBACK = 20;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_BOW_KNOCKBACK = "punch";
    public static final int ID_BOW_FLAME = 21;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_BOW_FLAME = "flame";
    public static final int ID_BOW_INFINITY = 22;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_BOW_INFINITY = "infinity";
    public static final int ID_FORTUNE_FISHING = 23;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_FORTUNE_FISHING = "luck_of_the_sea";
    public static final int ID_LURE = 24;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_LURE = "lure";
    public static final int ID_FROST_WALKER = 25;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_FROST_WALKER = "frost_walker";

    public static final int ID_MENDING = 26;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_MENDING = "mending";
    public static final int ID_BINDING_CURSE = 27;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_BINDING_CURSE = "binding";
    public static final int ID_VANISHING_CURSE = 28;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_VANISHING_CURSE = "vanishing";
    public static final int ID_TRIDENT_IMPALING = 29;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_TRIDENT_IMPALING = "impaling";
    public static final int ID_TRIDENT_RIPTIDE = 30;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_TRIDENT_RIPTIDE = "riptide";
    public static final int ID_TRIDENT_LOYALTY = 31;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_TRIDENT_LOYALTY = "loyalty";
    public static final int ID_TRIDENT_CHANNELING = 32;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_TRIDENT_CHANNELING = "channeling";
    @Since("1.4.0.0-PN")
    public static final int ID_CROSSBOW_MULTISHOT = 33;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_CROSSBOW_MULTISHOT = "multishot";
    @Since("1.4.0.0-PN")
    public static final int ID_CROSSBOW_PIERCING = 34;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_CROSSBOW_PIERCING = "piercing";
    @Since("1.4.0.0-PN")
    public static final int ID_CROSSBOW_QUICK_CHARGE = 35;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_CROSSBOW_QUICK_CHARGE = "quick_charge";
    @Since("1.4.0.0-PN")
    public static final int ID_SOUL_SPEED = 36;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final String NAME_SOUL_SPEED = "soul_speed";
    @Since("1.4.0.0-PN")
    public static final int ID_SWIFT_SNEAK = 37;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
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
        customEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_ALL), enchantments[0]);
        customEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_FIRE), enchantments[1]);
        customEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_FALL), enchantments[2]);
        customEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_EXPLOSION), enchantments[3]);
        customEnchantments.put(new Identifier("minecraft", NAME_PROTECTION_PROJECTILE), enchantments[4]);
        customEnchantments.put(new Identifier("minecraft", NAME_THORNS), enchantments[5]);
        customEnchantments.put(new Identifier("minecraft", NAME_WATER_BREATHING), enchantments[6]);
        customEnchantments.put(new Identifier("minecraft", NAME_WATER_WORKER), enchantments[7]);
        customEnchantments.put(new Identifier("minecraft", NAME_WATER_WALKER), enchantments[8]);
        customEnchantments.put(new Identifier("minecraft", NAME_DAMAGE_ALL), enchantments[9]);
        customEnchantments.put(new Identifier("minecraft", NAME_DAMAGE_SMITE), enchantments[10]);
        customEnchantments.put(new Identifier("minecraft", NAME_DAMAGE_ARTHROPODS), enchantments[11]);
        customEnchantments.put(new Identifier("minecraft", NAME_KNOCKBACK), enchantments[12]);
        customEnchantments.put(new Identifier("minecraft", NAME_FIRE_ASPECT), enchantments[13]);
        customEnchantments.put(new Identifier("minecraft", NAME_LOOTING), enchantments[14]);
        customEnchantments.put(new Identifier("minecraft", NAME_EFFICIENCY), enchantments[15]);
        customEnchantments.put(new Identifier("minecraft", NAME_SILK_TOUCH), enchantments[16]);
        customEnchantments.put(new Identifier("minecraft", NAME_DURABILITY), enchantments[17]);
        customEnchantments.put(new Identifier("minecraft", NAME_FORTUNE_DIGGING), enchantments[18]);
        customEnchantments.put(new Identifier("minecraft", NAME_BOW_POWER), enchantments[19]);
        customEnchantments.put(new Identifier("minecraft", NAME_BOW_KNOCKBACK), enchantments[20]);
        customEnchantments.put(new Identifier("minecraft", NAME_BOW_FLAME), enchantments[21]);
        customEnchantments.put(new Identifier("minecraft", NAME_BOW_INFINITY), enchantments[22]);
        customEnchantments.put(new Identifier("minecraft", NAME_FORTUNE_FISHING), enchantments[23]);
        customEnchantments.put(new Identifier("minecraft", NAME_LURE), enchantments[24]);
        customEnchantments.put(new Identifier("minecraft", NAME_FROST_WALKER), enchantments[25]);
        customEnchantments.put(new Identifier("minecraft", NAME_MENDING), enchantments[26]);
        customEnchantments.put(new Identifier("minecraft", NAME_BINDING_CURSE), enchantments[27]);
        customEnchantments.put(new Identifier("minecraft", NAME_VANISHING_CURSE), enchantments[28]);
        customEnchantments.put(new Identifier("minecraft", NAME_TRIDENT_IMPALING), enchantments[29]);
        customEnchantments.put(new Identifier("minecraft", NAME_TRIDENT_RIPTIDE), enchantments[30]);
        customEnchantments.put(new Identifier("minecraft", NAME_TRIDENT_LOYALTY), enchantments[31]);
        customEnchantments.put(new Identifier("minecraft", NAME_TRIDENT_CHANNELING), enchantments[32]);
        customEnchantments.put(new Identifier("minecraft", NAME_CROSSBOW_MULTISHOT), enchantments[33]);
        customEnchantments.put(new Identifier("minecraft", NAME_CROSSBOW_PIERCING), enchantments[34]);
        customEnchantments.put(new Identifier("minecraft", NAME_CROSSBOW_QUICK_CHARGE), enchantments[35]);
        customEnchantments.put(new Identifier("minecraft", NAME_SOUL_SPEED), enchantments[36]);
        customEnchantments.put(new Identifier("minecraft", NAME_SWIFT_SNEAK), enchantments[37]);
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public static String getLevelString(int level) {
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

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public static OK<?> register(Enchantment enchantment, boolean registerItem) {
        Objects.requireNonNull(enchantment);
        Objects.requireNonNull(enchantment.getIdentifier());
        if (customEnchantments.containsKey(enchantment.getIdentifier())) {
            return new OK<>(false, "This identifier already exists,register custom enchantment failed!");
        }
        if (enchantment.getIdentifier().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
            return new OK<>(false, "Please do not use the reserved namespace `minecraft` !");
        }
        customEnchantments.put(enchantment.getIdentifier(), enchantment);
        if (registerItem) {
            return registerCustomEnchantBook(enchantment);
        }
        return OK.TRUE;
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public static OK<?> register(Enchantment... enchantments) {
        for (var ench : enchantments) {
            var msg = register(ench, true);
            if (!msg.ok()) {
                return msg;
            }
        }
        return OK.TRUE;
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    private static int BOOK_NUMBER = 1;

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
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
                methodVisitor.visitLineNumber(6, label0);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitLdcInsn(identifier.toString());
                methodVisitor.visitLdcInsn(name);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "cn/nukkit/item/customitem/ItemCustomBookEnchanted", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                Label label1 = new Label();
                methodVisitor.visitLabel(label1);
                methodVisitor.visitLineNumber(7, label1);
                methodVisitor.visitInsn(RETURN);
                Label label2 = new Label();
                methodVisitor.visitLabel(label2);
                methodVisitor.visitLocalVariable("this", "Lcn/nukkit/item/customitem/" + className + ";", null, label0, label2, 0);
                methodVisitor.visitMaxs(3, 1);
                methodVisitor.visitEnd();
            }
            classWriter.visitEnd();
            BOOK_NUMBER++;
            try {
                Class<? extends CustomItem> clazz = (Class<? extends CustomItem>) loadClass(Thread.currentThread().getContextClassLoader(), "cn.nukkit.item.customitem." + className, classWriter.toByteArray());
                Item.registerCustomItem(clazz).assertOK();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException | AssertionError e) {
                return new OK<>(false, e);
            }
        }
        return OK.TRUE;
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    private static WeakReference<Method> defineClassMethodRef = new WeakReference<>(null);

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
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
        try {
            var args = new Object[]{className, b, 0, b.length};
            clazz = (Class<?>) method.invoke(loader, args);
        } finally {
            method.setAccessible(false);
        }
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
     *
     * @param id The enchantment id
     * @return A new enchantment object.
     */
    public static Enchantment getEnchantment(int id) {
        return get(id).clone();
    }

    /**
     * 使用附魔标识符来获取附魔，原版附魔可以不加命名空间，但是自定义附魔必须加上命名空间才能获取
     * <p>
     * Gets enchantment.
     *
     * @param name Enchantment Identifier
     * @return the enchantment
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static Enchantment getEnchantment(String name) {
        if (Identifier.isValid(name)) {
            return customEnchantments.get(Identifier.tryParse(name));
        } else return customEnchantments.get(new Identifier(Identifier.DEFAULT_NAMESPACE, name));
    }

    /**
     * Gets an array of all registered enchantments, the objects in the array are linked to the registry,
     * it's not safe to change them. Changing them can cause the same issue as documented in {@link #get(int)}
     *
     * @return An array with the enchantment objects, the array may contain null objects but is very unlikely.
     */
    public static Enchantment[] getEnchantments() {
        return customEnchantments.values().toArray(EMPTY_ARRAY);
    }

    /**
     * Gets a collection with a safe copy of all enchantments that are currently registered.
     *
     * @return The objects can be modified without affecting the registry and the collection will not have null values.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static Collection<Enchantment> getRegisteredEnchantments() {
        return new ArrayList<>(customEnchantments.values());
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static Map<String, Integer> getEnchantmentName2IDMap() {
        return customEnchantments.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().getId()));
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
     * @param weight How rare this enchantment is, from {@code 1} to {@code 10} both inclusive where {@code 1} is the rarest
     * @param type   Where the enchantment can be applied
     */
    @PowerNukkitOnly("Was removed from Nukkit in 1.4.0.0-PN, keeping it in PowerNukkit for backward compatibility")
    @Deprecated
    @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.4.0.0-PN", reason = "Changed the signature without backward compatibility",
            replaceWith = "Enchantment(int, String, Rarity, EnchantmentType)")
    protected Enchantment(int id, String name, int weight, @NotNull EnchantmentType type) {
        this(id, name, Rarity.fromWeight(weight), type);
    }

    /**
     * Constructs this instance using the given data and with level 1.
     *
     * @param id     The enchantment ID
     * @param name   The translation key without the "%enchantment." suffix
     * @param rarity How rare this enchantment is
     * @param type   Where the enchantment can be applied
     */
    @Since("1.4.0.0-PN")
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
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
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
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    @Nullable
    public Identifier getIdentifier() {
        return identifier;
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
    @Since("1.4.0.0-PN")
    @NotNull
    public Rarity getRarity() {
        return this.rarity;
    }

    /**
     * How rare this enchantment is, from {@code 1} to {@code 10} where {@code 1} is the rarest.
     *
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
     *
     * @param level The level being checked
     * @return The minimum value
     */
    public int getMinEnchantAbility(int level) {
        return 1 + level * 10;
    }

    /**
     * The maximum enchantability for the given level as described in https://minecraft.gamepedia.com/Enchanting/Levels
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
    @PowerNukkitXDifference(info = "移除在InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK中的切入点，因为这会与#doAttack混淆，他们是一样的作用")
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
    @Since("FUTURE")
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
    @PowerNukkitDifference(since = "1.4.0.0-PN",
            info = "Cloudburst Nukkit added the final modifier, we removed it to maintain backward compatibility. " +
                    "The right way to implement compatibility now is to override checkCompatibility(Enchantment enchantment) " +
                    "and also make sure to keep it protected! Some overrides was incorrectly made public, let's avoid this mistake."
    )
    public boolean isCompatibleWith(@NotNull Enchantment enchantment) {
        return this.checkCompatibility(enchantment) && enchantment.checkCompatibility(this);
    }

    /**
     * Checks if this enchantment can be applied to an item that have the give enchantment without doing reverse check.
     *
     * @param enchantment The enchantment to be checked
     * @return If this enchantment is compatible with the other enchantment.
     */
    @Since("1.4.0.0-PN")
    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment;
    }

    //return the translation key for the enchantment
    public String getName() {
        if (this.identifier == null) return "%enchantment." + this.name;
        else return this.name;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
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

    /**
     * Checks if an item can have this enchantment. It's not strict to the enchantment table.
     */
    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN",
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
         *
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
