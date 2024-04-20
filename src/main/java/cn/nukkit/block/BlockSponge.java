package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.SpongeType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.CloudParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.SPONGE_TYPE;
import static cn.nukkit.block.property.enums.SpongeType.DRY;
import static cn.nukkit.block.property.enums.SpongeType.WET;

public class BlockSponge extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(SPONGE,
            SPONGE_TYPE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSponge() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSponge(BlockState state) {
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

    public SpongeType getSpongeType() {
        return this.getPropertyValue(SPONGE_TYPE);
    }

    public void setSpongeType(SpongeType type) {
        this.setPropertyValue(SPONGE_TYPE, type);
    }

    @Override
    public String getName() {
        return switch (getSpongeType()) {
            case DRY -> "Sponge";
            case WET -> "Wet Sponge";
        };
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        BlockSponge clone = (BlockSponge) this.clone();
        if (this.getSpongeType() == WET && level.getDimension() == Level.DIMENSION_NETHER) {
            clone.setSpongeType(DRY);
            level.setBlock(block, clone, true, true);
            this.getLevel().addLevelEvent(block.add(0.5, 0.875, 0.5), LevelEventPacket.EVENT_CAULDRON_EXPLODE);

            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < 8; ++i) {
                level.addParticle(new CloudParticle(block.getLocation().add(random.nextDouble(), 1, random.nextDouble())));
            }

            return true;
        }
        if (this.getSpongeType() == DRY && (block instanceof BlockFlowingWater || block.getLevelBlockAround().stream().anyMatch(b -> b instanceof BlockFlowingWater)) &&
                performWaterAbsorb(block)
        ) {
            clone.setSpongeType(WET);
            level.setBlock(block, clone, true, true);

            for (int i = 0; i < 4; i++) {
                LevelEventPacket packet = new LevelEventPacket();
                packet.evid = LevelEventPacket.EVENT_PARTICLE_DESTROY_BLOCK;
                packet.x = (float) block.getX() + 0.5f;
                packet.y = (float) block.getY() + 1f;
                packet.z = (float) block.getZ() + 0.5f;
                packet.data = Block.get(BlockID.FLOWING_WATER).blockstate.blockStateHash();
                level.addChunkPacket(getChunkX(), getChunkZ(), packet);
            }

            return true;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, getSpongeType().ordinal());
    }

    private boolean performWaterAbsorb(Block block) {
        Queue<Entry> entries = new ArrayDeque<>();

        entries.add(new Entry(block, 0));

        Entry entry;
        int waterRemoved = 0;
        while (waterRemoved < 64 && (entry = entries.poll()) != null) {
            for (BlockFace face : BlockFace.values()) {
                Block layer0 = entry.block.getSideAtLayer(0, face);
                Block layer1 = layer0.getLevelBlockAtLayer(1);

                if (layer0 instanceof BlockFlowingWater) {
                    this.getLevel().setBlockStateAt(layer0.getFloorX(), layer0.getFloorY(), layer0.getFloorZ(), BlockAir.PROPERTIES.getDefaultState());
                    this.getLevel().updateAround(layer0);
                    waterRemoved++;
                    if (entry.distance < 6) {
                        entries.add(new Entry(layer0, entry.distance + 1));
                    }
                } else if (layer1 instanceof BlockFlowingWater) {
                    if (BlockID.KELP.equals(layer0.getId()) ||
                            BlockID.SEAGRASS.equals(layer0.getId()) ||
                            BlockID.SEA_PICKLE.equals(layer0.getId()) || layer0 instanceof BlockCoralFan) {
                        layer0.getLevel().useBreakOn(layer0);
                    }
                    this.getLevel().setBlockStateAt(layer1.getFloorX(), layer1.getFloorY(), layer1.getFloorZ(), 1, BlockAir.PROPERTIES.getDefaultState());
                    this.getLevel().updateAround(layer1);
                    waterRemoved++;
                    if (entry.distance < 6) {
                        entries.add(new Entry(layer1, entry.distance + 1));
                    }
                }
            }
        }

        return waterRemoved > 0;
    }

    private record Entry(Block block, int distance) {
    }
}
