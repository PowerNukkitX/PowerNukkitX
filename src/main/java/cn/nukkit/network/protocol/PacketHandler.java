package cn.nukkit.network.protocol;

public interface PacketHandler {
    default void handle(AddBehaviorTreePacket pk) {
    }

    default void handle(AddEntityPacket pk) {
    }

    default void handle(AddItemEntityPacket pk) {
    }

    default void handle(AddPaintingPacket pk) {
    }

    default void handle(AddPlayerPacket pk) {
    }

    default void handle(AddVolumeEntityPacket pk) {
    }

    default void handle(AdventureSettingsPacket pk) {
    }

    default void handle(AgentActionEventPacket pk) {
    }

    default void handle(AgentAnimationPacket pk) {
    }

    default void handle(AnimateEntityPacket pk) {
    }

    default void handle(AnimatePacket pk) {
    }

    default void handle(AnvilDamagePacket pk) {
    }

    default void handle(AvailableCommandsPacket pk) {
    }

    default void handle(AvailableEntityIdentifiersPacket pk) {
    }

    default void handle(BiomeDefinitionListPacket pk) {
    }

    default void handle(BlockEntityDataPacket pk) {
    }

    default void handle(BlockEventPacket pk) {
    }

    default void handle(BlockPickRequestPacket pk) {
    }

    default void handle(BookEditPacket pk) {
    }

    default void handle(BossEventPacket pk) {
    }

    default void handle(CameraInstructionPacket pk) {
    }

    default void handle(CameraPacket pk) {
    }

    default void handle(CameraPresetsPacket pk) {
    }

    default void handle(CameraShakePacket pk) {
    }

    default void handle(ChangeDimensionPacket pk) {
    }

    default void handle(ChangeMobPropertyPacket pk) {
    }

    default void handle(ChunkRadiusUpdatedPacket pk) {
    }

    default void handle(ClientCacheStatusPacket pk) {
    }

    default void handle(ClientToServerHandshakePacket pk) {
    }

    default void handle(ClientboundMapItemDataPacket pk) {
    }

    default void handle(CodeBuilderPacket pk) {
    }

    default void handle(CodeBuilderSourcePacket pk) {
    }

    default void handle(CommandBlockUpdatePacket pk) {
    }

    default void handle(CommandOutputPacket pk) {
    }

    default void handle(CommandRequestPacket pk) {
    }

    default void handle(CompletedUsingItemPacket pk) {
    }

    default void handle(CompressedBiomeDefinitionListPacket pk) {
    }

    default void handle(ContainerClosePacket pk) {
    }

    default void handle(ContainerOpenPacket pk) {
    }

    default void handle(ContainerSetDataPacket pk) {
    }

    default void handle(CraftingDataPacket pk) {
    }

    default void handle(CraftingEventPacket pk) {
    }

    default void handle(CreatePhotoPacket pk) {
    }

    default void handle(CreativeContentPacket pk) {
    }

    default void handle(DeathInfoPacket pk) {
    }

    default void handle(DebugInfoPacket pk) {
    }

    default void handle(DimensionDataPacket pk) {
    }

    default void handle(DisconnectPacket pk) {
    }

    default void handle(EduUriResourcePacket pk) {
    }

    default void handle(EmoteListPacket pk) {
    }

    default void handle(EmotePacket pk) {
    }

    default void handle(EntityEventPacket pk) {
    }

    default void handle(EntityFallPacket pk) {
    }

    default void handle(EntityPickRequestPacket pk) {
    }

    default void handle(EventPacket pk) {
    }

    default void handle(FilterTextPacket pk) {
    }

    default void handle(GUIDataPickItemPacket pk) {
    }

    default void handle(GameRulesChangedPacket pk) {
    }

    default void handle(HurtArmorPacket pk) {
    }

    default void handle(InitiateWebSocketConnectionPacket pk) {
    }

    default void handle(InteractPacket pk) {
    }

    default void handle(InventoryContentPacket pk) {
    }

    default void handle(InventorySlotPacket pk) {
    }

    default void handle(InventoryTransactionPacket pk) {
    }

    default void handle(ItemComponentPacket pk) {
    }

    default void handle(ItemFrameDropItemPacket pk) {
    }

    default void handle(ItemStackRequestPacket pk) {
    }

    default void handle(ItemStackResponsePacket pk) {
    }

    default void handle(LabTablePacket pk) {
    }

    default void handle(LecternUpdatePacket pk) {
    }

    default void handle(LessonProgressPacket pk) {
    }

    default void handle(LevelChunkPacket pk) {
    }

    default void handle(LevelEventGenericPacket pk) {
    }

    default void handle(LevelEventPacket pk) {
    }

    default void handle(LevelSoundEventPacket pk) {
    }

    default void handle(LoginPacket pk) {
    }

    default void handle(MapCreateLockedCopyPacket pk) {
    }

    default void handle(MapInfoRequestPacket pk) {
    }

    default void handle(MobArmorEquipmentPacket pk) {
    }

    default void handle(MobEffectPacket pk) {
    }

    default void handle(MobEquipmentPacket pk) {
    }

    default void handle(ModalFormRequestPacket pk) {
    }

    default void handle(ModalFormResponsePacket pk) {
    }

    default void handle(MoveEntityAbsolutePacket pk) {
    }

    default void handle(MoveEntityDeltaPacket pk) {
    }

    default void handle(MovePlayerPacket pk) {
    }

    default void handle(NPCDialoguePacket pk) {
    }

    default void handle(NPCRequestPacket pk) {
    }

    default void handle(NetworkChunkPublisherUpdatePacket pk) {
    }

    default void handle(NetworkSettingsPacket pk) {
    }

    default void handle(NetworkStackLatencyPacket pk) {
    }

    default void handle(OnScreenTextureAnimationPacket pk) {
    }

    default void handle(OpenSignPacket pk) {
    }

    default void handle(PacketViolationWarningPacket pk) {
    }

    default void handle(PhotoInfoRequestPacket pk) {
    }

    default void handle(PlaySoundPacket pk) {
    }

    default void handle(PlayStatusPacket pk) {
    }

    default void handle(PlayerActionPacket pk) {
    }

    default void handle(PlayerArmorDamagePacket pk) {
    }

    default void handle(PlayerAuthInputPacket pk) {
    }

    default void handle(PlayerEnchantOptionsPacket pk) {
    }

    default void handle(PlayerFogPacket pk) {
    }

    default void handle(PlayerHotbarPacket pk) {
    }

    default void handle(PlayerInputPacket pk) {
    }

    default void handle(PlayerListPacket pk) {
    }

    default void handle(PlayerSkinPacket pk) {
    }

    default void handle(PlayerStartItemCoolDownPacket pk) {
    }

    default void handle(PositionTrackingDBClientRequestPacket pk) {
    }

    default void handle(PositionTrackingDBServerBroadcastPacket pk) {
    }

    default void handle(RemoveEntityPacket pk) {
    }

    default void handle(RemoveObjectivePacket pk) {
    }

    default void handle(RemoveVolumeEntityPacket pk) {
    }

    default void handle(RequestAbilityPacket pk) {
    }

    default void handle(RequestChunkRadiusPacket pk) {
    }

    default void handle(RequestNetworkSettingsPacket pk) {
    }

    default void handle(RequestPermissionsPacket pk) {
    }

    default void handle(ResourcePackChunkDataPacket pk) {
    }

    default void handle(ResourcePackChunkRequestPacket pk) {
    }

    default void handle(ResourcePackClientResponsePacket pk) {
    }

    default void handle(ResourcePackDataInfoPacket pk) {
    }

    default void handle(ResourcePackStackPacket pk) {
    }

    default void handle(ResourcePacksInfoPacket pk) {
    }

    default void handle(RespawnPacket pk) {
    }

    default void handle(RiderJumpPacket pk) {
    }

    default void handle(ScriptCustomEventPacket pk) {
    }

    default void handle(ScriptMessagePacket pk) {
    }

    default void handle(ServerPostMovePositionPacket pk) {
    }

    default void handle(ServerSettingsRequestPacket pk) {
    }

    default void handle(ServerSettingsResponsePacket pk) {
    }

    default void handle(ServerToClientHandshakePacket pk) {
    }

    default void handle(SetCommandsEnabledPacket pk) {
    }

    default void handle(SetDefaultGameTypePacket pk) {
    }

    default void handle(SetDifficultyPacket pk) {
    }

    default void handle(SetDisplayObjectivePacket pk) {
    }

    default void handle(SetEntityDataPacket pk) {
    }

    default void handle(SetEntityLinkPacket pk) {
    }

    default void handle(SetEntityMotionPacket pk) {
    }

    default void handle(SetHealthPacket pk) {
    }

    default void handle(SetHudPacket pk) {
    }

    default void handle(SetLastHurtByPacket pk) {
    }

    default void handle(SetLocalPlayerAsInitializedPacket pk) {
    }

    default void handle(SetPlayerGameTypePacket pk) {
    }

    default void handle(SetPlayerInventoryOptionsPacket pk) {
    }

    default void handle(SetScorePacket pk) {
    }

    default void handle(SetScoreboardIdentityPacket pk) {
    }

    default void handle(SetSpawnPositionPacket pk) {
    }

    default void handle(SetTimePacket pk) {
    }

    default void handle(SetTitlePacket pk) {
    }

    default void handle(ShowCreditsPacket pk) {
    }

    default void handle(ShowProfilePacket pk) {
    }

    default void handle(SimpleEventPacket pk) {
    }

    default void handle(SimulationTypePacket pk) {
    }

    default void handle(SpawnExperienceOrbPacket pk) {
    }

    default void handle(SpawnParticleEffectPacket pk) {
    }

    default void handle(StartGamePacket pk) {
    }

    default void handle(StopSoundPacket pk) {
    }

    default void handle(StructureBlockUpdatePacket pk) {
    }

    default void handle(SubClientLoginPacket pk) {
    }

    default void handle(SyncEntityPropertyPacket pk) {
    }

    default void handle(TakeItemEntityPacket pk) {
    }

    default void handle(TextPacket pk) {
    }

    default void handle(TickSyncPacket pk) {
    }

    default void handle(TickingAreasLoadStatusPacket pk) {
    }

    default void handle(ToastRequestPacket pk) {
    }

    default void handle(ToggleCrafterSlotRequestPacket pk) {
    }

    default void handle(TransferPacket pk) {
    }

    default void handle(TrimDataPacket pk) {
    }

    default void handle(UnlockedRecipesPacket pk) {
    }

    default void handle(UpdateAbilitiesPacket pk) {
    }

    default void handle(UpdateAdventureSettingsPacket pk) {
    }

    default void handle(UpdateAttributesPacket pk) {
    }

    default void handle(UpdateBlockPacket pk) {
    }

    default void handle(UpdateBlockSyncedPacket pk) {
    }

    default void handle(UpdateClientInputLocksPacket pk) {
    }

    default void handle(UpdateEquipmentPacket pk) {
    }

    default void handle(UpdatePlayerGameTypePacket pk) {
    }

    default void handle(UpdateSoftEnumPacket pk) {
    }

    default void handle(UpdateSubChunkBlocksPacket pk) {
    }

    default void handle(UpdateTradePacket pk) {
    }

    default void handle(VideoStreamConnectPacket pk) {
    }
}
