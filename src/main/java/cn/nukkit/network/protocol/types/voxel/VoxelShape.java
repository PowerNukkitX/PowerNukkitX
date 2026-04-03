package cn.nukkit.network.protocol.types.voxel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @since v924
 */
@Getter
@AllArgsConstructor
@ToString
public class VoxelShape {
    private final VoxelCells cells;
    private final List<Float> xCoordinates;
    private final List<Float> yCoordinates;
    private final List<Float> zCoordinates;
}
