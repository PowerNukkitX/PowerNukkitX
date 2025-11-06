package cn.nukkit.level.generator.noise.minecraft.noise;

import cn.nukkit.utils.random.RandomSourceProvider;

/**
 * @author SeedFinding Project
 */
public class Noise {

    public final double originX;
    public final double originY;
    public final double originZ;
    protected final byte[] permutations = new byte[256];

    public Noise(RandomSourceProvider rand) {
        this.originX = rand.nextDouble() * 256.0D;
        this.originY = rand.nextDouble() * 256.0D;
        this.originZ = rand.nextDouble() * 256.0D;

        for(int j = 0; j < 256; ++j) {
            this.permutations[j] = (byte)j;
        }

        for(int index = 0; index < 256; ++index) {
            int randomIndex = rand.nextBoundedInt(255 - index) + index;
            byte temp = this.permutations[index];
            this.permutations[index] = this.permutations[randomIndex];
            this.permutations[randomIndex] = temp;
        }
    }

    protected int lookup(int hash) {
        return this.permutations[hash & 0xFF] & 0xFF;
    }
}
