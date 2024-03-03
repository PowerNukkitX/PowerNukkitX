package cn.nukkit.registry;

import cn.nukkit.network.protocol.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.sunlan.fastreflection.FastConstructor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import java.util.concurrent.atomic.AtomicBoolean;

public class PacketRegistry implements IRegistry<Integer, DataPacket, Class<? extends DataPacket>> {
    private final Int2ObjectOpenHashMap<FastConstructor<? extends DataPacket>> PACKET_POOL = new Int2ObjectOpenHashMap<>(256);
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);
    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        registerPackets();
    }

    @Override
    public DataPacket get(Integer key) {
        FastConstructor<? extends DataPacket> fastConstructor = PACKET_POOL.get(key);
        if (fastConstructor == null) {
            return null;
        } else {
            try {
                return (DataPacket) fastConstructor.invoke();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public DataPacket get(int key) {
        FastConstructor<? extends DataPacket> fastConstructor = PACKET_POOL.get(key);
        if (fastConstructor == null) {
            return null;
        } else {
            try {
                return (DataPacket) fastConstructor.invoke();
            } catch (Throwable e) {
                throw new RuntimeException(e);
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
     * Register a packet to the pool. Using from 1.19.70.
     *
     * @param id    The packet id, non-negative int
     * @param clazz The packet class
     */
    @Override
    public void register(Integer id, Class<? extends DataPacket> clazz) throws RegisterException {
        try {
            if (this.PACKET_POOL.putIfAbsent(id, FastConstructor.create(clazz.getConstructor())) != null) {
                throw new RegisterException("The packet has been registered!");
            }
        } catch (NoSuchMethodException e) {
            throw new RegisterException(e);
        }
    }

    private void register0(@Nonnegative int id, @NotNull Class<? extends DataPacket> clazz) {
        try {
            if (this.PACKET_POOL.putIfAbsent(id, FastConstructor.create(clazz.getConstructor())) != null) {
                throw new RegisterException("The packet has been registered!");
            }
        } catch (NoSuchMethodException | RegisterException ignored) {
        }
    }

    private void registerPackets() {
        this.PACKET_POOL.clear();

        this.register0(ProtocolInfo.SERVER_TO_CLIENT_HANDSHAKE_PACKET, ServerToClientHandshakePacket.class);
        this.register0(ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET, ClientToServerHandshakePacket.class);
        this.register0(ProtocolInfo.ADD_ENTITY_PACKET, AddEntityPacket.class);
        this.register0(ProtocolInfo.ADD_ITEM_ENTITY_PACKET, AddItemEntityPacket.class);
        this.register0(ProtocolInfo.ADD_PAINTING_PACKET, AddPaintingPacket.class);
        this.register0(ProtocolInfo.ADD_PLAYER_PACKET, AddPlayerPacket.class);
        this.register0(ProtocolInfo.ADVENTURE_SETTINGS_PACKET, AdventureSettingsPacket.class);
        this.register0(ProtocolInfo.ANIMATE_PACKET, AnimatePacket.class);
        this.register0(ProtocolInfo.ANVIL_DAMAGE_PACKET, AnvilDamagePacket.class);
        this.register0(ProtocolInfo.AVAILABLE_COMMANDS_PACKET, AvailableCommandsPacket.class);
        this.register0(ProtocolInfo.BLOCK_ENTITY_DATA_PACKET, BlockEntityDataPacket.class);
        this.register0(ProtocolInfo.BLOCK_EVENT_PACKET, BlockEventPacket.class);
        this.register0(ProtocolInfo.BLOCK_PICK_REQUEST_PACKET, BlockPickRequestPacket.class);
        this.register0(ProtocolInfo.BOOK_EDIT_PACKET, BookEditPacket.class);
        this.register0(ProtocolInfo.BOSS_EVENT_PACKET, BossEventPacket.class);
        this.register0(ProtocolInfo.CHANGE_DIMENSION_PACKET, ChangeDimensionPacket.class);
        this.register0(ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET, ChunkRadiusUpdatedPacket.class);
        this.register0(ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET, ClientboundMapItemDataPacket.class);
        this.register0(ProtocolInfo.COMMAND_REQUEST_PACKET, CommandRequestPacket.class);
        this.register0(ProtocolInfo.CONTAINER_CLOSE_PACKET, ContainerClosePacket.class);
        this.register0(ProtocolInfo.CONTAINER_OPEN_PACKET, ContainerOpenPacket.class);
        this.register0(ProtocolInfo.CONTAINER_SET_DATA_PACKET, ContainerSetDataPacket.class);
        this.register0(ProtocolInfo.CRAFTING_DATA_PACKET, CraftingDataPacket.class);
        this.register0(ProtocolInfo.CRAFTING_EVENT_PACKET, CraftingEventPacket.class);
        this.register0(ProtocolInfo.DISCONNECT_PACKET, DisconnectPacket.class);
        this.register0(ProtocolInfo.ENTITY_EVENT_PACKET, EntityEventPacket.class);
        this.register0(ProtocolInfo.ENTITY_FALL_PACKET, EntityFallPacket.class);
        this.register0(ProtocolInfo.FULL_CHUNK_DATA_PACKET, LevelChunkPacket.class);
        this.register0(ProtocolInfo.GAME_RULES_CHANGED_PACKET, GameRulesChangedPacket.class);
        this.register0(ProtocolInfo.HURT_ARMOR_PACKET, HurtArmorPacket.class);
        this.register0(ProtocolInfo.INTERACT_PACKET, InteractPacket.class);
        this.register0(ProtocolInfo.INVENTORY_CONTENT_PACKET, InventoryContentPacket.class);
        this.register0(ProtocolInfo.INVENTORY_SLOT_PACKET, InventorySlotPacket.class);
        this.register0(ProtocolInfo.INVENTORY_TRANSACTION_PACKET, InventoryTransactionPacket.class);
        this.register0(ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET, ItemFrameDropItemPacket.class);
        this.register0(ProtocolInfo.LEVEL_EVENT_PACKET, LevelEventPacket.class);
        this.register0(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1, LevelSoundEventPacketV1.class);
        this.register0(ProtocolInfo.LOGIN_PACKET, LoginPacket.class);
        this.register0(ProtocolInfo.MAP_INFO_REQUEST_PACKET, MapInfoRequestPacket.class);
        this.register0(ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET, MobArmorEquipmentPacket.class);
        this.register0(ProtocolInfo.MOB_EQUIPMENT_PACKET, MobEquipmentPacket.class);
        this.register0(ProtocolInfo.MODAL_FORM_REQUEST_PACKET, ModalFormRequestPacket.class);
        this.register0(ProtocolInfo.MODAL_FORM_RESPONSE_PACKET, ModalFormResponsePacket.class);
        this.register0(ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET, MoveEntityAbsolutePacket.class);
        this.register0(ProtocolInfo.MOVE_PLAYER_PACKET, MovePlayerPacket.class);
        this.register0(ProtocolInfo.PLAYER_ACTION_PACKET, PlayerActionPacket.class);
        this.register0(ProtocolInfo.PLAYER_INPUT_PACKET, PlayerInputPacket.class);
        this.register0(ProtocolInfo.PLAYER_LIST_PACKET, PlayerListPacket.class);
        this.register0(ProtocolInfo.PLAYER_HOTBAR_PACKET, PlayerHotbarPacket.class);
        this.register0(ProtocolInfo.PLAY_SOUND_PACKET, PlaySoundPacket.class);
        this.register0(ProtocolInfo.PLAY_STATUS_PACKET, PlayStatusPacket.class);
        this.register0(ProtocolInfo.REMOVE_ENTITY_PACKET, RemoveEntityPacket.class);
        this.register0(ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET, RequestChunkRadiusPacket.class);
        this.register0(ProtocolInfo.RESOURCE_PACKS_INFO_PACKET, ResourcePacksInfoPacket.class);
        this.register0(ProtocolInfo.RESOURCE_PACK_STACK_PACKET, ResourcePackStackPacket.class);
        this.register0(ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET, ResourcePackClientResponsePacket.class);
        this.register0(ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET, ResourcePackDataInfoPacket.class);
        this.register0(ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET, ResourcePackChunkDataPacket.class);
        this.register0(ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET, ResourcePackChunkRequestPacket.class);
        this.register0(ProtocolInfo.PLAYER_SKIN_PACKET, PlayerSkinPacket.class);
        this.register0(ProtocolInfo.RESPAWN_PACKET, RespawnPacket.class);
        this.register0(ProtocolInfo.RIDER_JUMP_PACKET, RiderJumpPacket.class);
        this.register0(ProtocolInfo.SET_COMMANDS_ENABLED_PACKET, SetCommandsEnabledPacket.class);
        this.register0(ProtocolInfo.SET_DIFFICULTY_PACKET, SetDifficultyPacket.class);
        this.register0(ProtocolInfo.SET_ENTITY_DATA_PACKET, SetEntityDataPacket.class);
        this.register0(ProtocolInfo.SET_ENTITY_LINK_PACKET, SetEntityLinkPacket.class);
        this.register0(ProtocolInfo.SET_ENTITY_MOTION_PACKET, SetEntityMotionPacket.class);
        this.register0(ProtocolInfo.SET_HEALTH_PACKET, SetHealthPacket.class);
        this.register0(ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET, SetPlayerGameTypePacket.class);
        this.register0(ProtocolInfo.SET_SPAWN_POSITION_PACKET, SetSpawnPositionPacket.class);
        this.register0(ProtocolInfo.SET_TITLE_PACKET, SetTitlePacket.class);
        this.register0(ProtocolInfo.SET_TIME_PACKET, SetTimePacket.class);
        this.register0(ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET, ServerSettingsRequestPacket.class);
        this.register0(ProtocolInfo.SERVER_SETTINGS_RESPONSE_PACKET, ServerSettingsResponsePacket.class);
        this.register0(ProtocolInfo.SHOW_CREDITS_PACKET, ShowCreditsPacket.class);
        this.register0(ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET, SpawnExperienceOrbPacket.class);
        this.register0(ProtocolInfo.START_GAME_PACKET, StartGamePacket.class);
        this.register0(ProtocolInfo.TAKE_ITEM_ENTITY_PACKET, TakeItemEntityPacket.class);
        this.register0(ProtocolInfo.TEXT_PACKET, TextPacket.class);
        this.register0(ProtocolInfo.SERVER_POST_MOVE_POSITION, ServerPostMovePositionPacket.class);
        this.register0(ProtocolInfo.UPDATE_ATTRIBUTES_PACKET, UpdateAttributesPacket.class);
        this.register0(ProtocolInfo.UPDATE_BLOCK_PACKET, UpdateBlockPacket.class);
        this.register0(ProtocolInfo.UPDATE_TRADE_PACKET, UpdateTradePacket.class);
        this.register0(ProtocolInfo.MOVE_ENTITY_DELTA_PACKET, MoveEntityDeltaPacket.class);
        this.register0(ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET, SetLocalPlayerAsInitializedPacket.class);
        this.register0(ProtocolInfo.NETWORK_STACK_LATENCY_PACKET, NetworkStackLatencyPacket.class);
        this.register0(ProtocolInfo.UPDATE_SOFT_ENUM_PACKET, UpdateSoftEnumPacket.class);
        this.register0(ProtocolInfo.NETWORK_CHUNK_PUBLISHER_UPDATE_PACKET, NetworkChunkPublisherUpdatePacket.class);
        this.register0(ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET, AvailableEntityIdentifiersPacket.class);
        this.register0(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2, LevelSoundEventPacket.class);
//        this.registerPacket(ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET, ScriptCustomEventPacket.class); // deprecated since 1.20.10
        this.register0(ProtocolInfo.SPAWN_PARTICLE_EFFECT_PACKET, SpawnParticleEffectPacket.class);
        this.register0(ProtocolInfo.BIOME_DEFINITION_LIST_PACKET, BiomeDefinitionListPacket.class);
        this.register0(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET, LevelSoundEventPacket.class);
        this.register0(ProtocolInfo.LEVEL_EVENT_GENERIC_PACKET, LevelEventGenericPacket.class);
        this.register0(ProtocolInfo.LECTERN_UPDATE_PACKET, LecternUpdatePacket.class);
        this.register0(ProtocolInfo.VIDEO_STREAM_CONNECT_PACKET, VideoStreamConnectPacket.class);
        this.register0(ProtocolInfo.CLIENT_CACHE_STATUS_PACKET, ClientCacheStatusPacket.class);
        this.register0(ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET, MapCreateLockedCopyPacket.class);
        this.register0(ProtocolInfo.EMOTE_PACKET, EmotePacket.class);
        this.register0(ProtocolInfo.ON_SCREEN_TEXTURE_ANIMATION_PACKET, OnScreenTextureAnimationPacket.class);
        this.register0(ProtocolInfo.COMPLETED_USING_ITEM_PACKET, CompletedUsingItemPacket.class);
        this.register0(ProtocolInfo.CODE_BUILDER_PACKET, CodeBuilderPacket.class);
        this.register0(ProtocolInfo.PLAYER_AUTH_INPUT_PACKET, PlayerAuthInputPacket.class);
        this.register0(ProtocolInfo.CREATIVE_CONTENT_PACKET, CreativeContentPacket.class);
        this.register0(ProtocolInfo.DEBUG_INFO_PACKET, DebugInfoPacket.class);
        this.register0(ProtocolInfo.EMOTE_LIST_PACKET, EmoteListPacket.class);
        this.register0(ProtocolInfo.ITEM_STACK_REQUEST_PACKET, ItemStackRequestPacket.class);
        this.register0(ProtocolInfo.ITEM_STACK_RESPONSE_PACKET, ItemStackResponsePacket.class);
        this.register0(ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET, PacketViolationWarningPacket.class);
        this.register0(ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET, PlayerArmorDamagePacket.class);
        this.register0(ProtocolInfo.PLAYER_ENCHANT_OPTIONS_PACKET, PlayerEnchantOptionsPacket.class);
        this.register0(ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET, PositionTrackingDBClientRequestPacket.class);
        this.register0(ProtocolInfo.POS_TRACKING_SERVER_BROADCAST_PACKET, PositionTrackingDBServerBroadcastPacket.class);
        this.register0(ProtocolInfo.UPDATE_PLAYER_GAME_TYPE_PACKET, UpdatePlayerGameTypePacket.class);
        this.register0(ProtocolInfo.FILTER_TEXT_PACKET, FilterTextPacket.class);
        this.register0(ProtocolInfo.TOAST_REQUEST_PACKET, ToastRequestPacket.class);
        this.register0(ProtocolInfo.ITEM_COMPONENT_PACKET, ItemComponentPacket.class);
        this.register0(ProtocolInfo.ADD_VOLUME_ENTITY_PACKET, AddVolumeEntityPacket.class);
        this.register0(ProtocolInfo.REMOVE_VOLUME_ENTITY_PACKET, RemoveVolumeEntityPacket.class);
        this.register0(ProtocolInfo.SYNC_ENTITY_PROPERTY_PACKET, SyncEntityPropertyPacket.class);
        this.register0(ProtocolInfo.TICK_SYNC_PACKET, TickSyncPacket.class);
        this.register0(ProtocolInfo.ANIMATE_ENTITY_PACKET, AnimateEntityPacket.class);
        this.register0(ProtocolInfo.NPC_DIALOGUE_PACKET, NPCDialoguePacket.class);
        this.register0(ProtocolInfo.NPC_REQUEST_PACKET, NPCRequestPacket.class);
        this.register0(ProtocolInfo.SIMULATION_TYPE_PACKET, SimulationTypePacket.class);
        this.register0(ProtocolInfo.SCRIPT_MESSAGE_PACKET, ScriptMessagePacket.class);
        this.register0(ProtocolInfo.PLAYER_START_ITEM_COOL_DOWN_PACKET, PlayerStartItemCoolDownPacket.class);
        this.register0(ProtocolInfo.CODE_BUILDER_SOURCE_PACKET, CodeBuilderSourcePacket.class);
        this.register0(ProtocolInfo.UPDATE_SUB_CHUNK_BLOCKS_PACKET, UpdateSubChunkBlocksPacket.class);
        //powernukkitx only
        this.register0(ProtocolInfo.REQUEST_PERMISSIONS_PACKET, RequestPermissionsPacket.class);
        this.register0(ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET, CommandBlockUpdatePacket.class);
        this.register0(ProtocolInfo.SET_SCORE_PACKET, SetScorePacket.class);
        this.register0(ProtocolInfo.SET_DISPLAY_OBJECTIVE_PACKET, SetDisplayObjectivePacket.class);
        this.register0(ProtocolInfo.REMOVE_OBJECTIVE_PACKET, RemoveObjectivePacket.class);
        this.register0(ProtocolInfo.SET_SCOREBOARD_IDENTITY_PACKET, SetScoreboardIdentityPacket.class);
        this.register0(ProtocolInfo.CAMERA_SHAKE_PACKET, CameraShakePacket.class);
        this.register0(ProtocolInfo.DEATH_INFO_PACKET, DeathInfoPacket.class);
        this.register0(ProtocolInfo.AGENT_ACTION_EVENT_PACKET, AgentActionEventPacket.class);
        this.register0(ProtocolInfo.CHANGE_MOB_PROPERTY_PACKET, ChangeMobPropertyPacket.class);
        this.register0(ProtocolInfo.DIMENSION_DATA_PACKET, DimensionDataPacket.class);
        this.register0(ProtocolInfo.TICKING_AREAS_LOAD_STATUS_PACKET, TickingAreasLoadStatusPacket.class);
        this.register0(ProtocolInfo.LAB_TABLE_PACKET, LabTablePacket.class);
        this.register0(ProtocolInfo.UPDATE_BLOCK_SYNCED_PACKET, UpdateBlockSyncedPacket.class);
        this.register0(ProtocolInfo.EDU_URI_RESOURCE_PACKET, EduUriResourcePacket.class);
        this.register0(ProtocolInfo.CREATE_PHOTO_PACKET, CreatePhotoPacket.class);
        this.register0(ProtocolInfo.PHOTO_INFO_REQUEST_PACKET, PhotoInfoRequestPacket.class);
        this.register0(ProtocolInfo.LESSON_PROGRESS_PACKET, LessonProgressPacket.class);
        this.register0(ProtocolInfo.REQUEST_ABILITY_PACKET, RequestAbilityPacket.class);
        this.register0(ProtocolInfo.UPDATE_ABILITIES_PACKET, UpdateAbilitiesPacket.class);
        this.register0(ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET, RequestNetworkSettingsPacket.class);
        this.register0(ProtocolInfo.NETWORK_SETTINGS_PACKET, NetworkSettingsPacket.class);
        this.register0(ProtocolInfo.UPDATE_ADVENTURE_SETTINGS_PACKET, UpdateAdventureSettingsPacket.class);
        this.register0(ProtocolInfo.UPDATE_CLIENT_INPUT_LOCKS, UpdateClientInputLocksPacket.class);
        this.register0(ProtocolInfo.PLAYER_FOG_PACKET, PlayerFogPacket.class);
        this.register0(ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET, SetDefaultGameTypePacket.class);
        this.register0(ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET, StructureBlockUpdatePacket.class);
        // new packet id system
        this.register0(ProtocolInfo.CAMERA_PRESETS_PACKET, CameraPresetsPacket.class);
        this.register0(ProtocolInfo.UNLOCKED_RECIPES_PACKET, UnlockedRecipesPacket.class);
        this.register0(ProtocolInfo.CAMERA_INSTRUCTION_PACKET, CameraInstructionPacket.class);
        this.register0(ProtocolInfo.COMPRESSED_BIOME_DEFINITIONS_LIST, CompressedBiomeDefinitionListPacket.class);
        this.register0(ProtocolInfo.TRIM_DATA, TrimDataPacket.class);
        this.register0(ProtocolInfo.OPEN_SIGN, OpenSignPacket.class);
        this.register0(ProtocolInfo.AGENT_ANIMATION, AgentAnimationPacket.class);
        this.register0(ProtocolInfo.TOGGLE_CRAFTER_SLOT_REQUEST, ToggleCrafterSlotRequestPacket.class);
        this.register0(ProtocolInfo.SET_PLAYER_INVENTORY_OPTIONS_PACKET, SetPlayerInventoryOptionsPacket.class);
        this.register0(ProtocolInfo.SET_HUD, SetHudPacket.class);
        this.PACKET_POOL.trim();
    }
}
