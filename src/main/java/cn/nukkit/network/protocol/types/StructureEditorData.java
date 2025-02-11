package cn.nukkit.network.protocol.types;

import cn.nukkit.block.property.enums.StructureBlockType;
import lombok.Value;

@Value
public class StructureEditorData {
    String name;
    String filteredName;
    String dataField;
    boolean includingPlayers;
    boolean boundingBoxVisible;
    StructureBlockType type;
    StructureSettings settings;
    StructureRedstoneSaveMode redstoneSaveMode;
}
