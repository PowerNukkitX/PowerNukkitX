package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityMobSpawner;
import org.powernukkitx.blockentity.BlockEntitySpawnable;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.SessionState;
import org.powernukkitx.network.process.cache.ClientBlobCacheManager;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.payload.chunk.HeightMapDataType;
import org.cloudburstmc.protocol.bedrock.data.payload.chunk.SubChunkHeightmapData;
import org.cloudburstmc.protocol.bedrock.data.payload.chunk.SubChunkPacketData;
import org.cloudburstmc.protocol.bedrock.data.payload.chunk.SubChunkRequestResult;
import org.cloudburstmc.protocol.bedrock.data.payload.common.DimensionType;
import org.cloudburstmc.protocol.bedrock.packet.SubChunkPacket;
import org.cloudburstmc.protocol.bedrock.packet.SubChunkRequestPacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;

import java.io.IOException;

/**
 * Handles client subchunk requests and serializes the requested chunk data.
 *
 * @author Curse
 */
public class SubChunkRequestHandler implements PacketHandler<SubChunkRequestPacket> {

    @Override
    public void handle(SubChunkRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();

        if (player == null || holder.getState() != SessionState.CHUNKS) return;

        final Level level = player.getLevel();
        final DimensionType dimensionType = packet.getDimensionType();
        final Vector3i centerPos = packet.getCenterPos();

        ClientBlobCacheManager.TransferBuilder cacheTransfer = null;

        final SubChunkPacket responsePacket = new SubChunkPacket();
        responsePacket.setDimensionType(dimensionType);
        responsePacket.setCenterPos(centerPos);

        if (dimensionType.getValue() != level.getDimension()) {
            for (Vector3i offsetPos : packet.getSubChunkPosOffsetList()) {
                responsePacket.getSubChunkData().add(createFailureData(offsetPos, SubChunkRequestResult.WRONG_DIMENSION));
            }

            player.sendPacketImmediately(responsePacket);
            return;
        }

        final DimensionData dimensionData = level.getDimensionData();
        final boolean cacheSupported = !shouldBypassCacheForRequest(player, centerPos, packet) && ClientBlobCacheManager.isEnabled(player.getSession());

        if (cacheSupported) {
            cacheTransfer = ClientBlobCacheManager.startTransfer(player.getSession());
        }

        final boolean cacheEnabled = cacheTransfer != null;
        responsePacket.setCacheEnabled(cacheEnabled);

        try {
            for (Vector3i offsetPos : packet.getSubChunkPosOffsetList()) {
                final Vector3i subChunkPos = centerPos.add(offsetPos);
                final int subChunkY = subChunkPos.getY();

                if (subChunkY < dimensionData.getMinSectionY() || subChunkY > dimensionData.getMaxSectionY()) {
                    responsePacket.getSubChunkData().add(createFailureData(offsetPos, SubChunkRequestResult.INDEX_OUT_OF_BOUNDS));
                    continue;
                }

                final IChunk chunk = level.getChunkIfLoaded(subChunkPos.getX(), subChunkPos.getZ());

                if (chunk == null) {
                    responsePacket.getSubChunkData().add(createFailureData(offsetPos, SubChunkRequestResult.LEVEL_CHUNK_DOESNT_EXIST));
                    continue;
                }

                final ChunkSection section = chunk.getSection(subChunkY);

                if (section == null || isAllAir(section)) {
                    responsePacket.getSubChunkData().add(createAllAirData(chunk, offsetPos, subChunkY));
                    continue;
                }

                responsePacket.getSubChunkData().add(createSuccessData(chunk, section, offsetPos, subChunkPos, level, cacheTransfer));
            }

            player.sendPacketImmediately(responsePacket);
        } finally {
            if (cacheTransfer != null) {
                cacheTransfer.close();
            }
        }
    }

    private boolean shouldBypassCacheForRequest(Player player, Vector3i centerPos, SubChunkRequestPacket packet) {
        for (Vector3i offsetPos : packet.getSubChunkPosOffsetList()) {
            final Vector3i subChunkPos = centerPos.add(offsetPos);
            if (player.getPlayerChunkManager().shouldBypassChunkCache(Level.chunkHash(subChunkPos.getX(), subChunkPos.getZ()))) return true;
        }

        return false;
    }

    private SubChunkPacketData createSuccessData(IChunk chunk, ChunkSection section, Vector3i offsetPos, Vector3i subChunkPos, Level level, ClientBlobCacheManager.TransferBuilder cacheTransfer) {
        final ByteBuf terrainData = PooledByteBufAllocator.DEFAULT.ioBuffer();
        final ByteBuf serializedSubChunk = PooledByteBufAllocator.DEFAULT.ioBuffer();
        boolean success = false;

        try {
            if (level.isAntiXrayEnabled()) {
                section.writeObfuscatedToBuf(level, terrainData);
            } else {
                section.writeToBuf(terrainData);
            }

            final SubChunkPacketData data = new SubChunkPacketData();

            data.setSubChunkPosOffset(offsetPos);
            data.setSubChunkRequestResult(SubChunkRequestResult.SUCCESS);

            if (cacheTransfer != null) {
                data.setBlobId(cacheTransfer.remember(terrainData));
            } else {
                data.setBlobId(null);
                serializedSubChunk.writeBytes(terrainData, terrainData.readerIndex(), terrainData.readableBytes());
            }

            writeBlockEntitiesForSubChunk(chunk, subChunkPos, serializedSubChunk);
            data.setSerializedSubChunk(serializedSubChunk);
            data.setHeightMapData(createHeightMapData(chunk, subChunkPos.getY()));

            success = true;
            return data;
        } finally {
            terrainData.release();

            if (!success) {
                serializedSubChunk.release();
            }
        }
    }

    private SubChunkPacketData createAllAirData(IChunk chunk, Vector3i offsetPos, int subChunkY) {
        final SubChunkPacketData data = new SubChunkPacketData();
        data.setSubChunkPosOffset(offsetPos);
        data.setSubChunkRequestResult(SubChunkRequestResult.SUCCESS_ALL_AIR);
        data.setSerializedSubChunk(null);
        data.setHeightMapData(createHeightMapData(chunk, subChunkY));

        return data;
    }

    private SubChunkPacketData createFailureData(Vector3i offsetPos, SubChunkRequestResult result) {
        final SubChunkPacketData data = new SubChunkPacketData();
        data.setSubChunkPosOffset(offsetPos);
        data.setSubChunkRequestResult(result);
        data.setSerializedSubChunk(null);
        data.setHeightMapData(createNoHeightMapData());

        return data;
    }

    private void writeBlockEntitiesForSubChunk(IChunk chunk, Vector3i subChunkPos, ByteBuf buffer) {
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
                NBTOutputStream outputStream = NbtUtils.createNetworkWriter(stream)) {

            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (!(blockEntity instanceof BlockEntitySpawnable spawnable)) continue;

                if ((blockEntity.getFloorX() >> 4) != subChunkPos.getX()
                        || (blockEntity.getFloorY() >> 4) != subChunkPos.getY()
                        || (blockEntity.getFloorZ() >> 4) != subChunkPos.getZ()) {
                    continue;
                }

                if (blockEntity instanceof BlockEntityMobSpawner spawner && !spawner.hasSpawnEntityType()) continue;

                outputStream.writeTag(spawnable.getSpawnCompound().toNetwork());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize SubChunk block entities", e);
        }
    }

    private SubChunkHeightmapData createNoHeightMapData() {
        final SubChunkHeightmapData data = new SubChunkHeightmapData();
        data.setHeightMapType(HeightMapDataType.NO_DATA);
        data.setRenderHeightMapType(HeightMapDataType.NO_DATA);

        return data;
    }

    private SubChunkHeightmapData createHeightMapData(IChunk chunk, int subChunkY) {
        final HeightMapResult heightMap = createHeightMapResult(chunk, subChunkY, false);
        final HeightMapResult renderHeightMap = createHeightMapResult(chunk, subChunkY, true);
        final SubChunkHeightmapData data = new SubChunkHeightmapData();

        data.setHeightMapType(heightMap.type());

        if (heightMap.type() == HeightMapDataType.HAS_DATA) {
            data.setSubchunkHeightMap(heightMap.buffer());
        }

        if (heightMap.type() == HeightMapDataType.HAS_DATA && renderHeightMap.type() == HeightMapDataType.HAS_DATA && ByteBufUtil.equals(heightMap.buffer(), renderHeightMap.buffer())) {
            data.setRenderHeightMapType(HeightMapDataType.ALL_COPIED);
            renderHeightMap.buffer().release();
        } else {
            data.setRenderHeightMapType(renderHeightMap.type());

            if (renderHeightMap.type() == HeightMapDataType.HAS_DATA) {
                data.setSubchunkRenderHeightMap(renderHeightMap.buffer());
            }
        }

        return data;
    }

    private HeightMapResult createHeightMapResult(IChunk chunk, int subChunkY, boolean render) {
        final ByteBuf heightMap = PooledByteBufAllocator.DEFAULT.ioBuffer(256, 256);
        final int subChunkMinY = subChunkY << 4;
        boolean allTooHigh = true;
        boolean allTooLow = true;

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                final int blockY = render ? chunk.getRenderHeightMap(x, z) : chunk.getHeightMap(x, z);
                final int relativeY = blockY - subChunkMinY;

                if (relativeY > 15) {
                    allTooLow = false;
                    heightMap.writeByte(16);
                } else if (relativeY < 0) {
                    allTooHigh = false;
                    heightMap.writeByte(-1);
                } else {
                    allTooHigh = false;
                    allTooLow = false;
                    heightMap.writeByte(relativeY);
                }
            }
        }

        final HeightMapDataType type;

        if (allTooHigh) {
            type = HeightMapDataType.TOO_HIGH;
        } else if (allTooLow) {
            type = HeightMapDataType.TOO_LOW;
        } else {
            type = HeightMapDataType.HAS_DATA;
        }

        if (type != HeightMapDataType.HAS_DATA) {
            heightMap.release();
            return new HeightMapResult(type, null);
        }

        return new HeightMapResult(type, heightMap);
    }

    private record HeightMapResult(HeightMapDataType type, ByteBuf buffer) {
    }

    private boolean isAllAir(ChunkSection section) {
        return section.blockLayer()[0].isEmpty() && section.blockLayer()[1].isEmpty();
    }
}
