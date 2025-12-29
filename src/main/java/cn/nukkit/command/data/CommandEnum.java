package cn.nukkit.command.data;

import cn.nukkit.Server;
import cn.nukkit.camera.data.CameraPreset;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.protocol.UpdateSoftEnumPacket;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.function.Supplier;

/**
 * Represents an enumeration of possible values for command arguments in PowerNukkitX.
 * <p>
 * This class is used to define a set of valid options for a command parameter, supporting both static and dynamic value lists.
 * CommandEnum can be used for auto-completion, validation, and client-side display of command options.
 * <p>
 * Features:
 * <ul>
 *   <li>Supports static value lists and dynamic value suppliers for runtime updates.</li>
 *   <li>Provides built-in enums for enchantments, effects, functions, scoreboards, camera presets, booleans, gamemodes, blocks, items, and entities.</li>
 *   <li>Allows creation of soft enums, which can be updated dynamically and treated as strings by the client.</li>
 *   <li>Integrates with {@link UpdateSoftEnumPacket} for client-side updates.</li>
 *   <li>Supports custom enums for chained command options and other use cases.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with a name and a list of values, or a supplier for dynamic values.</li>
 *   <li>Use {@link #getValues()} to retrieve the current list of options.</li>
 *   <li>Call {@link #updateSoftEnum(UpdateSoftEnumPacket.Type, String...)} or {@link #updateSoftEnum()} to update client-side enums.</li>
 *   <li>Use built-in static instances for common command options.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandEnum myEnum = new CommandEnum("Color", Arrays.asList("red", "green", "blue"));
 * List<String> options = myEnum.getValues();
 * </pre>
 *
 * @author CreeperFace
 * @see UpdateSoftEnumPacket
 * @see Supplier
 */
public class CommandEnum {

    public static final CommandEnum ENUM_ENCHANTMENT = new CommandEnum("enchantmentName", () -> Enchantment.getEnchantmentName2IDMap().keySet().stream()
            .map(name -> name.startsWith(Identifier.DEFAULT_NAMESPACE) ? name.substring(10) : name)
            .toList());

    public static final CommandEnum ENUM_EFFECT = new CommandEnum("Effect", Registries.EFFECT.getEffectStringId2TypeMap()
            .keySet()
            .stream()
            .toList());

    public static final CommandEnum FUNCTION_FILE = new CommandEnum("filepath", () -> Server.getInstance().getFunctionManager().getFunctions().keySet());

    public static final CommandEnum SCOREBOARD_OBJECTIVES = new CommandEnum("ScoreboardObjectives", () -> Server.getInstance().getScoreboardManager().getScoreboards().keySet());

    public static final CommandEnum CAMERA_PRESETS = new CommandEnum("preset", () -> CameraPreset.getPresets().keySet());

    public static final CommandEnum CHAINED_COMMAND_ENUM = new CommandEnum("ExecuteChainedOption_0", "run", "as", "at", "positioned", "if", "unless", "in", "align", "anchored", "rotated", "facing");

    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableList.of("true", "false"));

    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode", ImmutableList.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));

    public static final CommandEnum ENUM_BLOCK = new CommandEnum("Block", Collections.emptyList());

    public static final CommandEnum ENUM_ITEM = new CommandEnum("Item", Collections.emptyList());

    public static final CommandEnum ENUM_ENTITY = new CommandEnum("Entity", Collections.emptyList());

    /**
     * The name of the enum, used for display and identification.
     */
    private final String name;
    /**
     * The static list of values for the enum (may be null for soft enums).
     */
    private final List<String> values;
    /**
     * Indicates if this is a soft enum (dynamic, treated as string by client).
     */
    private final boolean soft;
    /**
     * The supplier for dynamic value lists (used for soft enums).
     */
    private final Supplier<Collection<String>> supplier;

    /**
     * Constructs a static CommandEnum with a name and array of values.
     *
     * @param name the name of the enum
     * @param values the array of values
     */
    public CommandEnum(String name, String... values) {
        this(name, Arrays.asList(values));
    }

    /**
     * Constructs a static CommandEnum with a name and list of values.
     *
     * @param name the name of the enum
     * @param values the list of values
     */
    public CommandEnum(String name, List<String> values) {
        this(name, values, false);
    }

    /**
     * Constructs a CommandEnum with a name, list of values, and soft flag.
     * <p>
     * If soft is true, the client treats the enum as a string and values may be updated dynamically.
     *
     * @param name the name of the enum
     * @param values the list of values
     * @param soft true for soft enum, false for static
     */
    public CommandEnum(String name, List<String> values, boolean soft) {
        this.name = name;
        this.values = values;
        this.soft = soft;
        this.supplier = null;
    }

    /**
     * Constructs a soft CommandEnum with a name and a supplier for dynamic values.
     *
     * @param name the name of the enum
     * @param supplier the supplier providing the value collection
     */
    public CommandEnum(String name, Supplier<Collection<String>> supplier) {
        this.name = name;
        this.values = null;
        this.soft = true;
        this.supplier = supplier;
    }

    /**
     * Gets the name of the enum.
     *
     * @return the enum name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current list of values for the enum.
     * <p>
     * For soft enums, retrieves values from the supplier; for static enums, returns the static list.
     *
     * @return the list of enum values
     */
    public List<String> getValues() {
        if (this.supplier == null) {
            return values;
        } else {
            return supplier.get().stream().toList();
        }
    }

    /**
     * Checks if this is a soft enum (dynamic, treated as string by client).
     *
     * @return true if soft enum, false otherwise
     */
    public boolean isSoft() {
        return soft;
    }

    /**
     * Updates the client-side soft enum with the specified values and update type.
     * <p>
     * Only applicable for soft enums.
     *
     * @param mode the update type (SET, ADD, REMOVE)
     * @param value the values to update
     */
    public void updateSoftEnum(UpdateSoftEnumPacket.Type mode, String... value) {
        if (!this.soft) return;
        UpdateSoftEnumPacket packet = new UpdateSoftEnumPacket();
        packet.name = this.getName();
        packet.values = Arrays.stream(value).toList();
        packet.type = mode;
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), packet);
    }

    /**
     * Updates the client-side soft enum with the current values.
     * <p>
     * Only applicable for soft enums with a supplier.
     */
    public void updateSoftEnum() {
        if (!this.soft && this.supplier == null) return;
        UpdateSoftEnumPacket packet = new UpdateSoftEnumPacket();
        packet.name = this.getName();
        packet.values = this.getValues();
        packet.type = UpdateSoftEnumPacket.Type.SET;
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), packet);
    }

    /**
     * Returns the hash code for this enum, based on its name.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
