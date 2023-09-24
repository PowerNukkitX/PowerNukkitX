package cn.nukkit.player.data;

import lombok.Getter;

@Getter
public enum Device {
    UNKNOWN(-1, "Unknown"),
    ANDROID(1, "Android"),
    IOS(2, "iOS"),
    OSX(3, "Mac"),
    AMAZON(4, "Fire OS"),
    GEAR_VR(5, "Gear VR"),
    HOLOLENS(6, "HoloLens"),
    WINDOWS(7, "Windows"),
    WINDOWS_32(8, "Windows"),
    DEDICATED(9, "Dedicated"),
    TVOS(10, "tvOS"),
    PLAYSTATION(11, "PlayStation"),
    NINTENDO(12, "Nintendo Switch"),
    XBOX(13, "Xbox"),
    WINDOWS_PHONE(14, "Windows Phone");

    private final int id;
    private final String name;

    private static final Device[] VALUES = values();

    Device(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Device fromId(int id) {
        return id < 0 || id >= VALUES.length ? UNKNOWN : VALUES[id];
    }
}
