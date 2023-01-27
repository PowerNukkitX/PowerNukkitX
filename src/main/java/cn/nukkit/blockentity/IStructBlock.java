package cn.nukkit.blockentity;

import cn.nukkit.inventory.InventoryHolder;

public interface IStructBlock extends InventoryHolder {
    String TAG_CUSTOM_NAME = "CustomName";
    String TAG_ANIMATION_MODE = "animationMode";
    String TAG_ANIMATION_SECONDS = "animationSeconds";
    String TAG_DATA = "data";
    String TAG_DATA_FIELD = "dataField";
    String TAG_IGNORE_ENTITIES = "ignoreEntities";
    String TAG_INCLUDE_PLAYERS = "includePlayers";
    String TAG_INTEGRITY = "integrity";
    String TAG_MIRROR = "mirror";
    String TAG_IS_POWERED = "isPowered";
    String TAG_REDSTONE_SAVEMODE = "redstoneSaveMode";
    String TAG_REMOVE_BLOCKS = "removeBlocks";
    String TAG_ROTATION = "rotation";
    String TAG_SEED = "seed";
    String TAG_SHOW_BOUNDING_BOX = "showBoundingBox";
    String TAG_STRUCTURE_NAME = "structureName";
    String TAG_X_STRUCTURE_OFFSET = "xStructureOffset";
    String TAG_Y_STRUCTURE_OFFSET = "yStructureOffset";
    String TAG_Z_STRUCTURE_OFFSET = "zStructureOffset";
    String TAG_X_STRUCTURE_SIZE = "xStructureSize";
    String TAG_Y_STRUCTURE_SIZE = "yStructureSize";
    String TAG_Z_STRUCTURE_SIZE = "zStructureSize";
}
