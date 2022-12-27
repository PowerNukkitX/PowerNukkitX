package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.types.BlockChangeEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
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
    public byte pid() {
        return ProtocolInfo.UPDATE_SUB_CHUNK_BLOCKS_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        putVarInt(chunkX);
        putUnsignedVarInt(chunkY);
        putVarInt(chunkZ);
        putVarInt(standardBlocks.size());
        for(final var each : standardBlocks) {
            putBlockVector3(each.blockPos());
            putUnsignedVarInt(each.runtimeID());
            putUnsignedVarInt(each.updateFlags());
            putUnsignedVarLong(each.messageEntityID());
            putUnsignedVarInt(each.messageType().ordinal());
        }
        for(final var each : extraBlocks) {
            putBlockVector3(each.blockPos());
            putUnsignedVarInt(each.runtimeID());
            putUnsignedVarInt(each.updateFlags());
            putUnsignedVarLong(each.messageEntityID());
            putUnsignedVarInt(each.messageType().ordinal());
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof final UpdateSubChunkBlocksPacket other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.chunkX != other.chunkX) return false;
        if (this.chunkY != other.chunkY) return false;
        if (this.chunkZ != other.chunkZ) return false;
        if (!((Object) this.standardBlocks).equals(other.standardBlocks))
            return false;
        return ((Object) this.extraBlocks).equals(other.extraBlocks);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UpdateSubChunkBlocksPacket;
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
}
