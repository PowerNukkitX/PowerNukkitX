package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetTitlePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SET_TITLE_PACKET;

    private static final TitleAction[] TITLE_ACTIONS = TitleAction.values();

    public static final int TYPE_CLEAR = 0;
    public static final int TYPE_RESET = 1;
    public static final int TYPE_TITLE = 2;
    public static final int TYPE_SUBTITLE = 3;
    public static final int TYPE_ACTION_BAR = 4;
    public static final int TYPE_ANIMATION_TIMES = 5;
    public static final int TYPE_TITLE_JSON = 6;
    public static final int TYPE_SUBTITLE_JSON = 7;
    public static final int TYPE_ACTIONBAR_JSON = 8;

    public int type;
    public String text = "";
    public int fadeInTime = 0;
    public int stayTime = 0;
    public int fadeOutTime = 0;
    public String xuid = "";
    public String platformOnlineId = "";

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.type = byteBuf.readVarInt();
        this.text = byteBuf.readString();
        this.fadeInTime = byteBuf.readVarInt();
        this.stayTime = byteBuf.readVarInt();
        this.fadeOutTime = byteBuf.readVarInt();
        this.xuid = byteBuf.readString();
        this.platformOnlineId = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(type);
        byteBuf.writeString(text);
        byteBuf.writeVarInt(fadeInTime);
        byteBuf.writeVarInt(stayTime);
        byteBuf.writeVarInt(fadeOutTime);
        byteBuf.writeString(xuid);
        byteBuf.writeString(platformOnlineId);
    }

    @NotNull public TitleAction getTitleAction() {
        int currentType = this.type;
        if (currentType >= 0 && currentType < TITLE_ACTIONS.length) {
            return TITLE_ACTIONS[currentType];
        }
        throw new UnsupportedOperationException("Bad type: "+currentType);
    }

    public void setTitleAction(@NotNull TitleAction type) {
        this.type = type.ordinal();
    }

    @NotNull public String getText() {
        return text;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    public int getFadeInTime() {
        return fadeInTime;
    }

    public void setFadeInTime(int fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    public int getStayTime() {
        return stayTime;
    }

    public void setStayTime(int stayTime) {
        this.stayTime = stayTime;
    }

    public int getFadeOutTime() {
        return fadeOutTime;
    }

    public void setFadeOutTime(int fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
    }

    public String getXuid() {
        return xuid;
    }

    public void setXuid(String xuid) {
        this.xuid = xuid;
    }

    public String getPlatformOnlineId() {
        return platformOnlineId;
    }

    public void setPlatformOnlineId(String platformOnlineId) {
        this.platformOnlineId = platformOnlineId;
    }

    public enum TitleAction {
        CLEAR,
        RESET,
        SET_TITLE_MESSAGE,
        SET_SUBTITLE_MESSAGE,
        SET_ACTION_BAR_MESSAGE,
        SET_ANIMATION_TIMES,
        SET_TITLE_JSON,
        SET_SUBTITLE_JSON,
        SET_ACTIONBAR_JSON,
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
