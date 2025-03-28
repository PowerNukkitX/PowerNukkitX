package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SubChunkData;
import cn.nukkit.network.protocol.SubChunkPacket;
import cn.nukkit.network.protocol.SubChunkRequestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class SubChunkRequestProcessor extends DataPacketProcessor<SubChunkRequestPacket> {

    private static final ByteBuf EMPTY_BUFFER = Unpooled.wrappedBuffer(new byte[0]);

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SubChunkRequestPacket packet) {
        Player player = playerHandle.player;
        List<SubChunkData> responseData = new ArrayList<>();
        var centerPosition = packet.centerPos;
        var positionOffsets = packet.subChunkPosOffset;
        if(packet.dimensionType != player.getLevel().getDimension()) {
            log.error("Requested dimension id does not equal dimension id the player is in.");
            return;
        }
        var dimensionInfo = player.getLevel().getDimensionData();
        for (Vector3 offset : positionOffsets) {
            int sectionY = (int) (centerPosition.getY() + offset.getY())/* - (dimensionInfo.minSectionY())*/;

            var hMapType = SubChunkData.HeightMapDataType.NO_DATA;
            if (sectionY < dimensionInfo.getMinSectionY() || sectionY > dimensionInfo.getMaxSectionY()) {
                log.warn("Player {} requested sub chunk which is out of bounds", player.getName());
                createSubChunkData(
                        responseData, SubChunkData.SubChunkRequestResult.INDEX_OUT_OF_BOUNDS, offset, hMapType,
                        null, null, null
                );
                continue;
            }

            int cx = (int) (centerPosition.getX() + offset.getX());
            int cz = (int) (centerPosition.getZ() + offset.getZ());
            var chunk = player.getChunk();
            if (chunk == null) {
                log.warn("Player {} requested sub chunk which is not loaded", player.getName());
                createSubChunkData(
                        responseData, SubChunkData.SubChunkRequestResult.CHUNK_NOT_FOUND, offset, hMapType,
                        null, null, null
                );
                continue;
            }

            var hMap = new byte[256];
            boolean higher = true;
            boolean lower = true;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    var y = chunk.getHeightMap(x, z);
                    var i = (z << 4) | x;
                    var hSectionY = y >> 4;
                    if (hSectionY > sectionY) {
                        hMap[i] = 16;
                        lower = false;
                    } else if (hSectionY < sectionY) {
                        hMap[i] = -1;
                        higher = false;
                    } else {
                        hMap[i] = (byte) (y - (hSectionY << 4));
                        higher = false;
                        lower = false;
                    }
                }
            }

            ByteBuf heightMapData;
            if (higher) {
                hMapType = SubChunkData.HeightMapDataType.TOO_HIGH;
                heightMapData = EMPTY_BUFFER;
            } else if (lower) {
                hMapType = SubChunkData.HeightMapDataType.TOO_LOW;
                heightMapData = EMPTY_BUFFER;
            } else {
                hMapType = SubChunkData.HeightMapDataType.HAS_DATA;
                heightMapData = Unpooled.wrappedBuffer(hMap);
            }

            ChunkSection subChunk = chunk.getSection(sectionY);
            SubChunkData.SubChunkRequestResult subChunkRequestResult;
            if (subChunk.isEmpty()) subChunkRequestResult = SubChunkData.SubChunkRequestResult.SUCCESS_ALL_AIR;
            else subChunkRequestResult = SubChunkData.SubChunkRequestResult.SUCCESS;
            createSubChunkData(
                    responseData, subChunkRequestResult, offset, hMapType,
                    heightMapData, subChunk, chunk.getSectionBlockEntities(sectionY)
            );
        }

        SubChunkPacket subChunkPacket = new SubChunkPacket();
        subChunkPacket.subChunks = responseData;
        subChunkPacket.dimensionType = packet.dimensionType;
        subChunkPacket.centerPos = centerPosition;
        player.dataPacket(subChunkPacket);
    }

    @SneakyThrows
    private static void createSubChunkData(
            List<SubChunkData> response,
            SubChunkData.SubChunkRequestResult result,
            Vector3 offset,
            SubChunkData.HeightMapDataType type,
            ByteBuf heightMapData,
            ChunkSection subchunk,
            Collection<BlockEntity> subChunkBlockEntities
    ) {
        var subChunkData = new SubChunkData();
        subChunkData.setPosition(offset);
        subChunkData.setResult(result);

        if (result == SubChunkData.SubChunkRequestResult.SUCCESS) {
            var buffer = ByteBufAllocator.DEFAULT.ioBuffer();
            subchunk.writeToBuf(buffer);
            subchunk.biomes().writeToNetwork(buffer, value -> value);
            // edu - border blocks
            buffer.writeByte(0);
            for (var blockEntity : subChunkBlockEntities) {
                buffer.writeBytes(NBTIO.write(blockEntity.namedTag));
            }

            subChunkData.setData(buffer);
        } else {
            subChunkData.setData(EMPTY_BUFFER);
        }

        subChunkData.setHeightMapType(type);
        if (type == SubChunkData.HeightMapDataType.HAS_DATA)
            subChunkData.setHeightMapData(heightMapData);
        response.add(subChunkData);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.SUB_CHUNK_REQUEST_PACKET;
    }
}
