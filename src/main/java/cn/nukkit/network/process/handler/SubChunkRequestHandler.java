package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import cn.nukkit.network.process.SessionState;
import io.netty.buffer.ByteBufAllocator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.HeightMapDataType;
import org.cloudburstmc.protocol.bedrock.data.SubChunkData;
import org.cloudburstmc.protocol.bedrock.data.SubChunkRequestResult;
import org.cloudburstmc.protocol.bedrock.data.payload.common.DimensionType;
import org.cloudburstmc.protocol.bedrock.packet.SubChunkPacket;
import org.cloudburstmc.protocol.bedrock.packet.SubChunkRequestPacket;

import java.util.List;

/**
 * @author Kaooot
 */
public class SubChunkRequestHandler implements PacketHandler<SubChunkRequestPacket> {

    @Override
    public void handle(SubChunkRequestPacket packet, PlayerSessionHolder holder, Server server) {
        System.out.println(packet);
        if (holder.getPlayer() == null || !holder.getState().equals(SessionState.CHUNKS)) {
            holder.disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
            return;
        }
        final Player player = holder.getPlayer();
        final DimensionType dimensionType = packet.getDimensionType();
        final Level level = player.getLevel();
        if (level.getDimension() != dimensionType.getValue()) {
            holder.disconnect(DisconnectFailReason.BAD_PACKET);
            server.getLogger().warning(
                    player.getName() + ": sub chunk request dimension mismatch, expected: " +
                            level.getDimension() + ", received: " + dimensionType.getValue()
            );
            return;
        }

        final Vector3i centerPos = packet.getCenterPos();
        final DimensionData dimensionData = level.getDimensionData();
        final List<SubChunkData> subChunkDataList = new ObjectArrayList<>();
        final List<Vector3i> subChunkPosOffsetList = packet.getSubChunkPosOffsetList();
        for (final Vector3i offsetPos : subChunkPosOffsetList) {
            final Vector3i subChunkPos = centerPos.add(offsetPos);
            final int subChunkY = subChunkPos.getY();
            if (subChunkY < dimensionData.getMinSectionY() || subChunkY > dimensionData.getMaxSectionY()) {
                subChunkDataList.add(this.createFailureSubChunkData(offsetPos, SubChunkRequestResult.INDEX_OUT_OF_BOUNDS));
                server.getLogger().warning(
                        player.getName() + ": requested a subchunk with an index that is out of bounds" +
                                ", subChunkPos: " + offsetPos
                );
                continue;
            }
            final IChunk chunk = level.getChunkIfLoaded(subChunkPos.getX(), subChunkPos.getZ());
//            try {
            subChunkDataList.add(chunk == null ?
                    this.createFailureSubChunkData(offsetPos, SubChunkRequestResult.LEVEL_CHUNK_DOESNT_EXIST) :
                    chunk.getSection(subChunkY)/*  new UnsafeChunk((Chunk) chunk).getOrCreateSection(subChunkY)*/.serialize(chunk, offsetPos, level)
            );
           /* } catch (Exception e) {
                System.out.println("Tried to get SubChunk at " + subChunkPos);
            }*/
        }

        final SubChunkPacket subChunkPacket = this.buildSubChunkPacket(dimensionType, centerPos, subChunkDataList);
        player.sendPacketImmediately(subChunkPacket);
        System.out.println(subChunkPacket);
    }

    private SubChunkPacket buildSubChunkPacket(DimensionType dimensionType, Vector3i centerPos, List<SubChunkData> subChunkDataList) {
        final SubChunkPacket subChunkPacket = new SubChunkPacket();
        subChunkPacket.setDimensionType(dimensionType);
        subChunkPacket.setCenterPos(centerPos);
        subChunkPacket.getSubChunkDataList().addAll(subChunkDataList);
        return subChunkPacket;
    }

    private SubChunkData createFailureSubChunkData(Vector3i position, SubChunkRequestResult result) {
        final SubChunkData subChunkData = new SubChunkData();
        subChunkData.setPosition(position);
        subChunkData.setData(ByteBufAllocator.DEFAULT.ioBuffer());
        subChunkData.setSubChunkRequestResult(result);
        subChunkData.setHeightMapDataType(HeightMapDataType.NO_DATA);
        subChunkData.setRenderHeightMapDataType(HeightMapDataType.NO_DATA);
        return subChunkData;
    }
}