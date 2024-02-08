package cn.nukkit.network.process;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.processor.*;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * DataPacketManager is a static class to manage DataPacketProcessors and process DataPackets.
 */
@SuppressWarnings("rawtypes")
public final class DataPacketManager {
    private final Int2ObjectOpenHashMap<DataPacketProcessor> CURRENT_PROTOCOL_PROCESSORS = new Int2ObjectOpenHashMap<>(300);

    public DataPacketManager() {
        registerDefaultProcessors();
    }

    public void registerProcessor(@NotNull DataPacketProcessor... processors) {
        for (var processor : processors) {
            if (processor.getProtocol() != ProtocolInfo.CURRENT_PROTOCOL) {
                throw new IllegalArgumentException("Processor protocol " + processor.getProtocol() + " does not match current protocol " + ProtocolInfo.CURRENT_PROTOCOL
                        + ". Multi-version support is not implemented yet.");
            }
            CURRENT_PROTOCOL_PROCESSORS.put(processor.getPacketId(), processor);
        }
        CURRENT_PROTOCOL_PROCESSORS.trim();
    }

    public boolean canProcess(int protocol, int packetId) {
        if (protocol != ProtocolInfo.CURRENT_PROTOCOL) {
            return false;
        }
        return CURRENT_PROTOCOL_PROCESSORS.containsKey(packetId);
    }

    public void processPacket(@NotNull PlayerHandle playerHandle, @NotNull DataPacket packet) {
        if (packet.getProtocolUsed() != ProtocolInfo.CURRENT_PROTOCOL) {
            throw new IllegalArgumentException("Packet protocol " + packet.getProtocolUsed() + " does not match current protocol " + ProtocolInfo.CURRENT_PROTOCOL
                    + ". Multi-version support is not implemented yet.");
        }
        var processor = CURRENT_PROTOCOL_PROCESSORS.get(packet.pid());
        if (processor != null) {
            //noinspection unchecked
            processor.handle(playerHandle, packet);
        } else {
            throw new UnsupportedOperationException("No processor found for packet " + packet.getClass().getName() + " with id " + packet.pid() + ".");
        }
    }

    public void registerDefaultProcessors() {
        registerProcessor(
                new RequestNetworkSettingsProcessor(),
                new LoginProcessor(),
                new ClientToServerHandshakeProcessor(),
                new InventoryTransactionProcessor(),
                new ResourcePackClientResponseProcessor(),
                new ResourcePackChunkRequestProcessor(),
                new SetLocalPlayerAsInitializedProcessor(),
                new PlayerSkinProcessor(),
                new PacketViolationWarningProcessor(),
                new EmoteProcessor(),
                new PlayerInputProcessor(),
                new MovePlayerProcessor(),
                new PlayerAuthInputProcessor(),
                new MoveEntityAbsoluteProcessor(),
                new RequestAbilityProcessor(),
                new MobEquipmentProcessor(),
                new PlayerActionProcessor(),
                new ModalFormResponseProcessor(),
                new NPCRequestProcessor(),
                new InteractProcessor(),
                new BlockPickRequestProcessor(),
                new AnimateProcessor(),
                new EntityEventProcessor(),
                new CommandRequestProcessor(),
                new CommandBlockUpdateProcessor(),
                new StructureBlockUpdateProcessor(),
                new TextProcessor(),
                new ContainerCloseProcessor(),
                new CraftingEventProcessor(),
                new BlockEntityDataProcessor(),
                new RequestChunkRadiusProcessor(),
                new SetPlayerGameTypeProcessor(),
                new ItemFrameDropItemProcessor(),
                new LecternUpdateProcessor(),
                new MapInfoRequestProcessor(),
                new LevelSoundEventProcessor(),
                new LevelSoundEventProcessorV1(),
                new LevelSoundEventProcessorV2(),
                new PlayerHotbarProcessor(),
                new ServerSettingsRequestProcessor(),
                new RespawnProcessor(),
                new BookEditProcessor(),
                new FilterTextProcessor(),
                new SetDifficultyProcessor(),
                new PositionTrackingDBClientRequestProcessor(),
                new ShowCreditsProcessor(),
                new TickSyncProcessor(),
                new RequestPermissionsProcessor(),
                new RiderJumpProcessor(),
                new ItemStackRequestPacketProcessor()
        );
    }
}
