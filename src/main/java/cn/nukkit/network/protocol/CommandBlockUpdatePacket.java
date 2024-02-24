package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class CommandBlockUpdatePacket extends DataPacket {

    public boolean isBlock;
    public int x;
    public int y;
    public int z;
    public int commandBlockMode;
    public boolean isRedstoneMode;
    public boolean isConditional;
    public long minecartEid;
    public String command;
    public String lastOutput;
    public String name;
    public boolean shouldTrackOutput;


    public int tickDelay;


    public boolean executingOnFirstTick;

    @Override
    public int pid() {
        return ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.isBlock = byteBuf.readBoolean();
        if (this.isBlock) {
            BlockVector3 v = byteBuf.readBlockVector3();
            this.x = v.x;
            this.y = v.y;
            this.z = v.z;
            this.commandBlockMode = (int) byteBuf.readUnsignedVarInt();
            this.isRedstoneMode = byteBuf.readBoolean();
            this.isConditional = byteBuf.readBoolean();
        } else {
            this.minecartEid = byteBuf.readEntityRuntimeId();
        }
        this.command = byteBuf.readString();
        this.lastOutput = byteBuf.readString();
        this.name = byteBuf.readString();
        this.shouldTrackOutput = byteBuf.readBoolean();
        this.tickDelay = byteBuf.readIntLE();
        this.executingOnFirstTick = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeBoolean(this.isBlock);
        if (this.isBlock) {
            byteBuf.writeBlockVector3(this.x, this.y, this.z);
            byteBuf.writeUnsignedVarInt(this.commandBlockMode);
            byteBuf.writeBoolean(this.isRedstoneMode);
            byteBuf.writeBoolean(this.isConditional);
        } else {
            byteBuf.writeEntityRuntimeId(this.minecartEid);
        }
        byteBuf.writeString(this.command);
        byteBuf.writeString(this.lastOutput);
        byteBuf.writeString(this.name);
        byteBuf.writeBoolean(this.shouldTrackOutput);
        byteBuf.writeIntLE(this.tickDelay);
        byteBuf.writeBoolean(this.executingOnFirstTick);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
