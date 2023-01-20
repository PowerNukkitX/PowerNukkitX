package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.List;

@ToString
public class UpdateSoftEnumPacket extends DataPacket {

    public List<String> values = List.of();
    public String name = "";
    public Type type = Type.SET;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(name);
        this.putUnsignedVarInt(values.size());

        for (String value : values) {
            this.putString(value);
        }
        this.putByte((byte) type.ordinal());
    }

    public enum Type {
        ADD,
        REMOVE,
        SET
    }
}
