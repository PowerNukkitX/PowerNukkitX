package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockFire;
import org.powernukkitx.block.BlockIronBars;
import org.powernukkitx.block.BlockObsidian;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityEnderCrystal;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.random.RandomSourceProvider;
import java.util.Random;

import static org.powernukkitx.block.property.CommonBlockProperties.INFINIBURN_BIT;

public class ObjectObsidianPillar extends ObjectGenerator {
    private final int index;
    private final int pillar;
    private final int radius;
    private final int height;
    private final boolean guarded;

    public ObjectObsidianPillar() {
        this(0, 0);
    }

    private ObjectObsidianPillar(int index, int pillar) {
        this.index = index;
        this.pillar = pillar;
        this.radius = 2 + pillar / 3;
        this.height = 76 + pillar * 3;
        this.guarded = pillar == 1 || pillar == 2;
    }

    /**
     * Creates an End pillar configuration from the supplied seed and spike index.
     *
     * @param seed pillar permutation seed
     * @param index End spike index
     * @return resolved pillar configuration
     */
    public static ObjectObsidianPillar fromSeed(long seed, int index) {
        if (index < 0 || index >= 10) {
            throw new IllegalArgumentException("Invalid End spike index " + index);
        }

        int[] pillars = new int[10];
        for (int i = 0; i < pillars.length; i++) {
            pillars[i] = i;
        }

        BedrockMersenneTwister random = new BedrockMersenneTwister((int) seed);
        for (int i = 1; i < pillars.length; i++) {
            int j = Integer.remainderUnsigned(random.nextInt(), i + 1);
            int value = pillars[i];
            pillars[i] = pillars[j];
            pillars[j] = value;
        }
        return new ObjectObsidianPillar(index, pillars[index]);
    }

    /**
     * Returns the X offset of this End spike from the island center.
     *
     * @return spike X offset
     */
    public int getX() {
        return (int) (42d * Math.cos(2d * (index * Math.PI / 10d - Math.PI)));
    }

    /**
     * Returns the Z offset of this End spike from the island center.
     *
     * @return spike Z offset
     */
    public int getZ() {
        return (int) (42d * Math.sin(2d * (index * Math.PI / 10d - Math.PI)));
    }

    public int getPillar() {
        return pillar;
    }

    public int getRadius() {
        return radius;
    }

    public int getHeight() {
        return height;
    }

    public boolean isGuarded() {
        return guarded;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        return generate(level, rand, position, null);
    }

    /**
     * Generates this End spike and optionally assigns its crystal beam target.
     *
     * @param level block manager
     * @param rand random source
     * @param position spike origin
     * @param beamTarget End crystal beam target, or {@code null} for none
     * @return whether generation succeeded
     */
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position, BlockVector3 beamTarget) {
        int x = position.getFloorX();
        int z = position.getFloorZ();
        int height = getHeight();
        int radius = getRadius();

        for (int i = 0; i < height; i++) {
            for (int j = -radius; j <= radius; j++) {
                for (int k = -radius; k <= radius; k++) {
                    if (j * j + k * k <= radius * radius + 1) {
                        level.setBlockStateAt(x + j, i, z + k, BlockObsidian.PROPERTIES.getDefaultState());
                    }
                }
            }
        }

        if (this.isGuarded()) {
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    if (Math.abs(i) == 2 || Math.abs(j) == 2) {
                        for (int k = 0; k < 3; ++k) {
                            level.setBlockStateAt(x + i, height + k, z + j, BlockIronBars.PROPERTIES.getDefaultState());
                        }
                    }
                    level.setBlockStateAt(x + i, height + 3, z + j, BlockIronBars.PROPERTIES.getDefaultState());
                }
            }
        }

        level.setBlockStateAt(x, height, z, BlockBedrock.PROPERTIES.getBlockState(INFINIBURN_BIT.createValue(true)));
        level.setBlockStateAt(x, height + 1, z, BlockFire.PROPERTIES.getDefaultState());
        level.addHook(() -> {
            CompoundTag nbt = new CompoundTag()
                    .putList("Pos", new ListTag<FloatTag>()
                            .add(new FloatTag(x + 0.5))
                            .add(new FloatTag(height + 1))
                            .add(new FloatTag(z + 0.5)))
                    .putList("Motion", new ListTag<FloatTag>()
                            .add(new FloatTag(0))
                            .add(new FloatTag(0))
                            .add(new FloatTag(0)))
                    .putList("Rotation", new ListTag<FloatTag>()
                            .add(new FloatTag(new Random().nextFloat() * 360))
                            .add(new FloatTag(0)));

            Entity entity = Entity.createEntity(Entity.ENDER_CRYSTAL, level.getChunk(position.getChunkX(), position.getChunkZ()), nbt);
            if (entity instanceof EntityEnderCrystal crystal && beamTarget != null) {
                crystal.setInvulnerable(true);
                crystal.setBeamTarget(beamTarget);
            }
            entity.spawnToAll();
        });
        return true;
    }

    private static final class BedrockMersenneTwister {

        private final int[] state = new int[624];
        private int index = 624;

        private BedrockMersenneTwister(int seed) {
            state[0] = seed;
            for (int i = 1; i < state.length; i++) {
                int previous = state[i - 1];
                state[i] = 0x6c078965 * (previous ^ (previous >>> 30)) + i;
            }
        }

        private int nextInt() {
            if (index >= state.length) {
                twist();
            }

            int value = state[index++];
            value ^= value >>> 11;
            value ^= (value << 7) & 0x9d2c5680;
            value ^= (value << 15) & 0xefc60000;
            value ^= value >>> 18;
            return value;
        }

        private void twist() {
            for (int i = 0; i < state.length; i++) {
                int value = (state[i] & 0x80000000) | (state[(i + 1) % state.length] & 0x7fffffff);
                state[i] = state[(i + 397) % state.length] ^ (value >>> 1);
                if ((value & 1) != 0) {
                    state[i] ^= 0x9908b0df;
                }
            }
            index = 0;
        }
    }
}
