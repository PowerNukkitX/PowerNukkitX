package cn.nukkit.level.format.leveldb.datas;

public final class LDBPlayerAbilities {
    private boolean canAttackMobs;
    private boolean canAttackPlayers;
    private boolean canBuild;
    private boolean canFly;
    private boolean canInstaBuild;  // TODO: what exactly is this? How does it differ from canBuild?
    private boolean canMine;
    private boolean canOpenContainers;
    private boolean canTeleport;
    private boolean canUseDoorsAndSwitches;
    private float flySpeed;
    private boolean isFlying;
    private boolean isInvulnerable;
    private boolean isOp;
    private boolean lightning;  // TODO: what is this?
    private int permissionsLevel;
    private int playerPermissionsLevel; // TODO: what is this and isn't it the same as permissionLevel?
    private float walkSpeed;


    public boolean canAttackMobs() {
        return this.canAttackMobs;
    }

    public LDBPlayerAbilities setCanAttackMobs(boolean canAttackMobs) {
        this.canAttackMobs = canAttackMobs;
        return this;
    }

    public boolean canAttackPlayers() {
        return this.canAttackPlayers;
    }

    public LDBPlayerAbilities setCanAttackPlayers(boolean canAttackPlayers) {
        this.canAttackPlayers = canAttackPlayers;
        return this;
    }

    public boolean canBuild() {
        return this.canBuild;
    }

    public LDBPlayerAbilities setCanBuild(boolean canBuild) {
        this.canBuild = canBuild;
        return this;
    }

    public boolean canFly() {
        return this.canFly;
    }

    public LDBPlayerAbilities setCanFly(boolean canFly) {
        this.canFly = canFly;
        return this;
    }

    public boolean canInstaBuild() {
        return this.canInstaBuild;
    }

    public LDBPlayerAbilities setCanInstaBuild(boolean instaBuild) {
        this.canInstaBuild = instaBuild;
        return this;
    }

    public boolean canMine() {
        return this.canMine;
    }

    public LDBPlayerAbilities setCanMine(boolean canMine) {
        this.canMine = canMine;
        return this;
    }

    public boolean canOpenContainers() {
        return this.canOpenContainers;
    }

    public LDBPlayerAbilities setCanOpenContainers(boolean canOpenContainers) {
        this.canOpenContainers = canOpenContainers;
        return this;
    }

    public boolean canTeleport() {
        return this.canTeleport;
    }

    public LDBPlayerAbilities setCanTeleport(boolean canTeleport) {
        this.canTeleport = canTeleport;
        return this;
    }

    public boolean canUseDoorsAndSwitches() {
        return this.canUseDoorsAndSwitches;
    }

    public LDBPlayerAbilities setCanUseDoorsAndSwitches(boolean canUseDoorsAndSwitches) {
        this.canUseDoorsAndSwitches = canUseDoorsAndSwitches;
        return this;
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public LDBPlayerAbilities setFlySpeed(float flySpeed) {
        this.flySpeed = flySpeed;
        return this;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public LDBPlayerAbilities setIsFlying(boolean isFlying) {
        this.isFlying = isFlying;
        return this;
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    public LDBPlayerAbilities setIsInvulnerable(boolean invulnerable) {
        this.isInvulnerable = invulnerable;
        return this;
    }

    public boolean isOp() {
        return this.isOp;
    }

    public LDBPlayerAbilities setIsOp(boolean isOp) {
        this.isOp = isOp;
        return this;
    }

    public boolean isLightning() {
        return this.lightning;
    }

    public LDBPlayerAbilities setIsLightning(boolean isLightning) {
        this.lightning = isLightning;
        return this;
    }

    public int getPermissionsLevel() {
        return this.permissionsLevel;
    }

    public LDBPlayerAbilities setPermissionsLevel(int level) {
        this.permissionsLevel = level;
        return this;
    }

    public int getPlayerPermissionsLevel() {
        return this.playerPermissionsLevel;
    }

    public LDBPlayerAbilities setPlayerPermissionsLevel(int level) {
        this.playerPermissionsLevel = level;
        return this;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    public LDBPlayerAbilities setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
        return this;
    }

    public static LDBPlayerAbilities getDefault() {
        return new LDBPlayerAbilities().setIsLightning(false)
                .setCanMine(true)
                .setCanFly(false)
                .setIsOp(false)
                .setWalkSpeed(0.1f)
                .setPermissionsLevel(0)
                .setCanTeleport(false)
                .setCanAttackPlayers(true)
                .setFlySpeed(0.05f)
                .setCanInstaBuild(false)
                .setIsInvulnerable(false)
                .setCanBuild(true)
                .setIsFlying(false)
                .setCanAttackMobs(true)
                .setCanOpenContainers(true)
                .setCanUseDoorsAndSwitches(true)
                .setPlayerPermissionsLevel(1);
    }
}
