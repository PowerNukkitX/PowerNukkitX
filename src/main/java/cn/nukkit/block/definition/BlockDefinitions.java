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

    BlockDefinition DOOR = TRANSPARENT.toBuilder()
            .isSolid(false)
            .canBeActivated(false)
            .breaksWhenMoved(true)
            .sticksToPiston(false)
            .build();

    BlockDefinition WOODEN_DOOR = DOOR.toBuilder()
            .hardness(3)
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    BlockDefinition IRON_DOOR = DOOR.toBuilder()
            .hardness(5)
            .resistance(25)
            .toolType(ItemTool.TYPE_PICKAXE)
            .canHarvestWithHand(false)
            .build();

    BlockDefinition COPPER_DOOR = DOOR.toBuilder()
            .hardness(3)
            .resistance(3)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .build();

    BlockDefinition FENCE = TRANSPARENT.toBuilder()
            .hardness(2)
            .resistance(3)
            .toolType(ItemTool.TYPE_AXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .burnChance(5)
            .burnAbility(20)
            .build();

    BlockDefinition NON_FLAMMABLE_FENCE = FENCE.toBuilder()
            .burnChance(0)
            .burnAbility(0)
            .build();

    BlockDefinition NETHER_BRICK_FENCE = NON_FLAMMABLE_FENCE.toBuilder()
            .hardness(2)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .canHarvestWithHand(false)
            .build();

    BlockDefinition FENCE_GATE = TRANSPARENT.toBuilder()
            .hardness(2)
            .resistance(15)
            .canBeActivated(true)
            .toolType(ItemTool.TYPE_AXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .burnChance(5)
            .burnAbility(20)
            .build();

    BlockDefinition NON_FLAMMABLE_FENCE_GATE = FENCE_GATE.toBuilder()
            .burnChance(0)
            .burnAbility(0)
            .build();

    BlockDefinition SIGN = TRANSPARENT.toBuilder()
            .hardness(1)
            .resistance(5)
            .isSolid(false)
            .toolType(ItemTool.TYPE_AXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .breaksWhenMoved(true)
            .canBeActivated(true)
            .build();

    BlockDefinition LEAVES = TRANSPARENT.toBuilder()
            .hardness(0.2)
            .toolType(ItemTool.TYPE_HOE)
            .toolTier(ItemTool.TIER_WOODEN)
            .burnChance(30)
            .burnAbility(60)
            .canSilkTouch(true)
            .diffusesSkyLight(true)
            .breaksWhenMoved(true)
            .sticksToPiston(false)
            .build();
}
