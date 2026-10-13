package org.powernukkitx.level.generator.stages.nether;

import org.powernukkitx.block.BlockLava;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.GenerationLiquidUpdateAccess;
import org.powernukkitx.level.generator.populator.generic.PopulatorRuinedPortal;
import org.powernukkitx.level.generator.populator.nether.*;
import org.powernukkitx.level.generator.populator.nether.basalt_delta.BasaltDeltaLavaPopulator;
import org.powernukkitx.level.generator.populator.nether.basalt_delta.BasaltDeltaMagmaPopulator;
import org.powernukkitx.level.generator.populator.nether.basalt_delta.BasaltDeltaPillarPopulator;
import org.powernukkitx.level.generator.populator.nether.crimson.CrimsonFungiTreePopulator;
import org.powernukkitx.level.generator.populator.nether.crimson.CrimsonGrassesPopulator;
import org.powernukkitx.level.generator.populator.nether.crimson.CrimsonWeepingVinesPopulator;
import org.powernukkitx.level.generator.populator.nether.soulsand_valley.NetherFossilPopulator;
import org.powernukkitx.level.generator.populator.nether.warped.WarpedFungiTreePopulator;
import org.powernukkitx.level.generator.populator.nether.warped.WarpedGrassesPopulator;
import org.powernukkitx.level.generator.populator.nether.warped.WarpedTwistingVinesPopulator;
import org.powernukkitx.level.generator.stages.PopulatorStage;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.powernukkitx.utils.random.BedrockRandom;

public class NetherPopulatorStage extends PopulatorStage {

    public static final String NAME = "nether_populator";
    private static final int OPEN_SPRING_ATTEMPTS = 8;
    private static final BlockState LAVA = BlockLava.PROPERTIES.getDefaultState();

    public static final ObjectArraySet<String> POPULATORS = new ObjectArraySet<>(new String[] {
            GlowstonePopulator.NAME,
            SoulsandPopulator.NAME,
            MagmaPopulator.NAME,
            LavaOrePopulator.NAME,
            FirePopulator.NAME,
            LavaPopulator.NAME,
            NetherGoldOrePopulator.NAME,
            AncientDebrisSmallPopulator.NAME,
            AncientDebrisLargePopulator.NAME,
            NetherQuartzPopulator.NAME,
            BasaltDeltaLavaPopulator.NAME,
            BasaltDeltaPillarPopulator.NAME,
            BasaltDeltaMagmaPopulator.NAME,
            CrimsonFungiTreePopulator.NAME,
            CrimsonGrassesPopulator.NAME,
            CrimsonWeepingVinesPopulator.NAME,
            WarpedFungiTreePopulator.NAME,
            WarpedGrassesPopulator.NAME,
            WarpedTwistingVinesPopulator.NAME,
            NetherBlackstonePopulator.NAME,
            NetherGravelPopulator.NAME,
            BastionRemnantPopulator.NAME,
            NetherFortressPopulator.NAME,
            PopulatorRuinedPortal.NAME,
            NetherFossilPopulator.NAME
    });

    @Override
    protected void beforeApplyBlocks(ChunkGenerateContext context, BlockManager root) {
        IChunk chunk = context.getChunk();
        var updates = chunk.getGenerationBlockUpdateQueue();
        BedrockRandom random = createDecorationRandom(context.getLevel(), chunk);

        int baseX = (chunk.getX() << 4) + 8;
        int baseZ = (chunk.getZ() << 4) + 8;
        for (int i = 0; i < OPEN_SPRING_ATTEMPTS; i++) {
            int x = baseX + random.nextInt(16);
            int y = 4 + random.nextInt(120);
            int z = baseZ + random.nextInt(16);
            updates.add(LAVA, x, y, z, 0L, 0);
        }

        if (context.getLevel().getGameplaySettings().enableLiquidFlow()) {
            updates.settleLiquids(new GenerationLiquidUpdateAccess(root, updates));
        }
    }

    @Override
    protected void afterApplyBlocks(ChunkGenerateContext context, BlockManager root) {
        context.getChunk().getGenerationBlockUpdateQueue().drainToRuntime(context.getLevel());
    }

    private static BedrockRandom createDecorationRandom(Level level, IChunk chunk) {
        int worldSeed = (int) level.getSeed();
        BedrockRandom random = new BedrockRandom(worldSeed);

        int first = random.nextInt();
        int second = random.nextInt();
        int oddX = (first + (first >>> 31)) | 1;
        int oddZ = (second + (second >>> 31)) | 1;
        int decorationSeed = chunk.getX() * oddX + chunk.getZ() * oddZ ^ worldSeed;

        return random.setSeed(decorationSeed);
    }

    @Override
    public ObjectArraySet<String> populators() {
        return POPULATORS;
    }

    @Override
    public String name() {
        return NAME;
    }
}
