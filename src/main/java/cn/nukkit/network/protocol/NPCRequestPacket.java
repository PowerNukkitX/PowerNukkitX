package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

@ToString
public class NPCRequestPacket extends DataPacket {

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final byte NETWORK_ID = ProtocolInfo.NPC_REQUEST_PACKET;

    @Since("1.4.0.0-PN")
    public long entityRuntimeId;

    @Since("1.4.0.0-PN")
    public RequestType requestType = RequestType.SET_SKIN;

    @Since("1.4.0.0-PN")
    public String data = "";

    @Since("1.4.0.0-PN")
    public int skinType = 0;

    @Since("FUTURE")
    public String sceneName = "";

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public long getRequestedEntityRuntimeId() {
        return entityRuntimeId;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setRequestedEntityRuntimeId(long entityRuntimeId) {
        this.entityRuntimeId = entityRuntimeId;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public RequestType getRequestType() {
        return requestType;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public String getData() {
        return data;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setData(String data) {
        this.data = data;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public int getSkinType() {
        return skinType;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setSkinType(int skinType) {
        this.skinType = skinType;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public String getSceneName() {
        return sceneName;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    @Since("1.4.0.0-PN")
    public enum RequestType {

        @Since("1.4.0.0-PN") SET_ACTIONS,
        @Since("1.4.0.0-PN") EXECUTE_ACTION,
        @Since("1.4.0.0-PN") EXECUTE_CLOSING_COMMANDS,
        @Since("1.4.0.0-PN") SET_NAME,
        @Since("1.4.0.0-PN") SET_SKIN,
        @Since("1.4.0.0-PN") SET_INTERACTION_TEXT,
        @Since("1.6.0.0-PNX") EXECUTE_OPENING_COMMANDS
    }

    @Override
    public byte pid() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = super.getEntityRuntimeId();
        this.requestType = RequestType.values()[this.getByte()];
        this.data = this.getString();
        this.skinType = this.getByte();
        this.sceneName = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte((byte) requestType.ordinal());
        this.putString(this.data);
        this.putByte((byte) this.skinType);
        this.putString(this.sceneName);
    }
}
