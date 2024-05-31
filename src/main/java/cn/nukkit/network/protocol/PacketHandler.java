package cn.nukkit.network.protocol;

public interface PacketHandler {
    default 
    /**
     * @deprecated 
     */
    void handle(AddBehaviorTreePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AddEntityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AddItemEntityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AddPaintingPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AddPlayerPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AddVolumeEntityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AdventureSettingsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AgentActionEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AgentAnimationPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AnimateEntityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AnimatePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AnvilDamagePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AvailableCommandsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(AvailableEntityIdentifiersPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(BiomeDefinitionListPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(BlockEntityDataPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(BlockEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(BlockPickRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(BookEditPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(BossEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CameraInstructionPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CameraPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CameraPresetsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CameraShakePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ChangeDimensionPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ChangeMobPropertyPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ChunkRadiusUpdatedPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ClientCacheStatusPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ClientToServerHandshakePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ClientboundMapItemDataPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CodeBuilderPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CodeBuilderSourcePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CommandBlockUpdatePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CommandOutputPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CommandRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CompletedUsingItemPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CompressedBiomeDefinitionListPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ContainerClosePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ContainerOpenPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ContainerSetDataPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CraftingDataPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CraftingEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CreatePhotoPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(CreativeContentPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(DeathInfoPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(DebugInfoPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(DimensionDataPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(DisconnectPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(EduUriResourcePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(EmoteListPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(EmotePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(EntityEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(EntityFallPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(EntityPickRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(EventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(FilterTextPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(GUIDataPickItemPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(GameRulesChangedPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(HurtArmorPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(InitiateWebSocketConnectionPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(InteractPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(InventoryContentPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(InventorySlotPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(InventoryTransactionPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ItemComponentPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ItemFrameDropItemPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ItemStackRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ItemStackResponsePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(LabTablePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(LecternUpdatePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(LessonProgressPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(LevelChunkPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(LevelEventGenericPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(LevelEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(LevelSoundEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(LoginPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(MapCreateLockedCopyPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(MapInfoRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(MobArmorEquipmentPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(MobEffectPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(MobEquipmentPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ModalFormRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ModalFormResponsePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(MoveEntityAbsolutePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(MoveEntityDeltaPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(MovePlayerPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(NPCDialoguePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(NPCRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(NetworkChunkPublisherUpdatePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(NetworkSettingsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(NetworkStackLatencyPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(OnScreenTextureAnimationPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(OpenSignPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PacketViolationWarningPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PhotoInfoRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlaySoundPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayStatusPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerActionPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerArmorDamagePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerAuthInputPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerEnchantOptionsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerFogPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerHotbarPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerInputPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerListPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerSkinPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PlayerStartItemCoolDownPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PositionTrackingDBClientRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(PositionTrackingDBServerBroadcastPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RemoveEntityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RemoveObjectivePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RemoveVolumeEntityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RequestAbilityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RequestChunkRadiusPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RequestNetworkSettingsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RequestPermissionsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ResourcePackChunkDataPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ResourcePackChunkRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ResourcePackClientResponsePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ResourcePackDataInfoPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ResourcePackStackPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ResourcePacksInfoPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RespawnPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(RiderJumpPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ScriptCustomEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ScriptMessagePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ServerPostMovePositionPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ServerSettingsRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ServerSettingsResponsePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ServerToClientHandshakePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetCommandsEnabledPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetDefaultGameTypePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetDifficultyPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetDisplayObjectivePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetEntityDataPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetEntityLinkPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetEntityMotionPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetHealthPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetHudPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetLastHurtByPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetLocalPlayerAsInitializedPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetPlayerGameTypePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetPlayerInventoryOptionsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetScorePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetScoreboardIdentityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetSpawnPositionPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetTimePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SetTitlePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ShowCreditsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ShowProfilePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SimpleEventPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SimulationTypePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SpawnExperienceOrbPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SpawnParticleEffectPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(StartGamePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(StopSoundPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(StructureBlockUpdatePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SubClientLoginPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(SyncEntityPropertyPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(TakeItemEntityPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(TextPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(TickSyncPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(TickingAreasLoadStatusPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ToastRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(ToggleCrafterSlotRequestPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(TransferPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(TrimDataPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UnlockedRecipesPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateAbilitiesPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateAdventureSettingsPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateAttributesPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateBlockPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateBlockSyncedPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateClientInputLocksPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateEquipmentPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdatePlayerGameTypePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateSoftEnumPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateSubChunkBlocksPacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(UpdateTradePacket pk) {
    }

    default 
    /**
     * @deprecated 
     */
    void handle(VideoStreamConnectPacket pk) {
    }
}
