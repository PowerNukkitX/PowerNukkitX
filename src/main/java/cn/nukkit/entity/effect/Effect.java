package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.registry.Registries;

import java.awt.Color;

public abstract class Effect implements Cloneable {

    private final EffectType type;
    private final String name;
    private int duration = 600;
    private int amplifier;
    private boolean visible = true;
    private boolean ambient = false;
    private final boolean bad;
    private Color color;

    public Effect(EffectType type, String name, Color color) {
        this(type, name, color, false);
    }

    public Effect(EffectType type, String name, Color color, boolean bad) {
        this.type = type;
        this.name = name;
        this.color = color;
        this.bad = bad;
    }

    public static Effect get(EffectType type) {
        return Registries.EFFECT.get(type);
    }

    public static Effect get(String id) {
        return get(EffectType.get(id));
    }

    public static Effect get(int id) {
        return get(EffectType.get(id));
    }

    public EffectType getType() {
        return type;
    }

    public Integer getId() {
        return type.id();
    }

    public String getName() {
        return name;
    }

    /**
     * get the duration of this potion in 1 tick = 0.05 s
     */
    public int getDuration() {
        return duration;
    }

    /**
     * set the duration of this potion , 1 tick = 0.05 s
     *
     * @param duration the duration
     * @return the duration
     */
    public Effect setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Get amplifier.
     */
    public int getAmplifier() {
        return amplifier;
    }

    /**
     * Sets amplifier.
     *
     * @param amplifier the amplifier
     * @return the amplifier
     */
    public Effect setAmplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    /**
     * Get the level of potion,level = amplifier + 1.
     *
     * @return the level
     */
    public int getLevel() {
        return amplifier + 1;
    }

    public boolean isVisible() {
        return visible;
    }

    public Effect setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public Effect setAmbient(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public boolean isBad() {
        return bad;
    }

    public Color getColor() {
        return color;
    }

    public Effect setColor(Color color) {
        this.color = color;
        return this;
    }

    public boolean canTick() {
        return false;
    }

    public void apply(Entity entity, double tickCount) {

    }

    public void add(Entity entity) {

    }

    public void remove(Entity entity) {

    }

    @Override
    public Effect clone() {
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}