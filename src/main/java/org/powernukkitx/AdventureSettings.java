package org.powernukkitx;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.NumberTag;

import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.data.AbilitiesIndex;
import org.cloudburstmc.protocol.bedrock.data.PlayerPermissionLevel;
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermissionLevel;
import org.cloudburstmc.protocol.bedrock.data.payload.abilities.SerializedAbilitiesData;
import org.cloudburstmc.protocol.bedrock.data.payload.abilities.SerializedAbilitiesDataSerializedLayer;
import org.cloudburstmc.protocol.bedrock.data.payload.abilities.SerializedLayer;
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
    public static final String KEY_ABILITIES = "abilities";
    public static final String KEY_PLAYER_PERMISSION = "playerPermissionsLevel";
    public static final String KEY_COMMAND_PERMISSION = "permissionsLevel";
    private static final String PNX_EXTRA_ADVENTURE_SETTINGS = "AdventureSettings";

    private static final Map<AbilitiesIndex, Type> ability2TypeMap = new HashMap<>();

    private static final Type[] PNX_EXTRA_TYPES = new Type[]{
            Type.WORLD_IMMUTABLE,
            Type.NO_PVM,
            Type.SHOW_NAME_TAGS,
            Type.AUTO_JUMP,
            Type.NO_CLIP,
            Type.WORLD_BUILDER,
            Type.MUTED,
            Type.PRIVILEGED_BUILDER,
            Type.VERTICAL_FLY_SPEED
    };

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

    public AdventureSettings(Player player, CompoundTag nbt) {
        this.player = player;
        this.values = new EnumMap<>(Type.class);

        this.init(nbt);
    }

    public void setPlayerPermission(PlayerPermissionLevel playerPermission) {
        this.playerPermission = playerPermission;
        this.player.setOp(playerPermission == PlayerPermissionLevel.OPERATOR);
    }

    public void init(@Nullable CompoundTag nbt) {
        boolean immutable = this.player.isAdventure() || this.player.isSpectator();

        set(Type.WORLD_IMMUTABLE, immutable);
        set(Type.WORLD_BUILDER, !immutable);
        set(Type.AUTO_JUMP, true);
        set(Type.ALLOW_FLIGHT, this.player.isCreative() || this.player.isSpectator());
        set(Type.NO_CLIP, this.player.isSpectator());
        set(Type.FLYING, this.player.isSpectator());
        set(Type.OPERATOR, this.player.isOp());
        set(Type.TELEPORT, this.player.isOp());

        this.commandPermission = this.player.isOp()
                ? CommandPermissionLevel.GAME_DIRECTORS
                : CommandPermissionLevel.ANY;

        this.playerPermission = this.player.isOp()
                ? PlayerPermissionLevel.OPERATOR
                : PlayerPermissionLevel.MEMBER;

        if (nbt != null && nbt.containsCompound(KEY_ABILITIES)) {
            this.readNBT(nbt);
            this.opCheck();
        }

        this.readPnxExtraNBT();
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
        packet.setData(this.buildSerializedAbilitiesData());

        Server.broadcastPacket(players, packet);
    }

    public SerializedAbilitiesData buildSerializedAbilitiesData() {
        final SerializedAbilitiesData data = new SerializedAbilitiesData();
        data.setTargetPlayerRawId(this.player.uniqueIdLong());
        data.setPlayerPermissions(this.playerPermission);
        data.setCommandPermissions(this.commandPermission);

        final SerializedAbilitiesDataSerializedLayer layer = new SerializedAbilitiesDataSerializedLayer();
        layer.setSerializedLayer(SerializedLayer.BASE);
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
        return data;
    }

    /**
     * Save permissions to nbt
     */
    public void saveNBT() {
        CompoundTag nbt = player.getNbt();
        CompoundTag abilityTag = nbt.containsCompound(KEY_ABILITIES) ? nbt.getCompound(KEY_ABILITIES).copy() : new CompoundTag();

        abilityTag.putByte("attackmobs", this.get(Type.ATTACK_MOBS) ? 1 : 0);
        abilityTag.putByte("attackplayers", this.get(Type.ATTACK_PLAYERS) ? 1 : 0);
        abilityTag.putByte("build", this.get(Type.BUILD) ? 1 : 0);
        abilityTag.putByte("doorsandswitches", this.get(Type.DOORS_AND_SWITCHED) ? 1 : 0);
        abilityTag.putFloat("flySpeed", this.player.getHorizontalFlySpeed());
        abilityTag.putByte("flying", this.get(Type.FLYING) ? 1 : 0);
        abilityTag.putByte("instabuild", this.player.isCreative() ? 1 : 0);
        abilityTag.putByte("invulnerable", this.get(Type.NO_MVP) ? 1 : 0);

        if (!abilityTag.containsByte("lightning")) {
            abilityTag.putByte("lightning", 0);
        }

        abilityTag.putByte("mayfly", this.get(Type.ALLOW_FLIGHT) ? 1 : 0);
        abilityTag.putByte("mine", this.get(Type.MINE) ? 1 : 0);
        abilityTag.putByte("op", this.get(Type.OPERATOR) ? 1 : 0);
        abilityTag.putByte("opencontainers", this.get(Type.OPEN_CONTAINERS) ? 1 : 0);
        abilityTag.putByte("teleport", this.get(Type.TELEPORT) ? 1 : 0);
        abilityTag.putFloat("verticalFlySpeed", this.player.getVerticalFlySpeed());

        if (!abilityTag.containsFloat("walkSpeed")) {
            abilityTag.putFloat("walkSpeed", Player.DEFAULT_SPEED);
        }

        nbt.putCompound(KEY_ABILITIES, abilityTag);
        nbt.putInt(KEY_COMMAND_PERMISSION, toStorageCommandPermission(this.commandPermission));
        nbt.putInt(KEY_PLAYER_PERMISSION, toStoragePlayerPermission(this.playerPermission));

        this.savePnxExtraNBT();
    }

    /**
     * Read permission data from nbt
     */
    public void readNBT(CompoundTag nbt) {
        CompoundTag abilityTag = nbt.getCompound(KEY_ABILITIES);

        readBooleanAbility(abilityTag, "attackmobs", Type.ATTACK_MOBS);
        readBooleanAbility(abilityTag, "attackplayers", Type.ATTACK_PLAYERS);
        readBooleanAbility(abilityTag, "build", Type.BUILD);
        readBooleanAbility(abilityTag, "doorsandswitches", Type.DOORS_AND_SWITCHED);

        if (abilityTag.containsFloat("flySpeed")) {
            this.player.horizontalFlySpeed = abilityTag.getFloat("flySpeed");
        }

        readBooleanAbility(abilityTag, "flying", Type.FLYING);
        readBooleanAbility(abilityTag, "invulnerable", Type.NO_MVP);
        readBooleanAbility(abilityTag, "mayfly", Type.ALLOW_FLIGHT);
        readBooleanAbility(abilityTag, "mine", Type.MINE);
        readBooleanAbility(abilityTag, "op", Type.OPERATOR);
        readBooleanAbility(abilityTag, "opencontainers", Type.OPEN_CONTAINERS);
        readBooleanAbility(abilityTag, "teleport", Type.TELEPORT);

        if (abilityTag.containsFloat("verticalFlySpeed")) {
            this.player.verticalFlySpeed = abilityTag.getFloat("verticalFlySpeed");
        }

        if (nbt.containsInt(KEY_PLAYER_PERMISSION)) {
            this.playerPermission = fromStoragePlayerPermission(nbt.getInt(KEY_PLAYER_PERMISSION));
        }

        if (nbt.containsInt(KEY_COMMAND_PERMISSION)) {
            this.commandPermission = fromStorageCommandPermission(nbt.getInt(KEY_COMMAND_PERMISSION));
        }
    }

    private void readPnxExtraNBT() {
        CompoundTag pnxExtra = this.player.pnxExtraNbt;

        if (!pnxExtra.containsCompound(PNX_EXTRA_ADVENTURE_SETTINGS)) return;

        CompoundTag adventureSettings = pnxExtra.getCompound(PNX_EXTRA_ADVENTURE_SETTINGS);

        for (Type type : PNX_EXTRA_TYPES) {
            if (adventureSettings.get(type.name()) instanceof NumberTag<?> number) {
                this.set(type, number.getData().intValue() != 0);
            }
        }
    }

    private void savePnxExtraNBT() {
        CompoundTag pnxExtra = this.player.pnxExtraNbt;

        CompoundTag existingAdventureSettings = pnxExtra.containsCompound(PNX_EXTRA_ADVENTURE_SETTINGS)
                ? pnxExtra.getCompound(PNX_EXTRA_ADVENTURE_SETTINGS)
                : null;

        CompoundTag adventureSettings = existingAdventureSettings != null
                ? existingAdventureSettings.copy()
                : new CompoundTag();

        for (Type type : PNX_EXTRA_TYPES) {
            if (this.values.containsKey(type)) {
                adventureSettings.putInt(type.name(), this.get(type) ? 1 : 0);
            } else {
                adventureSettings.remove(type.name());
            }
        }

        if (adventureSettings.isEmpty()) {
            if (pnxExtra.contains(PNX_EXTRA_ADVENTURE_SETTINGS)) {
                pnxExtra.remove(PNX_EXTRA_ADVENTURE_SETTINGS);
                this.player.pnxExtraDirty = true;
            }
        } else if (!adventureSettings.equals(existingAdventureSettings)) {
            pnxExtra.putCompound(PNX_EXTRA_ADVENTURE_SETTINGS, adventureSettings);
            this.player.pnxExtraDirty = true;
        }
    }

    private void readBooleanAbility(CompoundTag abilities, String name, Type type) {
        if (abilities.containsByte(name)) {
            this.set(type, abilities.getBoolean(name));
        }
    }

    private static int toStoragePlayerPermission(PlayerPermissionLevel permission) {
        return switch (permission) {
            case VISITOR -> 0;
            case MEMBER -> 1;
            case OPERATOR -> 2;
            case CUSTOM -> 3;
        };
    }

    private static PlayerPermissionLevel fromStoragePlayerPermission(int permission) {
        return switch (permission) {
            case 0 -> PlayerPermissionLevel.VISITOR;
            case 2 -> PlayerPermissionLevel.OPERATOR;
            case 3 -> PlayerPermissionLevel.CUSTOM;
            default -> PlayerPermissionLevel.MEMBER;
        };
    }

    private static int toStorageCommandPermission(CommandPermissionLevel permission) {
        return switch (permission) {
            case ANY -> 0;
            case GAME_DIRECTORS -> 1;
            case ADMIN -> 2;
            case HOST -> 3;
            case OWNER -> 4;
            case INTERNAL -> 5;
        };
    }

    private static CommandPermissionLevel fromStorageCommandPermission(int permission) {
        return switch (permission) {
            case 1 -> CommandPermissionLevel.GAME_DIRECTORS;
            case 2 -> CommandPermissionLevel.ADMIN;
            case 3 -> CommandPermissionLevel.HOST;
            case 4 -> CommandPermissionLevel.OWNER;
            case 5 -> CommandPermissionLevel.INTERNAL;
            default -> CommandPermissionLevel.ANY;
        };
    }

    public void updateAdventureSettings() {
        this.sendAdventureSettings(false);
    }

    /**
     * Sends the player's adventure settings immediately.
     * <p>
     * This is intended for ordered protocol initialization
     * Normal runtime updates should continue using {@link #updateAdventureSettings()}.
     * </p>
     */
    public void updateAdventureSettingsImmediately() {
        this.sendAdventureSettings(true);
    }

    private void sendAdventureSettings(boolean immediately) {
        final UpdateAdventureSettingsPacket packet = new UpdateAdventureSettingsPacket();
        packet.setAutoJump(this.get(Type.AUTO_JUMP));
        packet.setNoPvM(this.get(Type.NO_PVM));
        packet.setNoMvP(this.get(Type.NO_MVP));
        packet.setImmutableWorld(this.get(Type.WORLD_IMMUTABLE));
        packet.setShowNameTags(this.get(Type.SHOW_NAME_TAGS));

        if (immediately) {
            this.player.sendPacketImmediately(packet);
        } else {
            this.player.sendPacket(packet);
        }

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
