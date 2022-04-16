package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.types.BlockChangeEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true)
@Value
public class UpdateSubChunkBlocksPacket extends DataPacket {
    public int chunkX;
    public int chunkY;
    public int chunkZ;

    public List<BlockChangeEntry> standardBlocks = new ObjectArrayList<>();
    public List<BlockChangeEntry> extraBlocks = new ObjectArrayList<>();

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
        for (final var each : standardBlocks) {
            putBlockVector3(each.blockPos());
            putUnsignedVarInt(each.runtimeID());
            putUnsignedVarInt(each.updateFlags());
            putUnsignedVarLong(each.messageEntityID());
            putUnsignedVarInt(each.messageType().ordinal());
        }
        for (final var each : extraBlocks) {
            putBlockVector3(each.blockPos());
            putUnsignedVarInt(each.runtimeID());
            putUnsignedVarInt(each.updateFlags());
            putUnsignedVarLong(each.messageEntityID());
            putUnsignedVarInt(each.messageType().ordinal());
        }
    }
}
