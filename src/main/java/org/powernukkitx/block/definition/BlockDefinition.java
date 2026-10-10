package org.powernukkitx.block.definition;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
public class BlockDefinition {
    boolean canHarvestWithHand;
    boolean canSilkTouch;
    boolean isSoulSpeedCompatible;
    boolean canBePlaced;
    boolean canBeReplaced;
    boolean isTransparent;
    boolean isSolid;
    boolean diffusesSkyLight;
    boolean canBeFlowedInto;
    boolean canBeActivated;
    boolean hasEntityCollision;
    boolean hasEntityStepSensor;
    boolean canPassThrough;
    boolean canBePushed;
    boolean canBePulled;
    boolean breaksWhenMoved;
    boolean sticksToPiston;
    boolean canStickBlocks;
    boolean hasComparatorInputOverride;
    boolean canBeClimbed;
    boolean isPowerSource;
    boolean isFertilizable;
    boolean lavaResistant;

    int burnChance;
    int burnAbility;
    int tickRate;
    int toolType;
    int walkThroughExtraCost;
    /**
     * Amount of light this block absorbs (0-15). A negative value means "unset": the filter is then derived
     * from the block's solidity, see {@code Block#getLightFilter()}. Zero is a valid explicit value.
     */
    int lightDampening;
    int lightEmission;
    int toolTier;
    int dropExp;
    int maxStackSize;
    int waterloggingLevel;

    double hardness;
    double resistance;
    double friction;
    double passableFrictionFactor;
}
