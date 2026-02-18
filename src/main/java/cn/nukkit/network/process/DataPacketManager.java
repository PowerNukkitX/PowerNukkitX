package cn.nukkit.network.process;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.processor.*;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * DataPacketManager is a static class to manage DataPacketProcessors and process DataPackets.
 */
public final class DataPacketManager {
    private final Map<Class<? extends BedrockPacket>, DataPacketProcessor<? extends BedrockPacket>> PROCESSORS = new HashMap<>(300);

    public DataPacketManager() {
        registerDefaultProcessors();
    }

    @SafeVarargs
    public final void registerProcessor(@NotNull DataPacketProcessor<? extends BedrockPacket>... processors) {
        for (var processor : processors) {
            PROCESSORS.put(processor.getPacketClass(), processor);
        }
    }

    public boolean canProcess(@NotNull BedrockPacket packet) {
        return PROCESSORS.containsKey(packet.getClass());
    }

    public void processPacket(@NotNull PlayerHandle playerHandle, @NotNull BedrockPacket packet) {
        var processor = PROCESSORS.get(packet.getClass());
        if (processor != null) {
            processor.handlePacket(playerHandle, packet);
        } else {
            throw new UnsupportedOperationException("No processor found for packet " + packet.getClass().getName() + ".");
        }
    }

    public void registerDefaultProcessors() {
        registerProcessor(
                new LoginProcessor(),
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
                new StructureBlockTemplateDataProcessor(),
                new TextProcessor(),
                new ContainerCloseProcessor(),
                new CraftingEventProcessor(),
                new BlockEntityDataProcessor(),
                new SetPlayerGameTypeProcessor(),
                new LecternUpdateProcessor(),
                new MapInfoRequestProcessor(),
                /*
                 * Minecraft doesn't really use this packet any more, and the client can send it and play the music it wants,
                 * even though it's of no interest to the gameplay.
                 * The client isn't supposed to be able to broadcast a sound to the whole server.
                 * @Zwuiix
                new LevelSoundEventProcessor(),
                new LevelSoundEventProcessorV1(),
                new LevelSoundEventProcessorV2(),
                */
                //new PlayerHotbarProcessor(),
                new ServerSettingsRequestProcessor(),
                new RespawnProcessor(),
                new BookEditProcessor(),
                new SetDifficultyProcessor(),
                new SettingsCommandProcessor(),
                new PositionTrackingDBClientRequestProcessor(),
                new ShowCreditsProcessor(),
                new TickSyncProcessor(),
                new RequestPermissionsProcessor(),
                new ItemStackRequestPacketProcessor(),
                new SetLocalPlayerAsInitializedPacketProcessor(),
                new ToggleCrafterSlotRequestPacketProcessor()
        );
    }
}
