package cn.nukkit.network.protocol.types.itemstack.request;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

public enum TextProcessingEventOrigin {
    SERVER_CHAT_PUBLIC(0),
    SERVER_CHAT_WHISPER(1),
    SIGN_TEXT(2),
    ANVIL_TEXT(3),
    BOOK_AND_QUILL_TEXT(4),
    COMMAND_BLOCK_TEXT(5),
    BLOCK_ENTITY_DATA_TEXT(6),
    JOIN_EVENT_TEXT(7),
    LEAVE_EVENT_TEXT(8),
    SLASH_COMMAND_TEXT(9),
    CARTOGRAPHY_TEXT(10),
    SLASH_COMMAND_NON_CHAT(11),
    SCOREBOARD_TEXT(12),
    TICKING_AREA_TEXT(13);

    private final int id;
    private static final Int2ObjectArrayMap<TextProcessingEventOrigin> VALUES = new Int2ObjectArrayMap<>();

    static {
        for (var v : values()) {
            VALUES.put(v.getId(), v);
        }
    }

    TextProcessingEventOrigin(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public static TextProcessingEventOrigin fromId(int id) {
        return VALUES.get(id);
    }
}