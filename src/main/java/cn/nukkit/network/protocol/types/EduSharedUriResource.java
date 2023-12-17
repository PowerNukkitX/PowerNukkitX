package cn.nukkit.network.protocol.types;


public record EduSharedUriResource(String buttonName, String linkUri) {
    public static final EduSharedUriResource EMPTY = new EduSharedUriResource("", "");
}
