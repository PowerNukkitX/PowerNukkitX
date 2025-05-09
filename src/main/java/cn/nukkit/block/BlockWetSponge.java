package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.CloudParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockWetSponge extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(WET_SPONGE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWetSponge() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWetSponge(BlockState state) {
        super(state);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
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
        this.getLevel().addLevelEvent(block.add(0.5, 0.875, 0.5), LevelEventPacket.EVENT_CAULDRON_EXPLODE);
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
