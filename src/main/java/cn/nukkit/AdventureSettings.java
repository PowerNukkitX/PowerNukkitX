package cn.nukkit;

import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.AbilitiesIndex;
import org.cloudburstmc.protocol.bedrock.data.PlayerPermissionLevel;
import org.cloudburstmc.protocol.bedrock.data.SerializedAbilitiesData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermissionLevel;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAbilitiesPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAdventureSettingsPacket;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * AdventureSettings class for managing player abilities and permissions.
 *
 * @author MagicDroidX (Nukkit Project)
 */
public class AdventureSettings implements Cloneable {
    public static final String KEY_ABILITIES = "Abilities";
    public static final String KEY_PLAYER_PERMISSION = "PlayerPermission";
    public static final String KEY_COMMAND_PERMISSION = "CommandPermission";

    private static final Map<AbilitiesIndex, Type> ability2TypeMap = new HashMap<>();

    //Controllable capabilities in the permission list
    public static final AbilitiesIndex[] CONTROLLABLE_ABILITIES = new AbilitiesIndex[]{
            AbilitiesIndex.BUILD,
            AbilitiesIndex.MINE,
            AbilitiesIndex.DOORS_AND_SWITCHES,
            AbilitiesIndex.OPEN_CONTAINERS,
            AbilitiesIndex.ATTACK_PLAYERS,
            AbilitiesIndex.ATTACK_MOBS,
            AbilitiesIndex.OPERATOR_COMMANDS,
            AbilitiesIndex.TELEPORT
    };

    private final Map<Type, Boolean> values;

    @Getter
    private PlayerPermissionLevel playerPermission;

    @Getter
    @Setter
    private CommandPermissionLevel commandPermission;

    private Player player;

    public AdventureSettings(Player player) {
        this(player, null);
    }

    public AdventureSettings(Player player, NbtMap nbt) {
        this.player = player;
        this.values = new EnumMap<>(Type.class);

        this.init(nbt);
    }

    public void setPlayerPermission(PlayerPermissionLevel playerPermission) {
        this.playerPermission = playerPermission;
        this.player.setOp(playerPermission == PlayerPermissionLevel.OPERATOR);
    }

    public void init(@Nullable NbtMap nbt) {
        if (nbt != null && nbt.containsKey(KEY_ABILITIES)) {
            this.readNBT(nbt);
            this.opCheck();
            return;
        }

        boolean immutable = this.player.isAdventure() || this.player.isSpectator();
        set(Type.WORLD_IMMUTABLE, immutable);
        // !player.isAdventure() && !player.isSpectator()
        set(Type.WORLD_BUILDER, !immutable);
        set(Type.AUTO_JUMP, true);
        set(Type.ALLOW_FLIGHT, this.player.isCreative() || this.player.isSpectator());
        set(Type.NO_CLIP, this.player.isSpectator());
        set(Type.FLYING, this.player.isSpectator());
        set(Type.OPERATOR, this.player.isOp());
        set(Type.TELEPORT, this.player.isOp());

        this.commandPermission = this.player.isOp() ? CommandPermissionLevel.GAME_DIRECTORS : CommandPermissionLevel.ANY;
        this.playerPermission = this.player.isOp() ? PlayerPermissionLevel.OPERATOR : PlayerPermissionLevel.MEMBER;
    }

    private void opCheck() {
        // Offline de-op
        if (this.playerPermission == PlayerPermissionLevel.OPERATOR && !this.player.isOp()) {
            onOpChange(false);
        }

        // Offline by op
        if (this.playerPermission != PlayerPermissionLevel.OPERATOR && this.player.isOp()) {
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

    public AdventureSettings set(AbilitiesIndex ability, boolean value) {
        Type type = ability2TypeMap.get(ability);
        if (type != null) {
            this.values.put(type, value);
        }
        return this;
    }

    public AdventureSettings set(Type type, boolean value) {
        this.values.put(type, value);
        return this;
    }

    public boolean get(AbilitiesIndex ability) {
        Type type = ability2TypeMap.get(ability);
        if (type == null) {
            throw new IllegalArgumentException("Unknown ability: " + ability);
        }
        return this.values.getOrDefault(type, type.getDefaultValue());
    }

    public boolean get(Type type) {
        return this.values.getOrDefault(type, type.getDefaultValue());
    }

    public void update() {
        // Permission to send to all players so they can see each other
        // Make sure it will be sent to yourself (e.g.: there is no such player among the online players when the player enters the server)
        Collection<Player> players = new HashSet<>(this.player.getServer().getOnlinePlayers().values());
        sendAbilities(players);
        updateAdventureSettings();
    }


    /**
     * This method will be called when the player's OP status changes.
     * Note that this method does not send a packet to the client to refresh the privilege information; you need to manually call the update() method to do so
     *
     * @param op is OP or not
     */
    public void onOpChange(boolean op) {
        if (op) {
            for (AbilitiesIndex controllableAbility : CONTROLLABLE_ABILITIES) {
                set(controllableAbility, true);
            }
        }

        // Set op-specific attributes
        set(Type.OPERATOR, op);
        set(Type.TELEPORT, op);

        this.commandPermission = op ? CommandPermissionLevel.GAME_DIRECTORS : CommandPermissionLevel.ANY;

        // Don't override customization/guest status
        if (op && this.playerPermission != PlayerPermissionLevel.OPERATOR) {
            this.playerPermission = PlayerPermissionLevel.OPERATOR;
        }
        if (!op && playerPermission == PlayerPermissionLevel.OPERATOR) {
            this.playerPermission = PlayerPermissionLevel.MEMBER;
        }
    }

    public void sendAbilities(Collection<Player> players) {
        final UpdateAbilitiesPacket packet = new UpdateAbilitiesPacket();
        final SerializedAbilitiesData data = new SerializedAbilitiesData();
        data.setTargetPlayerRawId(this.player.getId());
        data.setPlayerPermissions(this.playerPermission);
        data.setCommandPermissions(this.commandPermission);

        final SerializedAbilitiesData.SerializedLayer layer = new SerializedAbilitiesData.SerializedLayer();
        layer.setSerializedLayer(SerializedAbilitiesData.SerializedLayer.SerializedAbilitiesLayer.BASE);
        layer.getAbilitiesSet().addAll(List.of(AbilitiesIndex.values()));

        for (final Type type : Type.values()) {
            if (type.isAbility() && this.get(type)) {
                layer.getAbilityValues().add(type.getAbility());
            }
        }
        if (this.player.isCreative()) {
            layer.getAbilityValues().add(AbilitiesIndex.INSTABUILD);
        }
        layer.getAbilityValues().add(AbilitiesIndex.WALK_SPEED);
        layer.getAbilityValues().add(AbilitiesIndex.FLY_SPEED);
        layer.getAbilityValues().add(AbilitiesIndex.VERTICAL_FLY_SPEED);
        layer.setWalkSpeed(Player.DEFAULT_SPEED);
        layer.setFlySpeed(this.player.getHorizontalFlySpeed());
        layer.setVerticalFlySpeed(this.player.getVerticalFlySpeed());

        data.getLayers().add(layer);

        packet.setData(data);

        Server.broadcastPacket(players, packet);
    }

    /**
     * Save permissions to nbt
     */
    public void saveNBT() {
        NbtMapBuilder abilityTag = NbtMap.builder();
        this.values.forEach((type, bool) -> abilityTag.putInt(type.name(), (bool ? 1 : 0)));
        this.player.namedTag = this.player.namedTag.toBuilder()
                .putList(KEY_ABILITIES, NbtType.COMPOUND, abilityTag.build())
                .putString(KEY_PLAYER_PERMISSION, this.playerPermission.name())
                .putString(KEY_COMMAND_PERMISSION, this.commandPermission.name())
                .build();
    }

    /**
     * Read permission data from nbt
     */
    public void readNBT(NbtMap nbt) {
        NbtMap abilityTag = nbt.getCompound(KEY_ABILITIES);
        for (Map.Entry<String, Object> e : abilityTag.entrySet()) {
            if (e.getValue() instanceof Integer tag) {
                Type type = Type.valueOf(e.getKey());
                this.set(type, tag == 1);
            }
        }
        this.playerPermission = PlayerPermissionLevel.valueOf(nbt.getString(KEY_PLAYER_PERMISSION));
        this.commandPermission = CommandPermissionLevel.valueOf(nbt.getString(KEY_COMMAND_PERMISSION));
    }

    public void updateAdventureSettings() {
        final UpdateAdventureSettingsPacket packet = new UpdateAdventureSettingsPacket();
        packet.setNoPvM(this.get(Type.NO_PVM));
        packet.setNoMvP(this.get(Type.NO_MVP));
        packet.setImmutableWorld(this.get(Type.WORLD_IMMUTABLE));
        packet.setShowNameTags(this.get(Type.SHOW_NAME_TAGS));
        packet.setImmutableWorld(this.get(Type.AUTO_JUMP));

        this.player.sendPacket(packet);
        this.player.resetInAirTicks();
    }

    public enum Type {
        WORLD_IMMUTABLE(false),
        NO_PVM(false),
        NO_MVP(AbilitiesIndex.INVULNERABLE, false),
        SHOW_NAME_TAGS(false),
        AUTO_JUMP(true),
        ALLOW_FLIGHT(AbilitiesIndex.MAY_FLY, false),
        NO_CLIP(AbilitiesIndex.NO_CLIP, false),
        WORLD_BUILDER(AbilitiesIndex.WORLD_BUILDER, false),
        FLYING(AbilitiesIndex.FLYING, false),
        MUTED(AbilitiesIndex.MUTED, false),
        MINE(AbilitiesIndex.MINE, true),
        DOORS_AND_SWITCHED(AbilitiesIndex.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(AbilitiesIndex.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(AbilitiesIndex.ATTACK_PLAYERS, true),
        ATTACK_MOBS(AbilitiesIndex.ATTACK_MOBS, true),
        OPERATOR(AbilitiesIndex.OPERATOR_COMMANDS, false),
        TELEPORT(AbilitiesIndex.TELEPORT, false),
        BUILD(AbilitiesIndex.BUILD, true),
        PRIVILEGED_BUILDER(AbilitiesIndex.PRIVILEGED_BUILDER, false),
        VERTICAL_FLY_SPEED(AbilitiesIndex.VERTICAL_FLY_SPEED, true);

        private final AbilitiesIndex ability;
        private final boolean defaultValue;

        Type(boolean defaultValue) {
            this.defaultValue = defaultValue;
            this.ability = null;
        }

        Type(AbilitiesIndex ability, boolean defaultValue) {
            this.ability = ability;
            this.defaultValue = defaultValue;
            if (this.ability != null) {
                ability2TypeMap.put(this.ability, this);
            }
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }

        public AbilitiesIndex getAbility() {
            return this.ability;
        }

        public boolean isAbility() {
            return this.ability != null;
        }
    }
}