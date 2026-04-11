package cn.nukkit.network.process;

import cn.nukkit.network.process.handler.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.packet.*;

import java.util.Map;

/**
 * @author Kaooot
 */
@UtilityClass
public class PacketHandlerRegistry {

    private final Map<Class<? extends BedrockPacket>, PacketHandler> MAP = new Object2ObjectOpenHashMap<>();

    static {
        register(RequestNetworkSettingsPacket.class, new RequestNetworkSettingsHandler());
        register(LoginPacket.class, new LoginHandler());
        register(ClientToServerHandshakePacket.class, new ClientToServerHandshakeHandler());
        register(ResourcePackClientResponsePacket.class, new ResourcePackClientResponseHandler());
        register(RequestChunkRadiusPacket.class, new RequestChunkRadiusHandler());
        register(SetLocalPlayerAsInitializedPacket.class, new SetLocalPlayerAsInitializedHandler());
        register(PlayerAuthInputPacket.class, new PlayerAuthInputHandler());
        register(InteractPacket.class, new InteractHandler());
        register(ContainerClosePacket.class, new ContainerCloseHandler());
        register(ActorEventPacket.class, new ActorEventHandler());
        register(ActorPickRequestPacket.class, new ActorPickRequestHandler());
        register(AnimatePacket.class, new AnimateHandler());
        register(BlockActorDataPacket.class, new BlockActorDataHandler());
        register(BlockPickRequestPacket.class, new BlockPickRequestHandler());
        register(BookEditPacket.class, new BookEditHandler());
        register(CommandBlockUpdatePacket.class, new CommandBlockUpdateHandler());
        register(CommandRequestPacket.class, new CommandRequestHandler());
        register(EmotePacket.class, new EmoteHandler());
        register(InventoryTransactionPacket.class, new InventoryTransactionHandler());
        register(ItemStackRequestPacket.class, new ItemStackRequestHandler());
        register(LecternUpdatePacket.class, new LecternUpdateHandler());
        register(LevelSoundEventPacket.class, new LevelSoundEventHandler());
        register(MapInfoRequestPacket.class, new MapInfoRequestHandler());
        register(MobEquipmentPacket.class, new MobEquipmentHandler());
        register(ModalFormResponsePacket.class, new ModalFormResponseHandler());
        register(MoveActorAbsolutePacket.class, new MoveActorAbsoluteHandler());
        register(MovePlayerPacket.class, new MovePlayerHandler());
        register(NpcRequestPacket.class, new NpcRequestHandler());
        register(PacketViolationWarningPacket.class, new PacketViolationWarningHandler());
        register(PlayerActionPacket.class, new PlayerActionHandler());
        register(PlayerHotbarPacket.class, new PlayerHotbarHandler());
        register(PlayerSkinPacket.class, new PlayerSkinHandler());
        register(PlayerToggleCrafterSlotRequestPacket.class, new PlayerToggleCrafterSlotRequestHandler());
        register(PositionTrackingDBClientRequestPacket.class, new PositionTrackingDBClientRequestHandler());
        register(RequestAbilityPacket.class, new RequestAbilityHandler());
        register(RequestPermissionsPacket.class, new RequestPermissionsHandler());
        register(RespawnPacket.class, new RespawnHandler());
        register(ServerboundDataStorePacket.class, new ServerboundDataStoreHandler());
        register(ServerSettingsRequestPacket.class, new ServerSettingsRequestHandler());
        register(ServerboundDiagnosticsPacket.class, new ServerboundDiagnosticsHandler());
        register(SetDifficultyPacket.class, new SetDifficultyHandler());
        register(SetPlayerGameTypePacket.class, new SetPlayerGameTypeHandler());
        register(SettingsCommandPacket.class, new SettingsCommandHandler());
        register(ShowCreditsPacket.class, new ShowCreditsHandler());
        register(StructureBlockUpdatePacket.class, new StructureBlockUpdateHandler());
        register(StructureTemplateDataRequestPacket.class, new StructureTemplateDataRequestHandler());
        register(TextPacket.class, new TextHandler());
        register(TickSyncPacket.class, new TickSyncHandler());
    }

    private void register(Class<? extends BedrockPacket> clazz, PacketHandler packetHandler) {
        MAP.put(clazz, packetHandler);
    }

    public PacketHandler getPacketHandler(Class<? extends BedrockPacket> clazz) {
        if (MAP.containsKey(clazz)) {
            return MAP.get(clazz);
        }
        return null;
    }
}