package cn.nukkit.utils.firework;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFireworksRocket;
import cn.nukkit.item.ItemFireworkRocket;
import cn.nukkit.item.ItemFireworkRocket.FireworkExplosion.ExplosionType;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;

import java.util.ArrayList;
import java.util.Arrays;
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
    private NbtMap fireworkTag;
    private List<NbtMap> explosions;
    private final Random fireworkRandom;
    private Entity rider;

    public FireworkBuilder() {
        this.itemFirework = new ItemFireworkRocket();
        this.fireworkTag = NbtMap.EMPTY;
        this.explosions = new ArrayList<>();
        this.fireworkRandom = new Random();
    }

    public FireworkBuilder addExplosion(NbtMap explosion) {
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
        this.fireworkTag = this.fireworkTag.toBuilder().putByte("Flight", (byte) flight).build();
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
        List<NbtMap> explosions = new ObjectArrayList<>();
        this.explosions.forEach(explosions::add);
        this.fireworkTag = this.fireworkTag.toBuilder().putList("Explosions", NbtType.COMPOUND, explosions).build();
        NbtMap fireworks = NbtMap.builder().putCompound("Fireworks", this.fireworkTag).build();
        this.itemFirework.setNamedTag(fireworks);

        final NbtMap nbt = NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(pos.x, pos.y + 0.5, pos.z))
                .putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0))
                .putList("Rotation", NbtType.FLOAT, Arrays.asList(0f, 0f))
                .putBoolean("Riding", false)
                .putCompound("FireworkItem", ItemHelper.write(this.itemFirework))
                .build();
        EntityFireworksRocket eFirework = new EntityFireworksRocket(chunk, nbt);
        if (this.rider != null) eFirework.mountEntity(this.rider, false);
        eFirework.setDataProperty(ActorDataTypes.DISPLAY_TILE_RUNTIME_ID, this.itemFirework.getNamedTag());
        return eFirework;
    }
}
