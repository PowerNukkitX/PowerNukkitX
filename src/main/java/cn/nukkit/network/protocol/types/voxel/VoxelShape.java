package cn.nukkit.network.protocol.types.voxel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @since v924
 */
@Getter
@AllArgsConstructor
public class VoxelShape {
    private final List<VoxelCells> cells;
    private final List<Float> xCoordinates;
    private final List<Float> yCoordinates;
    private final List<Float> zCoordinates;
}
