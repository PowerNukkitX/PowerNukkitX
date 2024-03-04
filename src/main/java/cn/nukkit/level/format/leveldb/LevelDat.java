package cn.nukkit.level.format.leveldb;

import cn.nukkit.level.GameRules;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.GameType;
import cn.nukkit.utils.SemVersion;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

@Getter
@Builder
@ToString
public class LevelDat {
    @Builder.Default
    String biomeOverride = "";
    @Builder.Default
    boolean centerMapsToOrigin = false;
    @Builder.Default
    boolean confirmedPlatformLockedContent = false;
    @Builder.Default
    int difficulty = 1;
    @Builder.Default
    String flatWorldLayers = "";
    @Builder.Default
    boolean forceGameType = false;
    @Builder.Default
    GameType gameType = GameType.from(0);
    @Builder.Default
    int generator = 1;
    @Builder.Default
    String inventoryVersion = "1.20.60";
    @Builder.Default
    boolean LANBroadcast = true;
    @Builder.Default
    boolean LANBroadcastIntent = true;
    @Builder.Default
    long lastPlayed = 0L;
    @Builder.Default
    String name = "Bedrock level";
    @Builder.Default
    BlockVector3 limitedWorldOriginPoint = new BlockVector3(0, 64, 0);
    @Builder.Default
    SemVersion minimumCompatibleClientVersion = new SemVersion(
            1,
            20,
            50,
            0,
            0
    );
    @Builder.Default
    boolean multiplayerGame = true;
    @Builder.Default
    boolean multiplayerGameIntent = false;
    @Builder.Default
    int netherScale = 8;
    @Builder.Default
    int networkVersion = ProtocolInfo.CURRENT_PROTOCOL;
    @Builder.Default
    int platform = 2;
    @Builder.Default
    int platformBroadcastIntent = 0;
    @Builder.Default
    long randomSeed = 1811906518383890446L;
    @Builder.Default
    boolean spawnV1Villagers = false;
    @Builder.Default
    BlockVector3 spawnPoint = new BlockVector3(0, 70, 0);
    @Builder.Default
    int storageVersion = 10;
    @Builder.Default
    long time = 0L;
    @Builder.Default
    int worldVersion = 1;
    @Builder.Default
    int XBLBroadcastIntent = 0;
    @Builder.Default
    Abilities abilities = Abilities.builder().build();
    @Builder.Default
    String baseGameVersion = "*";
    @Builder.Default
    boolean bonusChestEnabled = false;
    @Builder.Default
    boolean bonusChestSpawned = false;
    @Builder.Default
    boolean cheatsEnabled = false;
    @Builder.Default
    boolean commandsEnabled = true;
    @Builder.Default
    @Getter(AccessLevel.NONE)
    GameRules gameRules = GameRules.getDefault();
    @Builder.Default
    long currentTick = 0L;
    @Builder.Default
    int daylightCycle = 0;
    @Builder.Default
    int editorWorldType = 0;
    @Builder.Default
    int eduOffer = 0;
    @Builder.Default
    boolean educationFeaturesEnabled = false;
    @Builder.Default
    Experiments experiments = Experiments.builder().build();
    @Builder.Default
    boolean hasBeenLoadedInCreative = true;
    @Builder.Default
    boolean hasLockedBehaviorPack = false;
    @Builder.Default
    boolean hasLockedResourcePack = false;
    @Builder.Default
    boolean immutableWorld = false;
    @Builder.Default
    boolean isCreatedInEditor = false;
    @Builder.Default
    boolean isExportedFromEditor = false;
    @Builder.Default
    boolean isFromLockedTemplate = false;
    @Builder.Default
    boolean isFromWorldTemplate = false;
    @Builder.Default
    boolean isRandomSeedAllowed = false;
    @Builder.Default
    boolean isSingleUseWorld = false;
    @Builder.Default
    boolean isWorldTemplateOptionLocked = false;
    @Builder.Default
    SemVersion lastOpenedWithVersion = new SemVersion(
            1,
            20,
            40,
            1,
            0
    );
    @Builder.Default
    float lightningLevel = 0.0f;
    @Builder.Default
    int lightningTime = 0;//thunderTime
    @Builder.Default
    int limitedWorldDepth = 16;
    @Builder.Default
    int limitedWorldWidth = 16;
    @Builder.Default
    int permissionsLevel = 0;
    @Builder.Default
    int playerPermissionsLevel = 1;
    @Builder.Default
    int playersSleepingPercentage = 100;
    @Builder.Default
    String prid = "";
    @Builder.Default
    float rainLevel = 0.0f;
    @Builder.Default
    int rainTime = 0;//rainTime
    @Builder.Default
    int randomTickSpeed = 1;
    @Builder.Default
    boolean recipesUnlock = false;
    @Builder.Default
    boolean requiresCopiedPackRemovalCheck = false;
    @Builder.Default
    int serverChunkTickRange = 4;
    @Builder.Default
    boolean spawnMobs = true;
    @Builder.Default
    boolean startWithMapEnabled = false;
    @Builder.Default
    boolean texturePacksRequired = false;
    @Builder.Default
    boolean useMsaGamertagsOnly = true;
    @Builder.Default
    long worldStartCount = 0L;
    @Builder.Default
    WorldPolicies worldPolicies = new WorldPolicies();
    @Builder.Default
    boolean raining = false;//PNX Custom field
    @Builder.Default
    boolean thundering = false;//PNX Custom field

    public void setRandomSeed(long seed) {
        this.randomSeed = seed;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public void setLightningTime(int lightningTime) {
        this.lightningTime = lightningTime;
    }

    public void setThundering(boolean thundering) {
        this.thundering = thundering;
    }

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameType getGameType() {
        return gameType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void addTime() {
        this.time++;
    }

    public void setCurrentTick(long currentTick) {
        this.currentTick = currentTick;
    }

    public GameRules getGameRules() {
        return gameRules;
    }

    public void setGameRules(GameRules gameRules) {
        this.gameRules = gameRules;
    }

    /**
     * The overworld default spawn point
     */
    public BlockVector3 getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(BlockVector3 spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Value
    @Builder
    @ToString
    public static class Abilities {
        @Builder.Default
        boolean attackMobs = true;

        @Builder.Default
        boolean attackPlayers = true;

        @Builder.Default
        boolean build = true;

        @Builder.Default
        boolean doorsAndSwitches = true;

        @Builder.Default
        float flySpeed = 0.05f;

        @Builder.Default
        boolean flying = false;

        @Builder.Default
        boolean instaBuild = false;

        @Builder.Default
        boolean invulnerable = false;

        @Builder.Default
        boolean lightning = false;

        @Builder.Default
        boolean mayFly = false;

        @Builder.Default
        boolean mine = true;

        @Builder.Default
        boolean op = false;

        @Builder.Default
        boolean openContainers = true;

        @Builder.Default
        boolean teleport = false;

        @Builder.Default
        float walkSpeed = 0.1f;
    }

    @Value
    @Builder
    @ToString
    public static class Experiments {
        @Builder.Default
        boolean experimentsEverUsed = false;
        @Builder.Default
        boolean savedWithToggledExperiments = false;
        @Builder.Default
        boolean cameras = false;
        @Builder.Default
        boolean dataDrivenBiomes = false;
        @Builder.Default
        boolean dataDrivenItems = false;
        @Builder.Default
        boolean experimentalMolangFeatures = false;
        @Builder.Default
        boolean gametest = false;
        @Builder.Default
        boolean upcomingCreatorFeatures = false;
        @Builder.Default
        boolean villagerTradesRebalance = false;
    }

    @Value
    @Builder
    @ToString
    public static class WorldPolicies {
    }
}
