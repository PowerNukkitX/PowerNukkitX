package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.BlockChangeEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;


public class UpdateSubChunkBlocksPacket extends DataPacket {
    public final int chunkX;
    public final int chunkY;
    public final int chunkZ;

    public final List<BlockChangeEntry> standardBlocks = new ObjectArrayList<>();
    public final List<BlockChangeEntry> extraBlocks = new ObjectArrayList<>();

    public UpdateSubChunkBlocksPacket(int chunkX, int chunkY, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_SUB_CHUNK_BLOCKS_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(chunkX);
        byteBuf.writeUnsignedVarInt(chunkY);
        byteBuf.writeVarInt(chunkZ);
        byteBuf.writeUnsignedVarInt(standardBlocks.size());
        for (final var each : standardBlocks) {
            byteBuf.writeBlockVector3(each.blockPos());
            byteBuf.writeUnsignedVarInt((int) each.runtimeID());
            byteBuf.writeUnsignedVarInt(each.updateFlags());
            byteBuf.writeUnsignedVarLong(each.messageEntityID());
            byteBuf.writeUnsignedVarInt(each.messageType().ordinal());
        }
        byteBuf.writeUnsignedVarInt(extraBlocks.size());
        for (final var each : extraBlocks) {
            byteBuf.writeBlockVector3(each.blockPos());
            byteBuf.writeUnsignedVarInt((int) each.runtimeID());
            byteBuf.writeUnsignedVarInt(each.updateFlags());
            byteBuf.writeUnsignedVarLong(each.messageEntityID());
            byteBuf.writeUnsignedVarInt(each.messageType().ordinal());
        }
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkY() {
        return this.chunkY;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public List<BlockChangeEntry> getStandardBlocks() {
        return this.standardBlocks;
    }

    public List<BlockChangeEntry> getExtraBlocks() {
        return this.extraBlocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateSubChunkBlocksPacket that)) return false;
        return chunkX == that.chunkX && chunkY == that.chunkY && chunkZ == that.chunkZ && standardBlocks.equals(that.standardBlocks) && extraBlocks.equals(that.extraBlocks);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.chunkX;
        result = result * PRIME + this.chunkY;
        result = result * PRIME + this.chunkZ;
        result = result * PRIME + ((Object) this.standardBlocks).hashCode();
        result = result * PRIME + ((Object) this.extraBlocks).hashCode();
        return result;
    }

    public String toString() {
        return "UpdateSubChunkBlocksPacket(chunkX=" + this.chunkX + ", chunkY=" + this.chunkY + ", chunkZ=" + this.chunkZ + ", standardBlocks=" + this.standardBlocks + ", extraBlocks=" + this.extraBlocks + ")";
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
