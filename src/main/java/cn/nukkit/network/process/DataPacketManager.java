package cn.nukkit.network.process;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.processor.*;
import cn.nukkit.network.protocol.DataPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * DataPacketManager is a static class to manage DataPacketProcessors and process DataPackets.
 */
public final class DataPacketManager {
    /**
     * Default processor singletons built once at class-load time. Processors are stateless
     * (playerHandle is supplied per-call), so sharing them across sessions is safe.
     */
    private static final Int2ObjectOpenHashMap<DataPacketProcessor<? extends DataPacket>> DEFAULT_PROCESSORS;

    static {
        DEFAULT_PROCESSORS = new Int2ObjectOpenHashMap<>(64);
        DataPacketProcessor<?>[] defaults = {
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
                new EntityPickRequestProcessor(),
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
                new ServerSettingsRequestProcessor(),
                new ServerboundDataStoreProcessor(),
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
        };
        for (DataPacketProcessor<?> p : defaults) {
            DEFAULT_PROCESSORS.put(p.getPacketId(), p);
        }
        DEFAULT_PROCESSORS.trim();
    }

    // Per-session map; starts as a shallow copy of defaults so plugins can add session-specific processors.
    private final Int2ObjectOpenHashMap<DataPacketProcessor<? extends DataPacket>> PROCESSORS;

    public DataPacketManager() {
        this.PROCESSORS = new Int2ObjectOpenHashMap<>(DEFAULT_PROCESSORS);
    }

    @SafeVarargs
    public final void registerProcessor(@NotNull DataPacketProcessor<? extends DataPacket>... processors) {
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
            processor.handlePacket(playerHandle, packet);
        } else {
            throw new UnsupportedOperationException("No processor found for packet " + packet.getClass().getName() + " with id " + packet.pid() + ".");
        }
    }

    public void registerDefaultProcessors() {
        PROCESSORS.putAll(DEFAULT_PROCESSORS);
        PROCESSORS.trim();
    }
}
