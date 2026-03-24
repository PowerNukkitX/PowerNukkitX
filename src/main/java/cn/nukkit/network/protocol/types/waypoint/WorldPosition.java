package cn.nukkit.network.protocol.types.waypoint;

import cn.nukkit.math.Vector3f;
import lombok.Value;

@Value
public class WorldPosition {

    Vector3f position;
    int dimensionId;
}