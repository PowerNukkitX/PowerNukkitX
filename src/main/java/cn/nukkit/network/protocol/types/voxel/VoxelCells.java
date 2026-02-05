package cn.nukkit.network.protocol.types.voxel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @since v924
 */
@AllArgsConstructor
@Getter
public class VoxelCells {
    private final short xSize;
    private final short ySize;
    private final short zSize;
    private final List<Short> storage;
}
