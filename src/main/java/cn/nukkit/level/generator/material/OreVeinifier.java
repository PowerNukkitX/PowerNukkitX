package cn.nukkit.level.generator.material;

import cn.nukkit.block.BlockCopperOre;
import cn.nukkit.block.BlockDeepslateIronOre;
import cn.nukkit.block.BlockGranite;
import cn.nukkit.block.BlockRawCopperBlock;
import cn.nukkit.block.BlockRawIronBlock;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockTuff;
import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;

import javax.annotation.Nullable;

public final class OreVeinifier {
    private static final float VEININESS_THRESHOLD = 0.4F;
    private static final int EDGE_ROUNDOFF_BEGIN = 20;
    private static final double MAX_EDGE_ROUNDOFF = 0.2;
    private static final float VEIN_SOLIDNESS = 0.7F;
    private static final float MIN_RICHNESS = 0.1F;
    private static final float MAX_RICHNESS = 0.3F;
    private static final float MAX_RICHNESS_THRESHOLD = 0.6F;
    private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02F;
    private static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3F;

    private final DensityFunction veinToggle;
    private final DensityFunction veinRidged;
    private final DensityFunction veinGap;
    private final long oreVeinSeed;
    private final ThreadLocal<NukkitRandom> random;

    public OreVeinifier(
            DensityFunction veinToggle,
            DensityFunction veinRidged,
            DensityFunction veinGap,
            long oreVeinSeed
    ) {
        this.veinToggle = veinToggle;
        this.veinRidged = veinRidged;
        this.veinGap = veinGap;
        this.oreVeinSeed = oreVeinSeed;
        this.random = ThreadLocal.withInitial(() -> new NukkitRandom(oreVeinSeed));
    }

    public @Nullable BlockState calculate(DensityFunction.FunctionContext context) {
        double oreVeininessNoiseValue = this.veinToggle.compute(context);
        int y = context.blockY();
        VeinType veinType = oreVeininessNoiseValue > 0.0 ? VeinType.COPPER : VeinType.IRON;
        double veininessRidged = Math.abs(oreVeininessNoiseValue);
        int distanceFromTop = veinType.maxY - y;
        int distanceFromBottom = y - veinType.minY;
        if (distanceFromBottom < 0 || distanceFromTop < 0) {
            return null;
        }

        int distanceFromEdge = Math.min(distanceFromTop, distanceFromBottom);
        double edgeRoundoff = clampedMap(distanceFromEdge, 0.0, EDGE_ROUNDOFF_BEGIN, -MAX_EDGE_ROUNDOFF, 0.0);
        if (veininessRidged + edgeRoundoff < VEININESS_THRESHOLD) {
            return null;
        }

        NukkitRandom positionalRandom = this.random.get();
        positionalRandom.setSeed(mixSeed(this.oreVeinSeed, context.blockX(), y, context.blockZ()));
        if (positionalRandom.nextFloat() > VEIN_SOLIDNESS) {
            return null;
        }
        if (this.veinRidged.compute(context) >= 0.0) {
            return null;
        }

        double richness = clampedMap(
                veininessRidged,
                VEININESS_THRESHOLD,
                MAX_RICHNESS_THRESHOLD,
                MIN_RICHNESS,
                MAX_RICHNESS
        );
        if (positionalRandom.nextFloat() < richness && this.veinGap.compute(context) > SKIP_ORE_IF_GAP_NOISE_IS_BELOW) {
            return positionalRandom.nextFloat() < CHANCE_OF_RAW_ORE_BLOCK ? veinType.rawOreBlock : veinType.ore;
        }
        return veinType.filler;
    }

    private static double clampedMap(double value, double inMin, double inMax, double outMin, double outMax) {
        double t = (value - inMin) / (inMax - inMin);
        t = NukkitMath.clamp(t, 0.0, 1.0);
        return outMin + (outMax - outMin) * t;
    }

    private static long mixSeed(long seed, int x, int y, int z) {
        long mixed = seed;
        mixed ^= (long) x * 341873128712L;
        mixed ^= (long) y * 132897987541L;
        mixed ^= (long) z * 42317861L;
        mixed ^= (mixed >>> 33);
        mixed *= 0xff51afd7ed558ccdL;
        mixed ^= (mixed >>> 33);
        mixed *= 0xc4ceb9fe1a85ec53L;
        mixed ^= (mixed >>> 33);
        return mixed;
    }

    private enum VeinType {
        COPPER(
                BlockCopperOre.PROPERTIES.getDefaultState(),
                BlockRawCopperBlock.PROPERTIES.getDefaultState(),
                BlockGranite.PROPERTIES.getDefaultState(),
                0,
                50
        ),
        IRON(
                BlockDeepslateIronOre.PROPERTIES.getDefaultState(),
                BlockRawIronBlock.PROPERTIES.getDefaultState(),
                BlockTuff.PROPERTIES.getDefaultState(),
                -60,
                -8
        );

        private final BlockState ore;
        private final BlockState rawOreBlock;
        private final BlockState filler;
        private final int minY;
        private final int maxY;

        VeinType(BlockState ore, BlockState rawOreBlock, BlockState filler, int minY, int maxY) {
            this.ore = ore;
            this.rawOreBlock = rawOreBlock;
            this.filler = filler;
            this.minY = minY;
            this.maxY = maxY;
        }
    }
}
