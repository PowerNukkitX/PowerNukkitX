package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.level.GameRules;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @since 15-10-13
 */
@Log4j2
@ToString
public class StartGamePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.START_GAME_PACKET;

    public static final int GAME_PUBLISH_SETTING_NO_MULTI_PLAY = 0;
    public static final int GAME_PUBLISH_SETTING_INVITE_ONLY = 1;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_ONLY = 2;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_OF_FRIENDS = 3;
    public static final int GAME_PUBLISH_SETTING_PUBLIC = 4;

    @Override
    public byte pid() {
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

    public String levelId = ""; //base64 string, usually the same as world folder name in vanilla
    public String worldName;
    public String premiumWorldTemplateId = "";
    public boolean isTrial = false;
    @Deprecated
    public boolean isMovementServerAuthoritative;
    @PowerNukkitXOnly
    @Since("1.19.40-r3")
    public Integer serverAuthoritativeMovement;
    @Since("1.3.0.0-PN")
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

    public byte chatRestrictionLevel;

    public boolean disablePlayerInteractions;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.playerGamemode);
        this.putVector3f(this.x, this.y, this.z);
        this.putLFloat(this.yaw);
        this.putLFloat(this.pitch);
        /* Level settings start */
        this.putLLong(this.seed);
        this.putLShort(0x00); // SpawnBiomeType - Default
        this.putString("plains"); // UserDefinedBiomeName
        this.putVarInt(this.dimension);
        this.putVarInt(this.generator);
        this.putVarInt(this.worldGamemode);
        this.putVarInt(this.difficulty);
        this.putBlockVector3(this.spawnX, this.spawnY, this.spawnZ);
        this.putBoolean(this.hasAchievementsDisabled);
        this.putBoolean(this.worldEditor);
        this.putVarInt(this.dayCycleStopTime);
        this.putVarInt(this.eduEditionOffer);
        this.putBoolean(this.hasEduFeaturesEnabled);
        this.putString(""); // Education Edition Product ID
        this.putLFloat(this.rainLevel);
        this.putLFloat(this.lightningLevel);
        this.putBoolean(this.hasConfirmedPlatformLockedContent);
        this.putBoolean(this.multiplayerGame);
        this.putBoolean(this.broadcastToLAN);
        this.putVarInt(this.xblBroadcastIntent);
        this.putVarInt(this.platformBroadcastIntent);
        this.putBoolean(this.commandsEnabled);
        this.putBoolean(this.isTexturePacksRequired);
        this.putGameRules(this.gameRules);
        if (Server.getInstance().isEnableExperimentMode() && !Server.getInstance().getConfig("settings.waterdogpe", false)) {
            this.putLInt(3); // Experiment count
            {
                this.putString("data_driven_items");
                this.putBoolean(true);
                //this.putString("data_driven_biomes");
                //this.putBoolean(true);
                this.putString("upcoming_creator_features");
                this.putBoolean(true);
                //this.putString("gametest");
                //this.putBoolean(true);
                this.putString("experimental_molang_features");
                this.putBoolean(true);
            }
            this.putBoolean(true); // Were experiments previously toggled
        } else {
            this.putLInt(0);
            this.putBoolean(false); // Were experiments previously toggled
        }
        this.putBoolean(this.bonusChest);
        this.putBoolean(this.hasStartWithMapEnabled);
        this.putVarInt(this.permissionLevel);
        this.putLInt(this.serverChunkTickRange);
        this.putBoolean(this.hasLockedBehaviorPack);
        this.putBoolean(this.hasLockedResourcePack);
        this.putBoolean(this.isFromLockedWorldTemplate);
        this.putBoolean(this.isUsingMsaGamertagsOnly);
        this.putBoolean(this.isFromWorldTemplate);
        this.putBoolean(this.isWorldTemplateOptionLocked);
        this.putBoolean(this.isOnlySpawningV1Villagers);
        this.putBoolean(this.isDisablingPersonas);
        this.putBoolean(this.isDisablingCustomSkins);
        this.putBoolean(this.emoteChatMuted);
        this.putString("*"); // vanillaVersion
        this.putLInt(16); // Limited world width
        this.putLInt(16); // Limited world height
        this.putBoolean(false); // Nether type
        this.putString(""); // EduSharedUriResource buttonName
        this.putString(""); // EduSharedUriResource linkUri
        if (Server.getInstance().isEnableExperimentMode()) { // force Experimental Gameplay
            this.putBoolean(!Server.getInstance().getConfig("settings.waterdogpe", false)); // Why WaterDogPE require an extra optional boolean if this is set to true? I don't know.
        } else {
            this.putBoolean(false);
        }
        this.putByte(this.chatRestrictionLevel);
        this.putBoolean(this.disablePlayerInteractions);
        /* Level settings end */
        this.putString(this.levelId);
        this.putString(this.worldName);
        this.putString(this.premiumWorldTemplateId);
        this.putBoolean(this.isTrial);
        this.putVarInt(Objects.requireNonNullElseGet(this.serverAuthoritativeMovement, () -> this.isMovementServerAuthoritative ? 1 : 0));// 2 - rewind
        this.putVarInt(0); // RewindHistorySize
        if (this.serverAuthoritativeMovement != null) {
            this.putBoolean(this.serverAuthoritativeMovement > 0); // isServerAuthoritativeBlockBreaking
        } else {//兼容nkx旧插件
            this.putBoolean(this.isMovementServerAuthoritative); // isServerAuthoritativeBlockBreaking
        }
        this.putLLong(this.currentTick);
        this.putVarInt(this.enchantmentSeed);

        // Custom blocks
        this.putUnsignedVarInt(this.blockProperties.size());
        try {
            for (CustomBlockDefinition customBlockDefinition : this.blockProperties) {
                this.putString(customBlockDefinition.identifier());
                this.put(NBTIO.write(customBlockDefinition.nbt(), ByteOrder.LITTLE_ENDIAN, true));
            }
        } catch (IOException e) {
            log.error("Error while encoding NBT data of BlockPropertyData", e);
        }

        this.put(RuntimeItems.getRuntimeMapping().getItemDataPalette());
        this.putString(this.multiplayerCorrelationId);
        this.putBoolean(this.isInventoryServerAuthoritative);
        this.putString(vanillaVersion); // Server Engine
        try {
            this.put(NBTIO.writeNetwork(new CompoundTag(""))); // playerPropertyData
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.putLLong(0); // blockRegistryChecksum
        this.putUUID(new UUID(0, 0)); // worldTemplateId
        this.putBoolean(this.clientSideGenerationEnabled);
    }
}
