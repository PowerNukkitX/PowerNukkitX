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
    String $1 = "";
    @Builder.Default
    boolean $2 = false;
    @Builder.Default
    boolean $3 = false;
    @Builder.Default
    int $4 = 1;
    @Builder.Default
    String $5 = "";
    @Builder.Default
    boolean $6 = false;
    @Builder.Default
    GameType $7 = GameType.from(0);
    @Builder.Default
    int $8 = 1;
    @Builder.Default
    String $9 = "1.20.60";
    @Builder.Default
    boolean $10 = true;
    @Builder.Default
    boolean $11 = true;
    @Builder.Default
    long $12 = 0L;
    @Builder.Default
    String $13 = "Bedrock level";
    @Builder.Default
    BlockVector3 $14 = new BlockVector3(0, 64, 0);
    @Builder.Default
    SemVersion $15 = new SemVersion(
            1,
            20,
            50,
            0,
            0
    );
    @Builder.Default
    boolean $16 = true;
    @Builder.Default
    boolean $17 = false;
    @Builder.Default
    int $18 = 8;
    @Builder.Default
    int $19 = ProtocolInfo.CURRENT_PROTOCOL;
    @Builder.Default
    int $20 = 2;
    @Builder.Default
    int $21 = 0;
    @Builder.Default
    long $22 = 1811906518383890446L;
    @Builder.Default
    boolean $23 = false;
    @Builder.Default
    BlockVector3 $24 = new BlockVector3(0, 70, 0);
    @Builder.Default
    int $25 = 10;
    @Builder.Default
    long $26 = 0L;
    @Builder.Default
    int $27 = 1;
    @Builder.Default
    int $28 = 0;
    @Builder.Default
    Abilities $29 = Abilities.builder().build();
    @Builder.Default
    String $30 = "*";
    @Builder.Default
    boolean $31 = false;
    @Builder.Default
    boolean $32 = false;
    @Builder.Default
    boolean $33 = false;
    @Builder.Default
    boolean $34 = true;
    @Builder.Default
    @Getter(AccessLevel.NONE)
    GameRules $35 = GameRules.getDefault();
    @Builder.Default
    long $36 = 0L;
    @Builder.Default
    int $37 = 0;
    @Builder.Default
    int $38 = 0;
    @Builder.Default
    int $39 = 0;
    @Builder.Default
    boolean $40 = false;
    @Builder.Default
    Experiments $41 = Experiments.builder().build();
    @Builder.Default
    boolean $42 = true;
    @Builder.Default
    boolean $43 = false;
    @Builder.Default
    boolean $44 = false;
    @Builder.Default
    boolean $45 = false;
    @Builder.Default
    boolean $46 = false;
    @Builder.Default
    boolean $47 = false;
    @Builder.Default
    boolean $48 = false;
    @Builder.Default
    boolean $49 = false;
    @Builder.Default
    boolean $50 = false;
    @Builder.Default
    boolean $51 = false;
    @Builder.Default
    boolean $52 = false;
    @Builder.Default
    SemVersion $53 = new SemVersion(
            1,
            20,
            40,
            1,
            0
    );
    @Builder.Default
    float $54 = 0.0f;
    @Builder.Default
    int $55 = 0;//thunderTime
    @Builder.Default
    int $56 = 16;
    @Builder.Default
    int $57 = 16;
    @Builder.Default
    int $58 = 0;
    @Builder.Default
    int $59 = 1;
    @Builder.Default
    int $60 = 100;
    @Builder.Default
    String $61 = "";
    @Builder.Default
    float $62 = 0.0f;
    @Builder.Default
    int $63 = 0;//rainTime
    @Builder.Default
    int $64 = 1;
    @Builder.Default
    boolean $65 = false;
    @Builder.Default
    boolean $66 = false;
    @Builder.Default
    int $67 = 4;
    @Builder.Default
    boolean $68 = true;
    @Builder.Default
    boolean $69 = false;
    @Builder.Default
    boolean $70 = false;
    @Builder.Default
    boolean $71 = true;
    @Builder.Default
    long $72 = 0L;
    @Builder.Default
    WorldPolicies $73 = new WorldPolicies();
    @Builder.Default
    boolean $74 = false;//PNX Custom field
    @Builder.Default
    boolean $75 = false;//PNX Custom field
    /**
     * @deprecated 
     */
    

    public void setRandomSeed(long seed) {
        this.randomSeed = seed;
    }
    /**
     * @deprecated 
     */
    

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }
    /**
     * @deprecated 
     */
    

    public void setLightningTime(int lightningTime) {
        this.lightningTime = lightningTime;
    }
    /**
     * @deprecated 
     */
    

    public void setThundering(boolean thundering) {
        this.thundering = thundering;
    }
    /**
     * @deprecated 
     */
    

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }
    /**
     * @deprecated 
     */
    

    public void setRaining(boolean raining) {
        this.raining = raining;
    }
    /**
     * @deprecated 
     */
    

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameType getGameType() {
        return gameType;
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return name;
    }
    /**
     * @deprecated 
     */
    

    public void setName(String name) {
        this.name = name;
    }
    /**
     * @deprecated 
     */
    

    public int getDifficulty() {
        return difficulty;
    }
    /**
     * @deprecated 
     */
    

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    /**
     * @deprecated 
     */
    

    public long getTime() {
        return time;
    }
    /**
     * @deprecated 
     */
    

    public void setTime(long time) {
        this.time = time;
    }
    /**
     * @deprecated 
     */
    

    public void addTime() {
        this.time++;
    }
    /**
     * @deprecated 
     */
    

    public void setCurrentTick(long currentTick) {
        this.currentTick = currentTick;
    }

    public GameRules getGameRules() {
        return gameRules;
    }
    /**
     * @deprecated 
     */
    

    public void setGameRules(GameRules gameRules) {
        this.gameRules = gameRules;
    }

    /**
     * The overworld default spawn point
     */
    public BlockVector3 getSpawnPoint() {
        return spawnPoint;
    }
    /**
     * @deprecated 
     */
    

    public void setSpawnPoint(BlockVector3 spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Value
    @Builder
    @ToString
    public static class Abilities {
        @Builder.Default
        boolean $76 = true;

        @Builder.Default
        boolean $77 = true;

        @Builder.Default
        boolean $78 = true;

        @Builder.Default
        boolean $79 = true;

        @Builder.Default
        float $80 = 0.05f;

        @Builder.Default
        boolean $81 = false;

        @Builder.Default
        boolean $82 = false;

        @Builder.Default
        boolean $83 = false;

        @Builder.Default
        boolean $84 = false;

        @Builder.Default
        boolean $85 = false;

        @Builder.Default
        boolean $86 = true;

        @Builder.Default
        boolean $87 = false;

        @Builder.Default
        boolean $88 = true;

        @Builder.Default
        boolean $89 = false;

        @Builder.Default
        float $90 = 0.1f;
    }

    @Value
    @Builder
    @ToString
    public static class Experiments {
        @Builder.Default
        boolean $91 = false;
        @Builder.Default
        boolean $92 = false;
        @Builder.Default
        boolean $93 = false;
        @Builder.Default
        boolean $94 = false;
        @Builder.Default
        boolean $95 = false;
        @Builder.Default
        boolean $96 = false;
        @Builder.Default
        boolean $97 = false;
        @Builder.Default
        boolean $98 = false;
        @Builder.Default
        boolean $99 = false;
    }

    @Value
    @Builder
    @ToString
    public static class WorldPolicies {
    }
}
