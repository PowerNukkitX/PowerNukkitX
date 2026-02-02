package cn.nukkit.block.definition;

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
