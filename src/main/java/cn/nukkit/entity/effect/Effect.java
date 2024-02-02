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

    public int getId() {
        return type.getId();
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public Effect setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public Effect setAmplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

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

    public void apply(Entity entity, double health) {

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