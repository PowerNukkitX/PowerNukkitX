package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.registry.Registries;

import java.awt.*;

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
     * @return true if the effect is infinite (-1)
     */
    public boolean isInfinite() {
        return duration == -1;
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
     * Set the effect as infinite.
     *
     * @return the Effect instance
     */
    public Effect setInfinite() {
        return setDuration(-1);
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

    /**
     * Checks whether the effect should perform its action in the current tick based on its internal duration.
     * <p>
     * Note: For infinite effects (which have no duration clock), this method will generally return {@code false}
     * for periodic effects. Use {@link #canTick(int)} with the entity's lifetime ticks instead.
     *
     * @return {@code true} if the effect should tick based on its duration, {@code false} otherwise.
     */
    public boolean canTick() {
        return canTick(this.getDuration());
    }

    /**
     * Checks whether the effect should perform its action based on a specific tick clock.
     * <p>
     * This method is clock-agnostic; the provided {@code tick} can be an effect's duration, 
     * an entity's lifetime ticks, or any other temporal value.
     *
     * @param tick the current tick value to evaluate against the effect's interval.
     * @return {@code true} if the effect should tick at this specific temporal point, {@code false} otherwise.
     */
    public boolean canTick(int tick) {
        int interval = getInterval();
        return interval > 0 && tick % interval == 0;
    }

    /**
     * Returns the periodic interval (in ticks) at which this effect should perform its action.
     * <p>
     * Subclasses for periodic effects should override this to define their ticking frequency.
     *
     * @return the tick interval, where {@code 0} means no periodic action, and {@code 1} means every tick.
     */
    public int getInterval() {
        return 0;
    }

    /**
     * Orchestrates the effect's action on the given entity. 
     * <p>
     * This is the primary entry point used by the entity tick loop. it automatically 
     * selects the appropriate clock (entity lifetime for infinite effects, duration for finite ones)
     * to determine if the action {@link #apply(Entity, double)} should be performed.
     *
     * @param entity the entity to evaluate and potentially run {@link #apply(Entity, double)} for.
     */
    public void onTick(Entity entity) {
        if (this.isInfinite()) {
            if (canTick(entity.ticksLived)) {
                apply(entity, 1);
            }
        } else if (canTick()) {
            apply(entity, 1);
        }
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