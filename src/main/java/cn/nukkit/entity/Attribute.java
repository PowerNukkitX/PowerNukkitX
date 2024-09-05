package cn.nukkit.entity;


import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.ServerException;
import lombok.Getter;

import javax.annotation.processing.Generated;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 属性是作用于{@link Entity}上一个的增益/减益系统。
 * <p>
 * Attributes are buffs/debuffs systems that act on {@link Entity}.
 *
 * @author Box, MagicDroidX(code), PeratX @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public class Attribute implements Cloneable {
    /**
     * 方便执行{@link Collection#toArray()}
     * <p>
     * Convenient execution of {@link Collection#toArray()}
     */
    public static final Attribute[] EMPTY_ARRAY = new Attribute[0];

    /**
     * 伤害吸收
     * <p>
     * ABSORPTION
     */
    public static final int ABSORPTION = 0;
    /**
     * 饱食度
     * <p>
     * SATURATION
     */
    public static final int SATURATION = 1;
    public static final int EXHAUSTION = 2;
    public static final int KNOCKBACK_RESISTANCE = 3;
    public static final int MAX_HEALTH = 4;
    public static final int MOVEMENT_SPEED = 5;
    public static final int FOLLOW_RANGE = 6;
    public static final int MAX_HUNGER = 7;
    public static final int FOOD = 7;
    public static final int ATTACK_DAMAGE = 8;
    public static final int EXPERIENCE_LEVEL = 9;
    public static final int EXPERIENCE = 10;
    public static final int LUCK = 11;
    public static final int HORSE_JUMP_STRENGTH = 12;
    public static final int UNDER_WATER_MOVEMENT_SPEED = 13;
    public static final int LAVA_MOVEMENT_SPEED = 14;

    protected static Map<Integer, Attribute> attributes = new HashMap<>();
    private final int id;
    protected float minValue;
    protected float maxValue;
    protected float defaultMinimum;
    protected float defaultMaximum;
    protected float defaultValue;
    protected float currentValue;
    protected String name;
    protected boolean shouldSend;

    private Attribute(int id, String name, float minValue, float maxValue, float defaultMinimum, float defaultMaximum, float defaultValue, boolean shouldSend) {
        this.id = id;
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultMinimum = defaultMinimum;
        this.defaultMaximum = defaultMaximum;
        this.defaultValue = defaultValue;
        this.shouldSend = shouldSend;
        this.currentValue = this.defaultValue;
    }

    public static void init() {
        addAttribute(ABSORPTION, "minecraft:absorption", 0.00f, 340282346638528859811704183484516925440.00f, 0.00f);
        addAttribute(SATURATION, "minecraft:player.saturation", 0.00f, 20.00f, 5.00f);
        addAttribute(EXHAUSTION, "minecraft:player.exhaustion", 0.00f, 5.00f, 0.41f);
        addAttribute(KNOCKBACK_RESISTANCE, "minecraft:knockback_resistance", 0.00f, 1.00f, 0.00f);
        addAttribute(MAX_HEALTH, "minecraft:health", 0.00f, 20.00f, 20.00f);
        addAttribute(MOVEMENT_SPEED, "minecraft:movement", 0.00f, 340282346638528859811704183484516925440.00f, 0.10f);
        addAttribute(UNDER_WATER_MOVEMENT_SPEED, "minecraft:underwater_movement", 0.00f, 340282346638528859811704183484516925440.00f, 0.02f);
        addAttribute(LAVA_MOVEMENT_SPEED, "minecraft:lava_movement", 0.00f, 340282346638528859811704183484516925440.00f, 0.02f);
        addAttribute(FOLLOW_RANGE, "minecraft:follow_range", 0.00f, 2048.00f, 16.00f, false);
        addAttribute(MAX_HUNGER, "minecraft:player.hunger", 0.00f, 20.00f, 20.00f);
        addAttribute(ATTACK_DAMAGE, "minecraft:attack_damage", 0.00f, 340282346638528859811704183484516925440.00f, 1.00f, false);
        addAttribute(EXPERIENCE_LEVEL, "minecraft:player.level", 0.00f, 24791.00f, 0.00f);
        addAttribute(EXPERIENCE, "minecraft:player.experience", 0.00f, 1.00f, 0.00f);
        addAttribute(LUCK, "minecraft:luck", -1024, 1024, 0);
        addAttribute(HORSE_JUMP_STRENGTH, "minecraft:horse.jump_strength", 0, 0.7101778f, 0.7101778f);
    }

    //SINCE 1.21.30
    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue) {
        return addAttribute(id, name, minValue, maxValue, defaultValue, true);
    }

    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        return addAttribute(id, name, minValue, maxValue, minValue, maxValue, defaultValue, shouldSend);
    }
    // END

    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultMinimum, float defaultMaximum, float defaultValue) {
        return addAttribute(id, name, minValue, maxValue, defaultMinimum, defaultMaximum, defaultValue, true);
    }

    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultMinimum, float defaultMaximum, float defaultValue, boolean shouldSend) {
        if (minValue > maxValue || defaultValue > maxValue || defaultValue < minValue) {
            throw new IllegalArgumentException("Invalid ranges: min value: " + minValue + ", max value: " + maxValue + ", defaultValue: " + defaultValue);
        }
        return attributes.put(id, new Attribute(id, name, minValue, maxValue, defaultMinimum, defaultMaximum, defaultValue, shouldSend));
    }

    /**
     * 将这个Attribute转换成NBT
     * <p>
     * Convert this attribute to NBT
     * <p>
     * like
     * <pre>
     * {
     *     "Base": 0f,
     *     "Current": 0f,
     *     "DefaultMax": 1024f,
     *     "DefaultMin": -1024f,
     *     "Max": 1024f,
     *     "Min": -1024f,
     *     "Name": "minecraft:luck"
     * }
     * </pre>
     *
     * @param attribute the attribute
     * @return the compound tag
     */
    public static CompoundTag toNBT(Attribute attribute) {
        return new CompoundTag().putString("Name", attribute.getName())
                .putFloat("Base", attribute.getDefaultValue())
                .putFloat("Current", attribute.getValue())
                .putFloat("DefaultMax", attribute.getDefaultMaximum())
                .putFloat("DefaultMin", attribute.getDefaultMinimum())
                .putFloat("Max", attribute.getMaxValue())
                .putFloat("Min", attribute.getMinValue());
    }

    /**
     * 从NBT获取Attribute
     * <p>
     * Get the Attribute from NBT
     * <p>
     * like
     * <pre>
     * {
     *     "Base": 0f,
     *     "Current": 0f,
     *     "DefaultMax": 1024f,
     *     "DefaultMin": -1024f,
     *     "Max": 1024f,
     *     "Min": -1024f,
     *     "Name": "minecraft:luck"
     * }
     * </pre>
     *
     * @param NBT the nbt
     * @return the attribute
     */
    public static Attribute fromNBT(CompoundTag NBT) {
        if (NBT.containsString("Name")
                && NBT.containsFloat("Base")
                && NBT.containsFloat("Current")
                && NBT.containsFloat("DefaultMax")
                && NBT.containsFloat("DefaultMin")
                && NBT.containsFloat("Max")
                && NBT.containsFloat("Min")) {
            return Attribute.getAttributeByName(NBT.getString("Name"))
                    .setMinValue(NBT.getFloat("Min"))
                    .setMaxValue(NBT.getFloat("Max"))
                    .setValue(NBT.getFloat("Current"))
                    .setDefaultValue(NBT.getFloat("Base"));
        }
        throw new IllegalArgumentException("NBT format error");
    }

    /**
     * 获取对应id的{@link Attribute}。
     * <p>
     * Get the {@link Attribute} of the corresponding id.
     *
     * @param id the id
     * @return the attribute
     */
    public static Attribute getAttribute(int id) {
        if (attributes.containsKey(id)) {
            return attributes.get(id).clone();
        }
        throw new ServerException("Attribute id: " + id + " not found");
    }

    /**
     * 获取对应名字的{@link Attribute}。
     * <p>
     * Get the {@link Attribute} of the corresponding name.
     *
     * @param name name
     * @return null |Attribute
     */
    public static Attribute getAttributeByName(String name) {
        for (Attribute a : attributes.values()) {
            if (Objects.equals(a.getName(), name)) {
                return a.clone();
            }
        }
        return null;
    }

    public float getMinValue() {
        return this.minValue;
    }

    public Attribute setMinValue(float minValue) {
        if (minValue > this.getMaxValue()) {
            throw new IllegalArgumentException("Value " + minValue + " is bigger than the maxValue!");
        }
        this.minValue = minValue;
        return this;
    }

    public float getMaxValue() {
        return this.maxValue;
    }

    public Attribute setMaxValue(float maxValue) {
        if (maxValue < this.getMinValue()) {
            throw new IllegalArgumentException("Value " + maxValue + " is bigger than the minValue!");
        }
        this.maxValue = maxValue;
        return this;
    }

    public float getDefaultValue() {
        return this.defaultValue;
    }

    public Attribute setDefaultValue(float defaultValue) {
        if (defaultValue > this.getMaxValue() || defaultValue < this.getMinValue()) {
            throw new IllegalArgumentException("Value " + defaultValue + " exceeds the range!");
        }
        this.defaultValue = defaultValue;
        return this;
    }

    public float getValue() {
        return this.currentValue;
    }

    public Attribute setValue(float value) {
        return setValue(value, true);
    }

    public Attribute setValue(float value, boolean fit) {
        if (value > this.getMaxValue() || value < this.getMinValue()) {
            if (!fit) {
                throw new IllegalArgumentException("Value " + value + " exceeds the range!");
            }
            value = Math.min(Math.max(value, this.getMinValue()), this.getMaxValue());
        }
        this.currentValue = value;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public boolean isSyncable() {
        return this.shouldSend;
    }

    public float getDefaultMinimum() {
        return defaultMinimum;
    }

    public float getDefaultMaximum() {
        return defaultMaximum;
    }

    @Override
    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return name + "{" +
                "min=" + minValue +
                ", max=" + maxValue +
                ", def=" + defaultValue +
                ", val=" + currentValue +
                '}';
    }
}
