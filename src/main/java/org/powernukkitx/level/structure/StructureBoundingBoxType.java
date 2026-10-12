package org.powernukkitx.level.structure;

/**
 * Bounding-box extent selected by a structure spawn override.
 *
 * @author Curse
 */
public enum StructureBoundingBoxType {
    STRUCTURE,
    PIECE;

    /**
     * Resolves the persisted FullStructureBoundingBox marker.
     */
    public static StructureBoundingBoxType fromFullBoundingBox(boolean fullBoundingBox) {
        return fullBoundingBox ? STRUCTURE : PIECE;
    }
}
