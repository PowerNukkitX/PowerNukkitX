package cn.nukkit.network.protocol;

import cn.nukkit.nbt.tag.CompoundTag;


public class AddVolumeEntityPacket extends DataPacket {


    public static final int NETWORK_ID = ProtocolInfo.ADD_VOLUME_ENTITY_PACKET;

    private long id;
    private CompoundTag data;
    /**
     * @since v465
     */
    private String engineVersion;
    /**
     * @since v485
     */
    private String identifier;
    /**
     * @since v485
     */
    private String instanceName;


    public AddVolumeEntityPacket() {
        // Does nothing
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        id = getUnsignedVarInt();
        data = getTag();
        engineVersion = getString();
        identifier = getString();
        instanceName = getString();
    }

    @Override
    public void encode() {
        reset();
        putUnsignedVarInt(id);
        putTag(data);
        putString(engineVersion);
        putString(identifier);
        putString(instanceName);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CompoundTag getData() {
        return data;
    }

    public void setData(CompoundTag data) {
        this.data = data;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
