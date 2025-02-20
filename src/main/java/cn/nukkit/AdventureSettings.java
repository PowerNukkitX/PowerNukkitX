package cn.nukkit;

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
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * AdventureSettings class for managing player abilities and permissions.
 * Author: MagicDroidX (Nukkit Project)
 */
public class AdventureSettings implements Cloneable {

    public static final int PERMISSION_NORMAL = 0;
    public static final int PERMISSION_OPERATOR = 1;
    public static final int PERMISSION_HOST = 2;
    public static final int PERMISSION_AUTOMATION = 3;
    public static final int PERMISSION_ADMIN = 4;

    public static final String KEY_ABILITIES = "Abilities";
    public static final String KEY_PLAYER_PERMISSION = "PlayerPermission";
    public static final String KEY_COMMAND_PERMISSION = "CommandPermission";

    private static final Map<PlayerAbility, Type> ability2TypeMap = new HashMap<>();

    private final Map<Type, Boolean> values = new EnumMap<>(Type.class);

    @Getter
    private PlayerPermission playerPermission;

    @Getter
    @Setter
    private CommandPermission commandPermission;

    private Player player;

    /**
     * Constructor for AdventureSettings.
     *
     * @param player The player associated with these settings.
     */
    public AdventureSettings(Player player) {
        this.player = player;
        init(null);
    }

    /**
     * Constructor for AdventureSettings with NBT data.
     *
     * @param player The player associated with these settings.
     * @param nbt    The NBT data to initialize settings.
     */
    public AdventureSettings(Player player, CompoundTag nbt) {
        this.player = player;
        init(nbt);
    }

    /**
     * Sets the player permission.
     *
     * @param playerPermission The player permission to set.
     */
    public void setPlayerPermission(PlayerPermission playerPermission) {
        this.playerPermission = playerPermission;
        this.player.setOp(playerPermission == PlayerPermission.OPERATOR);
    }

    /**
     * Initializes the settings with optional NBT data.
     *
     * @param nbt The NBT data to initialize settings, or null for default settings.
     */
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
        } else {
            readNBT(nbt);
        }

        // Handle offline deop
        if (playerPermission == PlayerPermission.OPERATOR && !player.isOp()) {
            onOpChange(false);
        }
        // Handle offline op
        if (playerPermission != PlayerPermission.OPERATOR && player.isOp()) {
            onOpChange(true);
        }
    }

    @Override
    public AdventureSettings clone() {
        try {
            AdventureSettings settings = (AdventureSettings) super.clone();
            settings.values.putAll(this.values);
            settings.player = this.player;
            settings.playerPermission = this.playerPermission;
            settings.commandPermission = this.commandPermission;
            return settings;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // This should never happen.
        }
    }

    /**
     * Sets a player ability.
     *
     * @param ability The player ability to set.
     * @param value   The value to set for the ability.
     * @return The current AdventureSettings instance.
     */
    public AdventureSettings set(PlayerAbility ability, boolean value) {
        Type type = ability2TypeMap.get(ability);
        if (type != null) {
            this.values.put(type, value);
        }
        return this;
    }

    /**
     * Sets a type value.
     *
     * @param type  The type to set.
     * @param value The value to set for the type.
     * @return The current AdventureSettings instance.
     */
    public AdventureSettings set(Type type, boolean value) {
        this.values.put(type, value);
        return this;
    }

    /**
     * Gets the value of a player ability.
     *
     * @param ability The player ability to get.
     * @return The value of the player ability.
     */
    public boolean get(PlayerAbility ability) {
        Type type = ability2TypeMap.get(ability);
        if (type == null) {
            throw new IllegalArgumentException("Unknown ability: " + ability);
        }
        return this.values.getOrDefault(type, type.getDefaultValue());
    }

    /**
     * Gets the value of a type.
     *
     * @param type The type to get.
     * @return The value of the type.
     */
    public boolean get(Type type) {
        return this.values.getOrDefault(type, type.getDefaultValue());
    }

    /**
     * Updates the adventure settings and sends them to all players.
     */
    public void update() {
        // Permission to send to all players so they can see each other
        // Make sure it will be sent to yourself (e.g., there is no such player among the online players when the player enters the server)
        Collection<Player> players = new HashSet<>(player.getServer().getOnlinePlayers().values());
        players.add(this.player);
        sendAbilities(players);
        updateAdventureSettings();
    }

    /**
     * This method will be called when the player's OP status changes.
     * Note that this method does not send a packet to the client to refresh the privilege information, you need to manually call the update() method to do so.
     *
     * @param op Whether the player is OP or not.
     */
    public void onOpChange(boolean op) {
        if (op) {
            for (PlayerAbility controllableAbility : RequestPermissionsPacket.CONTROLLABLE_ABILITIES) {
                set(controllableAbility, true);
            }
        }
        // Set op-specific attributes
        set(Type.OPERATOR, op);
        set(Type.TELEPORT, op);

        commandPermission = op ? CommandPermission.OPERATOR : CommandPermission.NORMAL;

        // Don't override customization/guest status
        if (op && playerPermission != PlayerPermission.OPERATOR) {
            playerPermission = PlayerPermission.OPERATOR;
        }
        if (!op && playerPermission == PlayerPermission.OPERATOR) {
            playerPermission = PlayerPermission.MEMBER;
        }
    }

    /**
     * Sends abilities to a collection of players.
     *
     * @param players The collection of players to send abilities to.
     */
    public void sendAbilities(Collection<Player> players) {
        UpdateAbilitiesPacket packet = new UpdateAbilitiesPacket();
        packet.entityId = player.getId();
        packet.commandPermission = commandPermission;
        packet.playerPermission = playerPermission;

        AbilityLayer layer = new AbilityLayer();
        layer.setVerticalFlySpeed(1f);
        layer.setLayerType(AbilityLayer.Type.BASE);
        layer.getAbilitiesSet().addAll(PlayerAbility.VALUES);

        for (Type type : Type.values()) {
            if (type.isAbility() && get(type)) {
                layer.getAbilityValues().add(type.getAbility());
            }
        }

        if (player.isCreative()) {
            layer.getAbilityValues().add(PlayerAbility.INSTABUILD);
        }

        layer.getAbilityValues().add(PlayerAbility.WALK_SPEED);
        layer.getAbilityValues().add(PlayerAbility.FLY_SPEED);

        layer.setWalkSpeed(Player.DEFAULT_SPEED);
        layer.setFlySpeed(Player.DEFAULT_FLY_SPEED);

        packet.abilityLayers.add(layer);

        Server.broadcastPacket(players, packet);
    }

    /**
     * Saves permissions to NBT.
     */
    public void saveNBT() {
        CompoundTag nbt = player.namedTag;
        CompoundTag abilityTag = new CompoundTag();
        values.forEach((type, bool) -> {
            abilityTag.put(type.name(), new IntTag(bool ? 1 : 0));
        });
        nbt.put(KEY_ABILITIES, abilityTag);
        nbt.putString(KEY_PLAYER_PERMISSION, playerPermission.name());
        nbt.putString(KEY_COMMAND_PERMISSION, commandPermission.name());
    }

    /**
     * Reads permission data from NBT.
     *
     * @param nbt The NBT data to read from.
     */
    public void readNBT(CompoundTag nbt) {
        CompoundTag abilityTag = nbt.getCompound(KEY_ABILITIES);
        for (Map.Entry<String, Tag> e : abilityTag.getTags().entrySet()) {
            if (e.getValue() instanceof IntTag) {
                set(Type.valueOf(e.getKey()), ((IntTag) e.getValue()).getData() == 1);
            }
        }
        playerPermission = PlayerPermission.valueOf(nbt.getString(KEY_PLAYER_PERMISSION));
        commandPermission = CommandPermission.valueOf(nbt.getString(KEY_COMMAND_PERMISSION));
    }

    /**
     * Updates the adventure settings packet and sends it to the player.
     */
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

    /**
     * Enum representing different types of settings.
     */
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
        VERTICAL_FLY_SPEED(PlayerAbility.VERTICAL_FLY_SPEED, true);

        private final PlayerAbility ability;
        private final boolean defaultValue;

        Type(boolean defaultValue) {
            this.defaultValue = defaultValue;
            this.ability = null;
        }

        Type(PlayerAbility ability, boolean defaultValue) {
            this.ability = ability;
            this.defaultValue = defaultValue;
            if (this.ability != null) {
                ability2TypeMap.put(this.ability, this);
            }
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