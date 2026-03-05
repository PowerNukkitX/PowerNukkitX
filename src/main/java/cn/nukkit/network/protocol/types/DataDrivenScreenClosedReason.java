package cn.nukkit.network.protocol.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataDrivenScreenClosedReason {

    PROGRAMMATIC_CLOSE("programmaticclose"),
    PROGRAMMATIC_CLOSE_ALL("programaticcloseall"),
    CLIENT_CANCELED("clientcanceled"),
    USER_BUSY("userbusy"),
    INVALID_FORM("invalidform");

    private final String id;

    private static final DataDrivenScreenClosedReason[] VALUES = values();

    public static DataDrivenScreenClosedReason from(int ordinal) {
        if (ordinal >= 0 && ordinal < VALUES.length) {
            return VALUES[ordinal];
        }
        throw new UnsupportedOperationException("Detected unknown DataDrivenScreenClosedReason ID: " + ordinal);
    }

    public static DataDrivenScreenClosedReason from(String id) {
        for (DataDrivenScreenClosedReason value : VALUES) {
            if (value.getId().equalsIgnoreCase(id)) {
                return value;
            }
        }
        throw new UnsupportedOperationException("Detected unknown DataDrivenScreenClosedReason ID: " + id);
    }
}