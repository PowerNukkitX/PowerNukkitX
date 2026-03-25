package cn.nukkit.network.protocol.types.waypoint;

import cn.nukkit.utils.OptionalBoolean;
import lombok.Data;

@Data
public class ServerWaypointPayload implements WaypointPayload {
    private int updateFlag;
    private OptionalBoolean isVisible = OptionalBoolean.empty();
    private WorldPosition worldPosition;
    private Integer textureId;
    private Integer color;
    private OptionalBoolean clientPositionAuthority = OptionalBoolean.empty();
    private Long actorUniqueID;

    public enum Action {
        NONE,
        ADD,
        REMOVE,
        UPDATE;

        private static final Action[] VALUES = values();

        public static Action from(int ordinal) {
            if (ordinal >= 0 && ordinal < VALUES.length) {
                return VALUES[ordinal];
            }
            throw new UnsupportedOperationException("Detected unknown ServerWaypointPayload.Action ID: " + ordinal);
        }
    }
}
