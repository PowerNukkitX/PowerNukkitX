package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

import javax.annotation.Nonnull;

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

    public int type;
    public String text = "";
    public int fadeInTime = 0;
    public int stayTime = 0;
    public int fadeOutTime = 0;
    
    private String xuid = "";
    private String platformOnlineId = "";

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

    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    public TitleAction getTitleAction() {
        int currentType = this.type;
        if (currentType >= 0 && currentType < TITLE_ACTIONS.length) {
            return TITLE_ACTIONS[currentType];
        }
        throw new UnsupportedOperationException("Bad type: "+currentType);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setTitleAction(@Nonnull TitleAction type) {
        this.type = type.ordinal();
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    public String getText() {
        return text;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setText(@Nonnull String text) {
        this.text = text;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public int getFadeInTime() {
        return fadeInTime;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setFadeInTime(int fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public int getStayTime() {
        return stayTime;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setStayTime(int stayTime) {
        this.stayTime = stayTime;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public int getFadeOutTime() {
        return fadeOutTime;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setFadeOutTime(int fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getXuid() {
        return xuid;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setXuid(String xuid) {
        this.xuid = xuid;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getPlatformOnlineId() {
        return platformOnlineId;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setPlatformOnlineId(String platformOnlineId) {
        this.platformOnlineId = platformOnlineId;
    }
    
    @PowerNukkitOnly
    @Since("FUTURE")
    public enum TitleAction {
        @PowerNukkitOnly @Since("FUTURE") CLEAR,
        @PowerNukkitOnly @Since("FUTURE") RESET,
        @PowerNukkitOnly @Since("FUTURE") SET_TITLE_MESSAGE,
        @PowerNukkitOnly @Since("FUTURE") SET_SUBTITLE_MESSAGE,
        @PowerNukkitOnly @Since("FUTURE") SET_ACTION_BAR_MESSAGE,
        @PowerNukkitOnly @Since("FUTURE") SET_ANIMATION_TIMES,
        @PowerNukkitOnly @Since("FUTURE") SET_TITLE_JSON,
        @PowerNukkitOnly @Since("FUTURE") SET_SUBTITLE_JSON,
        @PowerNukkitOnly @Since("FUTURE") SET_ACTIONBAR_JSON,
    }
}
