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
    public static final int $1 = ProtocolInfo.SET_TITLE_PACKET;

    private static final TitleAction[] TITLE_ACTIONS = TitleAction.values();

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;
    public static final int $5 = 3;
    public static final int $6 = 4;
    public static final int $7 = 5;
    public static final int $8 = 6;
    public static final int $9 = 7;
    public static final int $10 = 8;

    public int type;
    public String $11 = "";
    public int $12 = 0;
    public int $13 = 0;
    public int $14 = 0;
    public String $15 = "";
    public String $16 = "";

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
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
        int $17 = this.type;
        if (currentType >= 0 && currentType < TITLE_ACTIONS.length) {
            return TITLE_ACTIONS[currentType];
        }
        throw new UnsupportedOperationException("Bad type: "+currentType);
    }
    /**
     * @deprecated 
     */
    

    public void setTitleAction(@NotNull TitleAction type) {
        this.type = type.ordinal();
    }

    @NotNull
    /**
     * @deprecated 
     */
     public String getText() {
        return text;
    }
    /**
     * @deprecated 
     */
    

    public void setText(@NotNull String text) {
        this.text = text;
    }
    /**
     * @deprecated 
     */
    

    public int getFadeInTime() {
        return fadeInTime;
    }
    /**
     * @deprecated 
     */
    

    public void setFadeInTime(int fadeInTime) {
        this.fadeInTime = fadeInTime;
    }
    /**
     * @deprecated 
     */
    

    public int getStayTime() {
        return stayTime;
    }
    /**
     * @deprecated 
     */
    

    public void setStayTime(int stayTime) {
        this.stayTime = stayTime;
    }
    /**
     * @deprecated 
     */
    

    public int getFadeOutTime() {
        return fadeOutTime;
    }
    /**
     * @deprecated 
     */
    

    public void setFadeOutTime(int fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
    }
    /**
     * @deprecated 
     */
    

    public String getXuid() {
        return xuid;
    }
    /**
     * @deprecated 
     */
    

    public void setXuid(String xuid) {
        this.xuid = xuid;
    }
    /**
     * @deprecated 
     */
    

    public String getPlatformOnlineId() {
        return platformOnlineId;
    }
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
