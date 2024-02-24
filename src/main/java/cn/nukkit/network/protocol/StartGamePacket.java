package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.level.GameRules;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.registry.Registries;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @since 15-10-13
 */
@Slf4j
@ToString
public class StartGamePacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.START_GAME_PACKET;

    public static final int GAME_PUBLISH_SETTING_NO_MULTI_PLAY = 0;
    public static final int GAME_PUBLISH_SETTING_INVITE_ONLY = 1;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_ONLY = 2;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_OF_FRIENDS = 3;
    public static final int GAME_PUBLISH_SETTING_PUBLIC = 4;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public int playerGamemode;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float pitch;
    public long seed;
    public byte dimension;
    public int generator = 1;
    public int worldGamemode;
    public int difficulty;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public boolean hasAchievementsDisabled = true;

    public boolean worldEditor;
    public int dayCycleStopTime = -1; //-1 = not stopped, any positive value = stopped at that time
    public int eduEditionOffer = 0;
    public boolean hasEduFeaturesEnabled = false;
    public float rainLevel;
    public float lightningLevel;
    public boolean hasConfirmedPlatformLockedContent = false;
    public boolean multiplayerGame = true;
    public boolean broadcastToLAN = true;
    public int xblBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC;
    public int platformBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC;
    public boolean commandsEnabled;
    public boolean isTexturePacksRequired = false;

    public GameRules gameRules;
    public boolean bonusChest = false;
    public boolean hasStartWithMapEnabled = false;

    public int permissionLevel = 1;
    public int serverChunkTickRange = 4;
    public boolean hasLockedBehaviorPack = false;
    public boolean hasLockedResourcePack = false;
    public boolean isFromLockedWorldTemplate = false;
    public boolean isUsingMsaGamertagsOnly = false;
    public boolean isFromWorldTemplate = false;
    public boolean isWorldTemplateOptionLocked = false;
    public boolean isOnlySpawningV1Villagers = false;

    public String vanillaVersion = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    //HACK: For now we can specify this version, since the new chunk changes are not relevant for our Anvil format.
    //However, it could be that Microsoft will prevent this in a new update.

    public CompoundTag playerPropertyData = new CompoundTag();

    public String levelId = ""; //base64 string, usually the same as world folder name in vanilla
    public String worldName;
    public String premiumWorldTemplateId = "";
    public boolean isTrial = false;
    @Deprecated
    public boolean isMovementServerAuthoritative;


    public Integer serverAuthoritativeMovement;

    public boolean isInventoryServerAuthoritative;

    public long currentTick;

    public int enchantmentSeed;

    public final List<CustomBlockDefinition> blockProperties = new ArrayList<>();

    public String multiplayerCorrelationId = "";

    public boolean isDisablingPersonas;

    public boolean isDisablingCustomSkins;

    public boolean clientSideGenerationEnabled;
    /**
     * @since v567
     */
    public boolean emoteChatMuted;

    /**
     * Whether block runtime IDs should be replaced by 32-bit integer hashes of the NBT block state.
     * Unlike runtime IDs, this hashes should be persistent across versions and should make support for data-driven/custom blocks easier.
     *
     * @since v582
     */
    public boolean blockNetworkIdsHashed;
    /**
     * @since v582
     */
    public boolean createdInEditor;
    /**
     * @since v582
     */
    public boolean exportedFromEditor;
    public byte chatRestrictionLevel;
    public boolean disablePlayerInteractions;
    /**
     * @since v589
     */
    public boolean isSoundsServerAuthoritative;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeEntityUniqueId(this.entityUniqueId);
        byteBuf.writeEntityRuntimeId(this.entityRuntimeId);
        byteBuf.writeVarInt(this.playerGamemode);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeFloatLE(this.yaw);
        byteBuf.writeFloatLE(this.pitch);
        /* Level settings start */
        byteBuf.writeLongLE(this.seed);
        byteBuf.writeShortLE(0x00); // SpawnBiomeType - Default
        byteBuf.writeString("plains"); // UserDefinedBiomeName
        byteBuf.writeVarInt(this.dimension);
        byteBuf.writeVarInt(this.generator);
        byteBuf.writeVarInt(this.worldGamemode);
        byteBuf.writeVarInt(this.difficulty);
        byteBuf.writeBlockVector3(this.spawnX, this.spawnY, this.spawnZ);
        byteBuf.writeBoolean(this.hasAchievementsDisabled);
        byteBuf.writeBoolean(this.worldEditor);
        byteBuf.writeBoolean(this.createdInEditor);
        byteBuf.writeBoolean(this.exportedFromEditor);
        byteBuf.writeVarInt(this.dayCycleStopTime);
        byteBuf.writeVarInt(this.eduEditionOffer);
        byteBuf.writeBoolean(this.hasEduFeaturesEnabled);
        byteBuf.writeString(""); // Education Edition Product ID
        byteBuf.writeFloatLE(this.rainLevel);
        byteBuf.writeFloatLE(this.lightningLevel);
        byteBuf.writeBoolean(this.hasConfirmedPlatformLockedContent);
        byteBuf.writeBoolean(this.multiplayerGame);
        byteBuf.writeBoolean(this.broadcastToLAN);
        byteBuf.writeVarInt(this.xblBroadcastIntent);
        byteBuf.writeVarInt(this.platformBroadcastIntent);
        byteBuf.writeBoolean(this.commandsEnabled);
        byteBuf.writeBoolean(this.isTexturePacksRequired);
        byteBuf.writeGameRules(this.gameRules);
        if (!Server.getInstance().isWaterdogCapable()) {
            byteBuf.writeIntLE(6); // Experiment count
            {
                byteBuf.writeString("data_driven_items");
                byteBuf.writeBoolean(true);
                byteBuf.writeString("data_driven_biomes");
                byteBuf.writeBoolean(true);
                byteBuf.writeString("upcoming_creator_features");
                byteBuf.writeBoolean(true);
                byteBuf.writeString("gametest");
                byteBuf.writeBoolean(true);
                byteBuf.writeString("experimental_molang_features");
                byteBuf.writeBoolean(true);
                byteBuf.writeString("cameras");
                byteBuf.writeBoolean(true);
            }
            byteBuf.writeBoolean(true); // Were experiments previously toggled
        } else {
            byteBuf.writeIntLE(0);
            byteBuf.writeBoolean(false); // Were experiments previously toggled
        }
        byteBuf.writeBoolean(this.bonusChest);
        byteBuf.writeBoolean(this.hasStartWithMapEnabled);
        byteBuf.writeVarInt(this.permissionLevel);
        byteBuf.writeIntLE(this.serverChunkTickRange);
        byteBuf.writeBoolean(this.hasLockedBehaviorPack);
        byteBuf.writeBoolean(this.hasLockedResourcePack);
        byteBuf.writeBoolean(this.isFromLockedWorldTemplate);
        byteBuf.writeBoolean(this.isUsingMsaGamertagsOnly);
        byteBuf.writeBoolean(this.isFromWorldTemplate);
        byteBuf.writeBoolean(this.isWorldTemplateOptionLocked);
        byteBuf.writeBoolean(this.isOnlySpawningV1Villagers);
        byteBuf.writeBoolean(this.isDisablingPersonas);
        byteBuf.writeBoolean(this.isDisablingCustomSkins);
        byteBuf.writeBoolean(this.emoteChatMuted);
        byteBuf.writeString("*"); // vanillaVersion
        byteBuf.writeIntLE(16); // Limited world width
        byteBuf.writeIntLE(16); // Limited world height
        byteBuf.writeBoolean(false); // Nether type
        byteBuf.writeString(""); // EduSharedUriResource buttonName
        byteBuf.writeString(""); // EduSharedUriResource linkUri
        byteBuf.writeBoolean(false); // force Experimental Gameplay (exclusive to debug clients)
        byteBuf.writeByte(this.chatRestrictionLevel);
        byteBuf.writeBoolean(this.disablePlayerInteractions);
        /* Level settings end */
        byteBuf.writeString(this.levelId);
        byteBuf.writeString(this.worldName);
        byteBuf.writeString(this.premiumWorldTemplateId);
        byteBuf.writeBoolean(this.isTrial);
        byteBuf.writeVarInt(Objects.requireNonNullElseGet(this.serverAuthoritativeMovement, () -> this.isMovementServerAuthoritative ? 1 : 0));// 2 - rewind
        byteBuf.writeVarInt(0); // RewindHistorySize
        if (this.serverAuthoritativeMovement != null) {
            byteBuf.writeBoolean(this.serverAuthoritativeMovement > 0); // isServerAuthoritativeBlockBreaking
        } else {//兼容nkx旧插件
            byteBuf.writeBoolean(this.isMovementServerAuthoritative); // isServerAuthoritativeBlockBreaking
        }
        byteBuf.writeLongLE(this.currentTick);
        byteBuf.writeVarInt(this.enchantmentSeed);

        // Custom blocks
        byteBuf.writeUnsignedVarInt(this.blockProperties.size());
        try {
            for (CustomBlockDefinition customBlockDefinition : this.blockProperties) {
                byteBuf.writeString(customBlockDefinition.identifier());
                byteBuf.writeBytes(NBTIO.write(customBlockDefinition.nbt(), ByteOrder.LITTLE_ENDIAN, true));
            }
        } catch (IOException e) {
            log.error("Error while encoding NBT data of BlockPropertyData", e);
        }

        byteBuf.writeBytes(Registries.ITEM_RUNTIMEID.getItemPalette());
        byteBuf.writeString(this.multiplayerCorrelationId);
        byteBuf.writeBoolean(this.isInventoryServerAuthoritative);
        byteBuf.writeString(vanillaVersion); // Server Engine
        try {
            byteBuf.writeBytes(NBTIO.writeNetwork(playerPropertyData)); // playerPropertyData
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byteBuf.writeLongLE(0); // blockRegistryChecksum
        byteBuf.writeUUID(new UUID(0, 0)); // worldTemplateId
        byteBuf.writeBoolean(this.clientSideGenerationEnabled);
        byteBuf.writeBoolean(this.blockNetworkIdsHashed); // blockIdsAreHashed
        byteBuf.writeBoolean(this.isSoundsServerAuthoritative); // serverAuthSounds
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
