package cn.nukkit.network.protocol.types.waypoint;

import lombok.Data;

import java.util.UUID;

@Data
public class LocatorBarWaypointPayload implements WaypointPayload {
    private UUID groupHandle;
    private ServerWaypointPayload serverWaypointPayload;
    private ServerWaypointPayload.Action actionFlag;
}
