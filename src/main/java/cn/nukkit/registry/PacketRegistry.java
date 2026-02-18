package cn.nukkit.registry;

import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.codec.v924.Bedrock_v924;
import cn.nukkit.plugin.Plugin;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.sunlan.fastreflection.FastConstructor;
import me.sunlan.fastreflection.FastMemberLoader;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PacketRegistry implements IRegistry<Integer, BedrockPacket, Class<? extends BedrockPacket>> {
    private final Int2ObjectOpenHashMap<FastConstructor<? extends BedrockPacket>> PACKET_POOL = new Int2ObjectOpenHashMap<>(256);
    private final Map<String, FastMemberLoader> fastMemberLoaderCache = new ConcurrentHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        registerPackets();
    }

    @Override
    public BedrockPacket get(Integer key) {
        FastConstructor<? extends BedrockPacket> fastConstructor = PACKET_POOL.get(key.intValue());
        if (fastConstructor == null) {
            return null;
        } else {
            try {
                return (BedrockPacket) fastConstructor.invoke();
            } catch (Error e) {
                throw e;
            } catch (Throwable e) {
                throw new PacketInstantiationException("Failed to instantiate packet", e);
            }
        }
    }

    public BedrockPacket get(int key) {
        FastConstructor<? extends BedrockPacket> fastConstructor = PACKET_POOL.get(key);
        if (fastConstructor == null) {
            return null;
        } else {
            try {
                return (BedrockPacket) fastConstructor.invoke();
            } catch (Error e) {
                throw e;
            } catch (Throwable e) {
                throw new PacketInstantiationException("Failed to instantiate packet", e);
            }
        }
    }

    @Override
    public void trim() {
        PACKET_POOL.trim();
    }

    public void reload() {
        isLoad.set(false);
        PACKET_POOL.clear();
        init();
    }

    /**
     * Registers an internal (non-plugin) packet class into the registry.
     *
     * <p>This method is intended for registering standard/internal packet classes
     * that are part of the server implementation. It creates a {@link me.sunlan.fastreflection.FastConstructor}
     * from the packet class's no-arg constructor and stores it in the packet pool.</p>
     *
     * <p>Do not use this method to register plugin-provided packet classes; use
     * {@code registerCustomPacket} for plugin classes so the plugin classloader is used.</p>
     *
     * @param id    the packet id (should be non-negative)
     * @param clazz the packet class to register (must have a public no-arg constructor)
     * @throws RegisterException if the id is already registered or the class has no no-arg constructor
     */
    @Override
    public void register(Integer id, Class<? extends BedrockPacket> clazz) throws RegisterException
    {
        try {
            if (this.PACKET_POOL.putIfAbsent(id, FastConstructor.create(clazz.getConstructor())) != null) {
                throw new RegisterException("The packet has been registered!");
            }
        } catch (NoSuchMethodException e) {
            throw new RegisterException(e);
        }
    }

    /**
     * Registers a plugin-provided custom packet using the plugin's classloader.
     *
     * <p>This method is intended specifically for registering custom packets supplied
     * by plugins. It ensures instances are instantiated using a {@link FastConstructor}
     * configured with a {@link FastMemberLoader} that uses the plugin's classloader,
     * so plugin classes resolve correctly at runtime.</p>
     *
     * <p>Do not use this method for standard/internal packet registration; internal
     * packets should be registered via the normal internal registration path
     * (for example {@code register0} or {@code register}).</p>
     *
     * @param plugin the plugin that provides the custom packet; its classloader will be used
     * @param id     the packet id (non-negative)
     * @param clazz  the packet class provided by the plugin
     * @throws RegisterException if the packet id is already registered or the class has no no-arg constructor
     */
    public void registerCustomPacket(Plugin plugin, Integer id, Class<? extends BedrockPacket> clazz) throws RegisterException {
        final Constructor<? extends BedrockPacket> ctor;
        try {
            ctor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RegisterException(e);
        }

        FastMemberLoader memberLoader = fastMemberLoaderCache.computeIfAbsent(
                plugin.getName(),
                p -> new FastMemberLoader(plugin.getPluginClassLoader())
        );

        FastConstructor<? extends BedrockPacket> fastCtor = FastConstructor.create(ctor, memberLoader, false);

        if (this.PACKET_POOL.putIfAbsent(id, fastCtor) != null) {
            throw new RegisterException("The packet has been registered!");
        }
    }

    private void register0(@Nonnegative int id, @NotNull Class<? extends BedrockPacket> clazz) {
        try {
            if (this.PACKET_POOL.putIfAbsent(id, FastConstructor.create(clazz.getConstructor())) != null) {
                throw new RegisterException("The packet has been registered!");
            }
        } catch (NoSuchMethodException | RegisterException ignored) {
        }
    }

    private void registerCodecPacket(@NotNull Class<? extends BedrockPacket> clazz) {
        var definition = Bedrock_v924.CODEC.getPacketDefinition(clazz);
        if (definition != null) {
            register0(definition.getId(), clazz);
        }
    }

    private void registerPackets() {
        this.PACKET_POOL.clear();

        this.registerCodecPacket(ServerToClientHandshakePacket.class);
        this.registerCodecPacket(ClientToServerHandshakePacket.class);
        this.registerCodecPacket(AddEntityPacket.class);
        this.registerCodecPacket(AddItemEntityPacket.class);
        this.registerCodecPacket(AddPaintingPacket.class);
        this.registerCodecPacket(AddPlayerPacket.class);
        this.registerCodecPacket(AdventureSettingsPacket.class);
        this.registerCodecPacket(AnimatePacket.class);
        this.registerCodecPacket(AnvilDamagePacket.class);
        this.registerCodecPacket(AvailableCommandsPacket.class);
        this.registerCodecPacket(BlockEntityDataPacket.class);
        this.registerCodecPacket(BlockEventPacket.class);
        this.registerCodecPacket(BlockPickRequestPacket.class);
        this.registerCodecPacket(BookEditPacket.class);
        this.registerCodecPacket(BossEventPacket.class);
        this.registerCodecPacket(ChangeDimensionPacket.class);
        this.registerCodecPacket(ChunkRadiusUpdatedPacket.class);
        this.registerCodecPacket(ClientboundMapItemDataPacket.class);
        this.registerCodecPacket(CommandRequestPacket.class);
        this.registerCodecPacket(ContainerClosePacket.class);
        this.registerCodecPacket(ContainerOpenPacket.class);
        this.registerCodecPacket(CorrectPlayerMovePredictionPacket.class);
        this.registerCodecPacket(ContainerSetDataPacket.class);
        this.registerCodecPacket(CraftingDataPacket.class);
        this.registerCodecPacket(CraftingEventPacket.class);
        this.registerCodecPacket(DisconnectPacket.class);
        this.registerCodecPacket(EntityEventPacket.class);
        this.registerCodecPacket(EntityFallPacket.class);
        this.registerCodecPacket(LevelChunkPacket.class);
        this.registerCodecPacket(GameRulesChangedPacket.class);
        this.registerCodecPacket(HurtArmorPacket.class);
        this.registerCodecPacket(InteractPacket.class);
        this.registerCodecPacket(InventoryContentPacket.class);
        this.registerCodecPacket(InventorySlotPacket.class);
        this.registerCodecPacket(InventoryTransactionPacket.class);
        this.registerCodecPacket(ItemFrameDropItemPacket.class);
        this.registerCodecPacket(LevelEventPacket.class);
        this.registerCodecPacket(LoginPacket.class);
        this.registerCodecPacket(MapInfoRequestPacket.class);
        this.registerCodecPacket(MobArmorEquipmentPacket.class);
        this.registerCodecPacket(MobEquipmentPacket.class);
        this.registerCodecPacket(ModalFormRequestPacket.class);
        this.registerCodecPacket(ModalFormResponsePacket.class);
        this.registerCodecPacket(MoveEntityAbsolutePacket.class);
        this.registerCodecPacket(MovePlayerPacket.class);
        this.registerCodecPacket(PlayerActionPacket.class);
        this.registerCodecPacket(PlayerInputPacket.class);
        this.registerCodecPacket(PlayerListPacket.class);
        this.registerCodecPacket(PlayerHotbarPacket.class);
        this.registerCodecPacket(PlaySoundPacket.class);
        this.registerCodecPacket(PlayStatusPacket.class);
        this.registerCodecPacket(RemoveEntityPacket.class);
        this.registerCodecPacket(RequestChunkRadiusPacket.class);
        this.registerCodecPacket(ResourcePacksInfoPacket.class);
        this.registerCodecPacket(ResourcePackStackPacket.class);
        this.registerCodecPacket(ResourcePackClientResponsePacket.class);
        this.registerCodecPacket(ResourcePackDataInfoPacket.class);
        this.registerCodecPacket(ResourcePackChunkDataPacket.class);
        this.registerCodecPacket(ResourcePackChunkRequestPacket.class);
        this.registerCodecPacket(PlayerSkinPacket.class);
        this.registerCodecPacket(RespawnPacket.class);
        this.registerCodecPacket(RiderJumpPacket.class);
        this.registerCodecPacket(SetCommandsEnabledPacket.class);
        this.registerCodecPacket(SetDifficultyPacket.class);
        this.registerCodecPacket(SetEntityDataPacket.class);
        this.registerCodecPacket(SetEntityLinkPacket.class);
        this.registerCodecPacket(SetEntityMotionPacket.class);
        this.registerCodecPacket(SetHealthPacket.class);
        this.registerCodecPacket(SetPlayerGameTypePacket.class);
        this.registerCodecPacket(SetSpawnPositionPacket.class);
        this.registerCodecPacket(SetTitlePacket.class);
        this.registerCodecPacket(SetTimePacket.class);
        this.registerCodecPacket(ServerSettingsRequestPacket.class);
        this.registerCodecPacket(ServerSettingsResponsePacket.class);
        this.registerCodecPacket(ShowCreditsPacket.class);
        this.registerCodecPacket(SpawnExperienceOrbPacket.class);
        this.registerCodecPacket(StartGamePacket.class);
        this.registerCodecPacket(TakeItemEntityPacket.class);
        this.registerCodecPacket(TextPacket.class);
        this.registerCodecPacket(UpdateAttributesPacket.class);
        this.registerCodecPacket(UpdateBlockPacket.class);
        this.registerCodecPacket(UpdateTradePacket.class);
        this.registerCodecPacket(MoveEntityDeltaPacket.class);
        this.registerCodecPacket(SetLocalPlayerAsInitializedPacket.class);
        this.registerCodecPacket(NetworkStackLatencyPacket.class);
        this.registerCodecPacket(UpdateSoftEnumPacket.class);
        this.registerCodecPacket(NetworkChunkPublisherUpdatePacket.class);
        this.registerCodecPacket(AvailableEntityIdentifiersPacket.class);
        this.registerCodecPacket(SpawnParticleEffectPacket.class);
        this.registerCodecPacket(BiomeDefinitionListPacket.class);
        this.registerCodecPacket(LevelSoundEventPacket.class);
        this.registerCodecPacket(LevelEventGenericPacket.class);
        this.registerCodecPacket(LecternUpdatePacket.class);
        this.registerCodecPacket(ClientCacheStatusPacket.class);
        this.registerCodecPacket(MapCreateLockedCopyPacket.class);
        this.registerCodecPacket(EmotePacket.class);
        this.registerCodecPacket(OnScreenTextureAnimationPacket.class);
        this.registerCodecPacket(CompletedUsingItemPacket.class);
        this.registerCodecPacket(CodeBuilderPacket.class);
        this.registerCodecPacket(PlayerAuthInputPacket.class);
        this.registerCodecPacket(CreativeContentPacket.class);
        this.registerCodecPacket(DebugInfoPacket.class);
        this.registerCodecPacket(EmoteListPacket.class);
        this.registerCodecPacket(ItemStackRequestPacket.class);
        this.registerCodecPacket(ItemStackResponsePacket.class);
        this.registerCodecPacket(PacketViolationWarningPacket.class);
        this.registerCodecPacket(PlayerArmorDamagePacket.class);
        this.registerCodecPacket(PlayerEnchantOptionsPacket.class);
        this.registerCodecPacket(PositionTrackingDBClientRequestPacket.class);
        this.registerCodecPacket(PositionTrackingDBServerBroadcastPacket.class);
        this.registerCodecPacket(UpdatePlayerGameTypePacket.class);
        this.registerCodecPacket(FilterTextPacket.class);
        this.registerCodecPacket(ToastRequestPacket.class);
        this.registerCodecPacket(AddVolumeEntityPacket.class);
        this.registerCodecPacket(RemoveVolumeEntityPacket.class);
        this.registerCodecPacket(SyncEntityPropertyPacket.class);
        this.registerCodecPacket(TickSyncPacket.class);
        this.registerCodecPacket(AnimateEntityPacket.class);
        this.registerCodecPacket(SimulationTypePacket.class);
        this.registerCodecPacket(ScriptMessagePacket.class);
        this.registerCodecPacket(CodeBuilderSourcePacket.class);
        this.registerCodecPacket(UpdateSubChunkBlocksPacket.class);
        this.registerCodecPacket(RequestPermissionsPacket.class);
        this.registerCodecPacket(CommandBlockUpdatePacket.class);
        this.registerCodecPacket(SetScorePacket.class);
        this.registerCodecPacket(SetDisplayObjectivePacket.class);
        this.registerCodecPacket(RemoveObjectivePacket.class);
        this.registerCodecPacket(SetScoreboardIdentityPacket.class);
        this.registerCodecPacket(CameraShakePacket.class);
        this.registerCodecPacket(DeathInfoPacket.class);
        this.registerCodecPacket(AgentActionEventPacket.class);
        this.registerCodecPacket(ChangeMobPropertyPacket.class);
        this.registerCodecPacket(DimensionDataPacket.class);
        this.registerCodecPacket(TickingAreasLoadStatusPacket.class);
        this.registerCodecPacket(LabTablePacket.class);
        this.registerCodecPacket(UpdateBlockSyncedPacket.class);
        this.registerCodecPacket(EduUriResourcePacket.class);
        this.registerCodecPacket(CreatePhotoPacket.class);
        this.registerCodecPacket(PhotoInfoRequestPacket.class);
        this.registerCodecPacket(LessonProgressPacket.class);
        this.registerCodecPacket(RequestAbilityPacket.class);
        this.registerCodecPacket(UpdateAbilitiesPacket.class);
        this.registerCodecPacket(RequestNetworkSettingsPacket.class);
        this.registerCodecPacket(NetworkSettingsPacket.class);
        this.registerCodecPacket(UpdateAdventureSettingsPacket.class);
        this.registerCodecPacket(UpdateClientInputLocksPacket.class);
        this.registerCodecPacket(PlayerFogPacket.class);
        this.registerCodecPacket(SetDefaultGameTypePacket.class);
        this.registerCodecPacket(StructureBlockUpdatePacket.class);
        this.registerCodecPacket(StructureTemplateDataRequestPacket.class);
        this.registerCodecPacket(StructureTemplateDataResponsePacket.class);
        this.registerCodecPacket(CameraPresetsPacket.class);
        this.registerCodecPacket(UnlockedRecipesPacket.class);
        this.registerCodecPacket(CameraInstructionPacket.class);
        this.registerCodecPacket(CompressedBiomeDefinitionListPacket.class);
        this.registerCodecPacket(TrimDataPacket.class);
        this.registerCodecPacket(OpenSignPacket.class);
        this.registerCodecPacket(AgentAnimationPacket.class);
        this.registerCodecPacket(ToggleCrafterSlotRequestPacket.class);
        this.registerCodecPacket(SetPlayerInventoryOptionsPacket.class);
        this.registerCodecPacket(SetHudPacket.class);
        this.registerCodecPacket(SettingsCommandPacket.class);
        this.registerCodecPacket(ClientboundCloseFormPacket.class);
        this.registerCodecPacket(ServerboundLoadingScreenPacket.class);
        this.registerCodecPacket(ServerboundDiagnosticsPacket.class);
        this.registerCodecPacket(ServerboundPackSettingChangePacket.class);
        this.registerCodecPacket(CameraAimAssistPresetsPacket.class);
        this.registerCodecPacket(UpdateClientOptionsPacket.class);
        this.registerCodecPacket(ClientboundControlSchemeSetPacket.class);
        this.registerCodecPacket(PlayerVideoCapturePacket.class);
        this.registerCodecPacket(PlayerUpdateEntityOverridesPacket.class);
        this.registerCodecPacket(PlayerLocationPacket.class);
        this.registerCodecPacket(ShowStoreOfferPacket.class);
        this.registerCodecPacket(ClientboundDataStorePacket.class);
        this.registerCodecPacket(ClientboundDataDrivenUIShowScreenPacket.class);
        this.registerCodecPacket(ClientboundDataDrivenUICloseAllScreensPacket.class);
        this.registerCodecPacket(ClientboundDataDrivenUIReloadPacket.class);
        this.registerCodecPacket(ClientboundTextureShiftPacket.class);
        this.registerCodecPacket(VoxelShapesPacket.class);
        this.registerCodecPacket(CameraAimAssistActorPriorityPacket.class);

        this.PACKET_POOL.trim();
    }
}
