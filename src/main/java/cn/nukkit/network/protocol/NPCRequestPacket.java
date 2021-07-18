package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

@ToString
public class NPCRequestPacket extends DataPacket {

    @Since("1.4.0.0-PN")
    public long entityRuntimeId;

    @Since("1.4.0.0-PN")
    public RequestType requestType;

    @Since("1.4.0.0-PN")
    public String commandString;

    @Since("1.4.0.0-PN")
    public int actionType;
    
    private String sceneName;

    @PowerNukkitOnly
    @Since("FUTURE")
    public long getRequestedEntityRuntimeId() {
        return entityRuntimeId;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setRequestedEntityRuntimeId(long entityRuntimeId) {
        this.entityRuntimeId = entityRuntimeId;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public RequestType getRequestType() {
        return requestType;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getCommandString() {
        return commandString;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public int getActionType() {
        return actionType;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getSceneName() {
        return sceneName;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
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
        @Since("1.4.0.0-PN") SET_INTERACTION_TEXT

    }

    @Override
    public byte pid() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = super.getEntityRuntimeId();
        this.requestType = RequestType.values()[this.getByte()];
        this.commandString = this.getString();
        this.actionType = this.getByte();
        this.sceneName = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte((byte) requestType.ordinal());
        this.putString(this.commandString);
        this.putByte((byte) this.actionType);
        this.putString(sceneName);
    }

}
