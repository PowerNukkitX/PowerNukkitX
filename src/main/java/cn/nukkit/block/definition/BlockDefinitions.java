package cn.nukkit.block.definition;

import cn.nukkit.block.Block;
import cn.nukkit.item.ItemTool;

public interface BlockDefinitions {
    BlockDefinition DEFAULT = BlockDefinition.builder()
            .breaksWhenMoved(false)
            .burnAbility(0)
            .burnChance(0)
            .canBeActivated(false)
            .canBeClimbed(false)
            .canBeFlowedInto(false)
            .canBePlaced(true)
            .canBePulled(true)
            .canBePushed(true)
            .canBeReplaced(false)
            .canHarvestWithHand(true)
            .canStickBlocks(false)
            .canPassThrough(false)
            .canSilkTouch(false)
            .diffusesSkyLight(false)
            .dropExp(0)
            .frictionFactor(Block.DEFAULT_FRICTION_FACTOR)
            .hardness(10)
            .hasComparatorInputOverride(false)
            .hasEntityCollision(false)
            .hasEntityStepSensor(false)
            .isFertilizable(false)
            .isPowerSource(false)
            .lightLevel(0)
            .passableFrictionFactor(Block.DEFAULT_AIR_FLUID_FRICTION)
            .isSolid(true)
            .isSoulSpeedCompatible(false)
            .isTransparent(false)
            .lavaResistant(false)
            .resistance(1)
            .maxStackSize(64)
            .sticksToPiston(true)
            .tickRate(10)
            .toolTier(0) //No tier required
            .toolType(ItemTool.TYPE_NONE)
            .walkThroughExtraCost(0)
            .build();

    BlockDefinition TRANSPARENT = DEFAULT.toBuilder()
            .isTransparent(true)
            .build();

    BlockDefinition FLOWABLE = TRANSPARENT.toBuilder()
            .canBeFlowedInto(true)
            .canPassThrough(true)
            .hardness(0)
            .resistance(0)
            .isSolid(false)
            .breaksWhenMoved(true)
            .sticksToPiston(false)
            .build();

    BlockDefinition BUTTON = FLOWABLE.toBuilder()
            .resistance(2.5)
            .hardness(0.5)
            .canBeFlowedInto(false)
            .canBeActivated(true)
            .isPowerSource(true)
            .build();

    BlockDefinition WOODEN_BUTTON = BUTTON.toBuilder()
            .toolType(ItemTool.TYPE_AXE)
            .build();
}
