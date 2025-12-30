package cn.nukkit.utils.firework;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFireworksRocket;
import cn.nukkit.item.ItemFireworkRocket;
import cn.nukkit.item.ItemFireworkRocket.FireworkExplosion.ExplosionType;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Builder for a Firework allowing to include multiple explosions {@link ExplosionBuilder} and to provide an additional {@link Entity} as rider.
 * <p>
 * The rider is unmounted upon explosion of the firework.
 * <p>
 * Returns a {@link EntityFireworksRocket} upon build.
 *
 * @author herby2212
 * @since 2025/04/08
 */
public class FireworkBuilder {
	private final ItemFireworkRocket itemFirework;
    private final CompoundTag fireworkTag;
    private final List<CompoundTag> explosions;
    private final Random fireworkRandom;
    private Entity rider;

    public FireworkBuilder() {
        this.itemFirework = new ItemFireworkRocket();
        this.fireworkTag = new CompoundTag();
        this.explosions = new ArrayList<CompoundTag>();
        this.fireworkRandom = new Random();
    }

    public FireworkBuilder addExplosion(CompoundTag explosion) {
        this.explosions.add(explosion);
        return this;
    }

    public FireworkBuilder addExplosion(ExplosionBuilder explosionBuilder) {
        this.explosions.add(explosionBuilder.build());
        return this;
    }

    /**
     * @param flight (0-1)
     * @return FireworkBuilder
     */
    public FireworkBuilder setFlight(int flight) {
        this.fireworkTag.putByte("Flight", (byte) flight);
        return this;
    }
    
    public FireworkBuilder setRider(Entity rider) {
    	this.rider = rider;
		return this;
    }

    /**
     * Generates a random firework with 1 explosion and random flicker and trail.
     *
     * @return FireworkBuilder
     */
    public FireworkBuilder random() {
        return this.random(1, null, null);
    }

    /**
     * Returns a random firework with X explosions and optional flicker and trail set as parameter.
     *
     * @param numberOfExplosions
     * @param flicker
     * @param trail
     * @return FireworkBuilder
     */
    public FireworkBuilder random(int numberOfExplosions, Boolean flicker, Boolean trail) {
        DyeColor[] allColors = DyeColor.values();
        ExplosionType[] types = ExplosionType.values();

        for (int i = 0; i < numberOfExplosions; i++) {
            DyeColor[] randomColors = new DyeColor[fireworkRandom.nextInt(allColors.length) + 1];
            for (int j = 0; j < randomColors.length; j++) {
                randomColors[j] = allColors[fireworkRandom.nextInt(allColors.length)];
            }

            DyeColor[] randomFadeColors = new DyeColor[fireworkRandom.nextInt(allColors.length) + 1];
            for (int j = 0; j < randomFadeColors.length; j++) {
                randomFadeColors[j] = allColors[fireworkRandom.nextInt(allColors.length)];
            }

            ExplosionType randomType = types[fireworkRandom.nextInt(types.length)];

            ExplosionBuilder explosionBuilder = new ExplosionBuilder()
                    .setColors(randomColors)
                    .setFadeColors(randomFadeColors)
                    .setType(randomType);

            explosionBuilder.setFlicker(Objects.requireNonNullElseGet(flicker, fireworkRandom::nextBoolean));

            explosionBuilder.setTrail(Objects.requireNonNullElseGet(trail, fireworkRandom::nextBoolean));
            
            this.addExplosion(explosionBuilder);
        }

        this.setFlight(1);

        return this;
    }

    /**
     * Builds the Firework ({@link EntityFireworksRocket}) for a given position and chunk.
     * <p>
     * If a rider ({@link Entity}) was provided upon creation of the builder it is mounted here onto the firework.
     *
     * @param pos
     * @param chunk
     * @return EntityFireworksRocket
     */
    public EntityFireworksRocket build(Position pos, IChunk chunk) {
        ListTag<CompoundTag> explosions = new ListTag<CompoundTag>();
        this.explosions.forEach(explosions::add);
        this.fireworkTag.putList("Explosions", explosions);
        CompoundTag fireworks = new CompoundTag().putCompound("Fireworks", this.fireworkTag);
        this.itemFirework.setNamedTag(fireworks);

        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(pos.x))
                        .add(new DoubleTag(pos.y + 0.5))
                        .add(new DoubleTag(pos.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(0))
                        .add(new FloatTag(0)))
                .putBoolean("Riding", false)
                .putCompound("FireworkItem", NBTIO.putItemHelper(this.itemFirework));
        EntityFireworksRocket eFirework = new EntityFireworksRocket(chunk, nbt);
        if(this.rider != null) eFirework.mountEntity(this.rider);
        eFirework.setDataProperty(Entity.DISPLAY_TILE_RUNTIME_ID, this.itemFirework.getNamedTag());
        return eFirework;
    }
}
