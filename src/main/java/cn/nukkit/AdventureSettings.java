package cn.nukkit;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.RequestPermissionsPacket;
import cn.nukkit.network.protocol.UpdateAbilitiesPacket;
import cn.nukkit.network.protocol.UpdateAdventureSettingsPacket;
import cn.nukkit.network.protocol.types.AbilityLayer;
import cn.nukkit.network.protocol.types.CommandPermission;
import cn.nukkit.network.protocol.types.PlayerAbility;
import cn.nukkit.network.protocol.types.PlayerPermission;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class AdventureSettings implements Cloneable {

    public static final int PERMISSION_NORMAL = 0;
    public static final int PERMISSION_OPERATOR = 1;
    public static final int PERMISSION_HOST = 2;
    public static final int PERMISSION_AUTOMATION = 3;
    public static final int PERMISSION_ADMIN = 4;

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static final String KEY_ABILITIES = "Abilities";
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static final String KEY_PLAYER_PERMISSION = "PlayerPermission";
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static final String KEY_COMMAND_PERMISSION = "CommandPermission";

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    private static final Map<PlayerAbility, Type> ability2TypeMap = new HashMap<>();

    private final Map<Type, Boolean> values = new EnumMap<>(Type.class);
    @Getter
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    private PlayerPermission playerPermission;
    @Getter
    @Setter
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    private CommandPermission commandPermission;
    private Player player;

    public AdventureSettings(Player player) {
        this.player = player;
        init(null);
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public AdventureSettings(Player player, CompoundTag nbt) {
        this.player = player;
        init(nbt);
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void setPlayerPermission(PlayerPermission playerPermission) {
        this.playerPermission = playerPermission;
        this.player.setOp(playerPermission == PlayerPermission.OPERATOR);
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void init(@Nullable CompoundTag nbt) {
        if (nbt == null || !nbt.contains(KEY_ABILITIES)) {
            set(Type.WORLD_IMMUTABLE, player.isAdventure() || player.isSpectator());
            set(Type.WORLD_BUILDER, !player.isAdventure() && !player.isSpectator());
            set(Type.AUTO_JUMP, true);
            set(Type.ALLOW_FLIGHT, player.isCreative() || player.isSpectator());
            set(Type.NO_CLIP, player.isSpectator());
            set(Type.FLYING, player.isSpectator());
            set(Type.OPERATOR, player.isOp());
            set(Type.TELEPORT, player.isOp());

            commandPermission = player.isOp() ? CommandPermission.OPERATOR : CommandPermission.NORMAL;
            playerPermission = player.isOp() ? PlayerPermission.OPERATOR : PlayerPermission.MEMBER;
        } else readNBT(nbt);

        //离线时被deop
        if (playerPermission == PlayerPermission.OPERATOR && !player.isOp()) onOpChange(false);
        //离线时被op
        if (playerPermission != PlayerPermission.OPERATOR && player.isOp()) onOpChange(true);
    }

    public AdventureSettings clone(Player newPlayer) {
        try {
            AdventureSettings settings = (AdventureSettings) super.clone();
            settings.values.putAll(this.values);
            settings.player = newPlayer;
            settings.playerPermission = playerPermission;
            settings.commandPermission = commandPermission;
            return settings;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public AdventureSettings set(PlayerAbility ability, boolean value) {
        this.values.put(ability2TypeMap.get(ability), value);
        return this;
    }

    public AdventureSettings set(Type type, boolean value) {
        this.values.put(type, value);
        return this;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public boolean get(PlayerAbility ability) {
        var type = ability2TypeMap.get(ability);
        Boolean value = this.values.get(type);

        return value == null ? type.getDefaultValue() : value;
    }

    public boolean get(Type type) {
        Boolean value = this.values.get(type);

        return value == null ? type.getDefaultValue() : value;
    }

    @PowerNukkitDifference(info = "Players in spectator mode will be flagged as member even if they are OP due to a client-side limitation", since = "1.3.1.2-PN")
    @PowerNukkitXDifference(info = "updateAdventureSettingsPacket now will be sent to all players")
    public void update() {
        //向所有玩家发送以使他们能看到彼此的权限
        //Permission to send to all players so they can see each other
        var players = new HashSet<>(player.getServer().getOnlinePlayers().values());
        //确保会发向自己（eg：玩家进服时在线玩家里没有此玩家）
        //Make sure it will be sent to yourself (eg: there is no such player among the online players when the player enters the server)
        players.add(this.player);
        this.sendAbilities(players);
        this.updateAdventureSettings();
    }



    /**
     * 当玩家OP身份变动时此方法将被调用
     * 注意此方法并不会向客户端发包刷新权限信息，你需要手动调用update()方法刷新
     * @param op 是否是OP
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void onOpChange(boolean op) {
        if (op) {
            for (PlayerAbility controllableAbility : RequestPermissionsPacket.CONTROLLABLE_ABILITIES)
                set(controllableAbility, true);
            //设置op特有属性
            set(Type.OPERATOR, true);
            set(Type.TELEPORT, true);
        }

        commandPermission = op ? CommandPermission.OPERATOR : CommandPermission.NORMAL;
        //不要覆盖自定义/访客状态
        if (op && playerPermission != PlayerPermission.OPERATOR) playerPermission = PlayerPermission.OPERATOR;
        if (!op && playerPermission == PlayerPermission.OPERATOR) playerPermission = PlayerPermission.MEMBER;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void sendAbilities(Collection<Player> players) {
        UpdateAbilitiesPacket packet = new UpdateAbilitiesPacket();
        packet.entityId = player.getId();
        packet.commandPermission = commandPermission;
        packet.playerPermission = playerPermission;

        AbilityLayer layer = new AbilityLayer();
        layer.setLayerType(AbilityLayer.Type.BASE);
        layer.getAbilitiesSet().addAll(PlayerAbility.VALUES);

        for (Type type : Type.values()) {
            if (type.isAbility() && this.get(type)) {
                layer.getAbilityValues().add(type.getAbility());
            }
        }

        if (player.isCreative()) { // Make sure player can interact with creative menu
            layer.getAbilityValues().add(PlayerAbility.INSTABUILD);
        }

        // Because we send speed
        layer.getAbilityValues().add(PlayerAbility.WALK_SPEED);
        layer.getAbilityValues().add(PlayerAbility.FLY_SPEED);

        layer.setWalkSpeed(Player.DEFAULT_SPEED);
        layer.setFlySpeed(Player.DEFAULT_FLY_SPEED);

        packet.abilityLayers.add(layer);

        Server.broadcastPacket(players, packet);
    }

    /**
     * 保存权限到nbt
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void saveNBT() {
        var nbt = player.namedTag;
        var abilityTag = new CompoundTag(KEY_ABILITIES);
        values.forEach((type, bool) -> {
            abilityTag.put(type.name(), new IntTag(type.name(), bool ? 1 : 0));
        });
        nbt.put(KEY_ABILITIES, abilityTag);
        nbt.putString(KEY_PLAYER_PERMISSION, playerPermission.name());
        nbt.putString(KEY_COMMAND_PERMISSION, commandPermission.name());
    }

    /**
     * 从nbt读取权限数据
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void readNBT(CompoundTag nbt) {
        var abilityTag = nbt.getCompound(KEY_ABILITIES);
        for (Tag tag : abilityTag.getAllTags()) {
            set(Type.valueOf(tag.getName()), ((IntTag) tag).getData() == 1);
        }
        playerPermission = PlayerPermission.valueOf(nbt.getString(KEY_PLAYER_PERMISSION));
        commandPermission = CommandPermission.valueOf(nbt.getString(KEY_COMMAND_PERMISSION));
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void updateAdventureSettings() {
        UpdateAdventureSettingsPacket adventurePacket = new UpdateAdventureSettingsPacket();
        adventurePacket.autoJump = get(Type.AUTO_JUMP);
        adventurePacket.immutableWorld = get(Type.WORLD_IMMUTABLE);
        adventurePacket.noMvP = get(Type.NO_MVP);
        adventurePacket.noPvM = get(Type.NO_PVM);
        adventurePacket.showNameTags = get(Type.SHOW_NAME_TAGS);
        player.dataPacket(adventurePacket);
        player.resetInAirTicks();
    }

    public enum Type {
        WORLD_IMMUTABLE(false),
        NO_PVM(false),
        NO_MVP(PlayerAbility.INVULNERABLE, false),
        SHOW_NAME_TAGS(false),
        AUTO_JUMP(true),
        ALLOW_FLIGHT(PlayerAbility.MAY_FLY, false),
        NO_CLIP(PlayerAbility.NO_CLIP, false),
        WORLD_BUILDER(PlayerAbility.WORLD_BUILDER, false),
        FLYING(PlayerAbility.FLYING, false),
        MUTED(PlayerAbility.MUTED, false),
        MINE(PlayerAbility.MINE, true),
        DOORS_AND_SWITCHED(PlayerAbility.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(PlayerAbility.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(PlayerAbility.ATTACK_PLAYERS, true),
        ATTACK_MOBS(PlayerAbility.ATTACK_MOBS, true),
        OPERATOR(PlayerAbility.OPERATOR_COMMANDS, false),
        TELEPORT(PlayerAbility.TELEPORT, false),
        BUILD(PlayerAbility.BUILD, true),
        PRIVILEGED_BUILDER(PlayerAbility.PRIVILEGED_BUILDER, false),

        @Deprecated DEFAULT_LEVEL_PERMISSIONS(null, false);

        private final PlayerAbility ability;
        private final boolean defaultValue;

        Type(boolean defaultValue) {
            this.defaultValue = defaultValue;
            this.ability = null;
        }

        Type(PlayerAbility ability, boolean defaultValue) {
            this.ability = ability;
            this.defaultValue = defaultValue;
            if (this.ability != null) ability2TypeMap.put(this.ability, this);
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }

        public PlayerAbility getAbility() {
            return this.ability;
        }

        public boolean isAbility() {
            return this.ability != null;
        }
    }
}
