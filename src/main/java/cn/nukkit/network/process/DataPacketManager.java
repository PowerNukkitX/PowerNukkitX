package cn.nukkit.network.process;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.processor.*;
import cn.nukkit.network.protocol.DataPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * DataPacketManager is a static class to manage DataPacketProcessors and process DataPackets.
 */
@SuppressWarnings("rawtypes")
public final class DataPacketManager {
    private final Int2ObjectOpenHashMap<DataPacketProcessor> PROCESSORS = new Int2ObjectOpenHashMap<>(300);

    public DataPacketManager() {
        registerDefaultProcessors();
    }

    public void registerProcessor(@NotNull DataPacketProcessor... processors) {
        for (var processor : processors) {
            PROCESSORS.put(processor.getPacketId(), processor);
        }
        PROCESSORS.trim();
    }

    public boolean canProcess(int packetId) {
        return PROCESSORS.containsKey(packetId);
    }

    public void processPacket(@NotNull PlayerHandle playerHandle, @NotNull DataPacket packet) {
        var processor = PROCESSORS.get(packet.pid());
        if (processor != null) {
            //noinspection unchecked
            processor.handle(playerHandle, packet);
        } else {
            throw new UnsupportedOperationException("No processor found for packet " + packet.getClass().getName() + " with id " + packet.pid() + ".");
        }
    }

    public void registerDefaultProcessors() {
        registerProcessor(
                new InventoryTransactionProcessor(),
                new PlayerSkinProcessor(),
                new PacketViolationWarningProcessor(),
                new EmoteProcessor(),
                new PlayerInputProcessor(),
                new MovePlayerProcessor(),
                new PlayerAuthInputProcessor(),
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
                new SetPlayerGameTypeProcessor(),
                new LecternUpdateProcessor(),
                new MapInfoRequestProcessor(),
                new LevelSoundEventProcessor(),
                new LevelSoundEventProcessorV1(),
                new LevelSoundEventProcessorV2(),
                new PlayerHotbarProcessor(),
                new ServerSettingsRequestProcessor(),
                new RespawnProcessor(),
                new BookEditProcessor(),
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
