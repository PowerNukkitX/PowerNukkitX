package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.particle.CloudParticle;
import org.powernukkitx.math.BlockFace;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockWetSponge extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(WET_SPONGE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.6)
            .resistance(3)
            .toolType(ItemTool.TYPE_HOE)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWetSponge() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWetSponge(BlockState state) {
        super(state, DEFINITION);
    }

    @Override
    public String getName() {
        return "Wet Sponge";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (level.getDimension() != Level.DIMENSION_NETHER) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }


        level.setBlock(block, new BlockSponge(), true, true);
        this.getLevel().addLevelEvent(block.add(0.5, 0.875, 0.5), LevelEvent.CAULDRON_EXPLODE);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 8; ++i) {
            level.addParticle(new CloudParticle(block.getLocation().add(random.nextDouble(), 1, random.nextDouble())));
        }

        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockWetSponge());
    }

}
