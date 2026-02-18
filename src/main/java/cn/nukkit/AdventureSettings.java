package cn.nukkit;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.Tag;
import org.cloudburstmc.protocol.bedrock.data.PlayerAbilityHolder;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAbilitiesPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAdventureSettingsPacket;
import org.cloudburstmc.protocol.bedrock.data.AbilityLayer;
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermission;
import org.cloudburstmc.protocol.bedrock.data.Ability;
import org.cloudburstmc.protocol.bedrock.data.PlayerPermission;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * AdventureSettings class for managing player abilities and permissions.
 * @author MagicDroidX (Nukkit Project)
 */
public class AdventureSettings implements Cloneable {
    public static final String KEY_ABILITIES = "Abilities";
    public static final String KEY_PLAYER_PERMISSION = "PlayerPermission";
    public static final String KEY_COMMAND_PERMISSION = "CommandPermission";

    private static final Map<Ability, Type> ability2TypeMap = new HashMap<>();

    private final Map<Type, Boolean> values;

    @Getter
    private PlayerPermission playerPermission;

    @Getter
    @Setter
    private CommandPermission commandPermission;

    private Player player;

    public AdventureSettings(Player player) {
        this(player, null);
    }

    public AdventureSettings(Player player, CompoundTag nbt) {
        this.player = player;
        this.values = new EnumMap<>(Type.class);

        this.init(nbt);
    }

    public void setPlayerPermission(PlayerPermission playerPermission) {
        this.playerPermission = playerPermission;
        this.player.setOp(playerPermission == PlayerPermission.OPERATOR);
    }

    public void init(@Nullable CompoundTag nbt) {
        if (nbt != null && nbt.contains(KEY_ABILITIES)) {
            this.readNBT(nbt);
            this.opCheck();
            return;
        }

        boolean immutable = player.isAdventure() || player.isSpectator();
        set(Type.WORLD_IMMUTABLE, immutable);
        // !player.isAdventure() && !player.isSpectator()
        set(Type.WORLD_BUILDER, !immutable);
        set(Type.AUTO_JUMP, true);
        set(Type.ALLOW_FLIGHT, player.isCreative() || player.isSpectator());
        set(Type.NO_CLIP, player.isSpectator());
        set(Type.FLYING, player.isSpectator());
        set(Type.OPERATOR, player.isOp());
        set(Type.TELEPORT, player.isOp());

        commandPermission = player.isOp() ? CommandPermission.ADMIN : CommandPermission.ANY;
        playerPermission = player.isOp() ? PlayerPermission.OPERATOR : PlayerPermission.MEMBER;
    }

    private void opCheck() {
        // Offline de-op
        if (playerPermission == PlayerPermission.OPERATOR && !player.isOp()) {
            onOpChange(false);
        }

        // Offline by op
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

    public AdventureSettings set(PlayerAbilityHolder ability, boolean value) {
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

    public boolean get(AbilityLayer.Type ability) {
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
     * @param op is OP or not
     */
    public void onOpChange(boolean op) {
        if (op) {
            for (Type type : Type.values()) {
                if (type.isAbility()) {
                    set(type, true);
                }
            }
        }

        // Set op-specific attributes
        set(Type.OPERATOR, op);
        set(Type.TELEPORT, op);

        commandPermission = op ? CommandPermission.ADMIN : CommandPermission.ANY;

        // Don't override customization/guest status
        if (op && playerPermission != PlayerPermission.OPERATOR) {
            playerPermission = PlayerPermission.OPERATOR;
        }
        if (!op && playerPermission == PlayerPermission.OPERATOR) {
            playerPermission = PlayerPermission.MEMBER;
        }
    }

    public void sendAbilities(Collection<Player> players) {
        UpdateAbilitiesPacket packet = new UpdateAbilitiesPacket();
        packet.setUniqueEntityId(player.getId());
        packet.setCommandPermission(commandPermission);
        packet.setPlayerPermission(playerPermission);

        AbilityLayer layer = new AbilityLayer();
        layer.setVerticalFlySpeed(player.getVerticalFlySpeed());
        layer.setLayerType(AbilityLayer.Type.BASE);
        layer.getAbilitiesSet().addAll(Arrays.asList(Ability.values()));

        for (Type type : Type.values()) {
            if (type.isAbility() && get(type)) {
                layer.getAbilityValues().add(type.getAbility());
            }
        }

        if (player.isCreative()) {
            layer.getAbilityValues().add(Ability.INSTABUILD);
        }

        layer.getAbilityValues().add(Ability.WALK_SPEED);
        layer.getAbilityValues().add(Ability.FLY_SPEED);

        layer.setWalkSpeed(Player.DEFAULT_SPEED);
        layer.setFlySpeed(player.getHorizontalFlySpeed());

        packet.getAbilityLayers().add(layer);

        Server.broadcastPacket(players, packet);
    }

    /**
     * Save permissions to nbt
     */
    public void saveNBT() {
        CompoundTag nbt = player.namedTag;
        CompoundTag abilityTag = new CompoundTag();
        this.values.forEach((type, bool) -> abilityTag.put(type.name(), new IntTag(bool ? 1 : 0)));
        nbt.put(KEY_ABILITIES, abilityTag);
        nbt.putString(KEY_PLAYER_PERMISSION, playerPermission.name());
        nbt.putString(KEY_COMMAND_PERMISSION, commandPermission.name());
    }

    /**
     * Read permission data from nbt
     */
    public void readNBT(CompoundTag nbt) {
        CompoundTag abilityTag = nbt.getCompound(KEY_ABILITIES);
        for (Map.Entry<String, Tag> e : abilityTag.getTags().entrySet()) {
            if (e.getValue() instanceof IntTag tag) {
                Type type = Type.valueOf(e.getKey());
                this.set(type, tag.getData() == 1);
            }
        }
        playerPermission = PlayerPermission.valueOf(nbt.getString(KEY_PLAYER_PERMISSION));
        commandPermission = CommandPermission.valueOf(nbt.getString(KEY_COMMAND_PERMISSION));
    }

    public void updateAdventureSettings() {
        UpdateAdventureSettingsPacket adventurePacket = new UpdateAdventureSettingsPacket();
        adventurePacket.setAutoJump(get(Type.AUTO_JUMP));
        adventurePacket.setImmutableWorld(get(Type.WORLD_IMMUTABLE));
        adventurePacket.setNoMvP(get(Type.NO_MVP));
        adventurePacket.setNoPvM(get(Type.NO_PVM));
        adventurePacket.setShowNameTags(get(Type.SHOW_NAME_TAGS));

        player.dataPacket(adventurePacket);
        player.resetInAirTicks();
    }

    public enum Type {
        WORLD_IMMUTABLE(false),
        NO_PVM(false),
        NO_MVP(Ability.INVULNERABLE, false),
        SHOW_NAME_TAGS(false),
        AUTO_JUMP(true),
        ALLOW_FLIGHT(Ability.MAY_FLY, false),
        NO_CLIP(Ability.NO_CLIP, false),
        WORLD_BUILDER(Ability.WORLD_BUILDER, false),
        FLYING(Ability.FLYING, false),
        MUTED(Ability.MUTED, false),
        MINE(Ability.MINE, true),
        DOORS_AND_SWITCHED(Ability.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(Ability.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(Ability.ATTACK_PLAYERS, true),
        ATTACK_MOBS(Ability.ATTACK_MOBS, true),
        OPERATOR(Ability.OPERATOR_COMMANDS, false),
        TELEPORT(Ability.TELEPORT, false),
        BUILD(Ability.BUILD, true),
        PRIVILEGED_BUILDER(Ability.PRIVILEGED_BUILDER, false),
        VERTICAL_FLY_SPEED(Ability.VERTICAL_FLY_SPEED, true);

        private final Ability ability;
        private final boolean defaultValue;

        Type(boolean defaultValue) {
            this.defaultValue = defaultValue;
            this.ability = null;
        }

        Type(Ability ability, boolean defaultValue) {
            this.ability = ability;
            this.defaultValue = defaultValue;
            if (this.ability != null) {
                ability2TypeMap.put(this.ability, this);
            }
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }

        public Ability getAbility() {
            return this.ability;
        }

        public boolean isAbility() {
            return this.ability != null;
        }
    }
}
