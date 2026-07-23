package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.PILLAR_AXIS;


public abstract class BlockLog extends BlockSolid implements IBlockWood {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(2)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .burnAbility(10)
            .canBeActivated(true)
            .build();
    public BlockLog(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockLog(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    public abstract BlockState getStrippedState();

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(PILLAR_AXIS, axis);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setPillarAxis(face.getAxis());
        getLevel().setBlock(block, this, true, true);
        return true;
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isAxe()) {
            Block strippedBlock = Block.get(getStrippedState());
            strippedBlock.setPropertyValue(PILLAR_AXIS, this.getPillarAxis());
            if (player == null || !player.isCreative()) {
                item.useOn(this);
            }
            this.level.setBlock(this, strippedBlock, true, true);
            this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), SoundEvent.ITEM_USE_ON, strippedBlock.getBlockState().blockStateHash());
            return true;
        }
        return false;
    }

    }
