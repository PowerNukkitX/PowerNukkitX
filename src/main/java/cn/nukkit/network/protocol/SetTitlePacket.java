package cn.nukkit.network.protocol;

import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tee7even
 */
@ToString
public class SetTitlePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_TITLE_PACKET;

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
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.type = this.getVarInt();
        this.text = this.getString();
        this.fadeInTime = this.getVarInt();
        this.stayTime = this.getVarInt();
        this.fadeOutTime = this.getVarInt();
        this.xuid = this.getString();
        this.platformOnlineId = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(type);
        this.putString(text);
        this.putVarInt(fadeInTime);
        this.putVarInt(stayTime);
        this.putVarInt(fadeOutTime);
        this.putString(xuid);
        this.putString(platformOnlineId);
    }


    @NotNull
    public TitleAction getTitleAction() {
        int currentType = this.type;
        if (currentType >= 0 && currentType < TITLE_ACTIONS.length) {
            return TITLE_ACTIONS[currentType];
        }
        throw new UnsupportedOperationException("Bad type: "+currentType);
    }


    public void setTitleAction(@NotNull TitleAction type) {
        this.type = type.ordinal();
    }


    @NotNull
    public String getText() {
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
}
