package cn.nukkit.network.protocol.types;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@AllArgsConstructor
@ToString
public class StructureSettings {
    String paletteName;
    boolean ignoringEntities;
    boolean ignoringBlocks;
    boolean nonTickingPlayersAndTickingAreasEnabled;
    BlockVector3 size;
    BlockVector3 offset;
    long lastEditedByEntityId;
    StructureRotation rotation;
    StructureMirror mirror;
    StructureAnimationMode animationMode;
    float animationSeconds;
    float integrityValue;
    int integritySeed;
    Vector3f pivot;
}
