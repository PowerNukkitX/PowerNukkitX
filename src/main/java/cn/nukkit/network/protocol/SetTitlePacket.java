package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
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

    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final int TYPE_TITLE_JSON = 6;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final int TYPE_SUBTITLE_JSON = 7;
    @PowerNukkitXOnly @Since("1.6.0.0-PNX") public static final int TYPE_ACTIONBAR_JSON = 8;

    public int type;
    public String text = "";
    public int fadeInTime = 0;
    public int stayTime = 0;
    public int fadeOutTime = 0;
    @Since("FUTURE") public String xuid = "";
    @Since("FUTURE") public String platformOnlineId = "";

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
    @Since("1.5.2.0-PN")
    @NotNull
    public TitleAction getTitleAction() {
        int currentType = this.type;
        if (currentType >= 0 && currentType < TITLE_ACTIONS.length) {
            return TITLE_ACTIONS[currentType];
        }
        throw new UnsupportedOperationException("Bad type: "+currentType);
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setTitleAction(@NotNull TitleAction type) {
        this.type = type.ordinal();
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    @NotNull
    public String getText() {
        return text;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setText(@NotNull String text) {
        this.text = text;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public int getFadeInTime() {
        return fadeInTime;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setFadeInTime(int fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public int getStayTime() {
        return stayTime;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setStayTime(int stayTime) {
        this.stayTime = stayTime;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public int getFadeOutTime() {
        return fadeOutTime;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setFadeOutTime(int fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public String getXuid() {
        return xuid;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setXuid(String xuid) {
        this.xuid = xuid;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public String getPlatformOnlineId() {
        return platformOnlineId;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setPlatformOnlineId(String platformOnlineId) {
        this.platformOnlineId = platformOnlineId;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public enum TitleAction {
        @PowerNukkitOnly @Since("1.5.2.0-PN") CLEAR,
        @PowerNukkitOnly @Since("1.5.2.0-PN") RESET,
        @PowerNukkitOnly @Since("1.5.2.0-PN") SET_TITLE_MESSAGE,
        @PowerNukkitOnly @Since("1.5.2.0-PN") SET_SUBTITLE_MESSAGE,
        @PowerNukkitOnly @Since("1.5.2.0-PN") SET_ACTION_BAR_MESSAGE,
        @PowerNukkitOnly @Since("1.5.2.0-PN") SET_ANIMATION_TIMES,
        @PowerNukkitOnly @Since("1.5.2.0-PN") SET_TITLE_JSON,
        @PowerNukkitOnly @Since("1.5.2.0-PN") SET_SUBTITLE_JSON,
        @PowerNukkitOnly @Since("1.5.2.0-PN") SET_ACTIONBAR_JSON,
    }
}
