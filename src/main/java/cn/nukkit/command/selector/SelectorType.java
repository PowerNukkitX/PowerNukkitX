package cn.nukkit.command.selector;

import cn.nukkit.command.exceptions.SelectorSyntaxException;
import lombok.Getter;

/**
 * Enumerates all possible selector types used in command parsing for PowerNukkitX.
 * <p>
 * SelectorType defines the valid tokens that can be used with Minecraft selectors (e.g., @a, @e, @p, @r, @s, @initiator)
 * to target players, entities, or special command sources. Each enum constant is associated with a string token
 * that matches the selector's identifier in command syntax.
 * <p>
 * Features:
 * <ul>
 *   <li>Provides a type-safe representation of all supported selector types.</li>
 *   <li>Associates each selector type with its corresponding token (e.g., "a" for all players).</li>
 *   <li>Supports parsing from string tokens to enum values via {@link #parseSelectorType(String)}.</li>
 *   <li>Throws {@link cn.nukkit.command.exceptions.SelectorSyntaxException} for unknown selector types.</li>
 *   <li>Includes support for NPC initiator and random non-player entity selection.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Use {@link #parseSelectorType(String)} to convert a selector token to its enum value.</li>
 *   <li>Access the selector token for display or serialization using {@link #getToken()}.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * SelectorType type = SelectorType.parseSelectorType("a"); // ALL_PLAYERS
 * String token = type.getToken(); // "a"
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.exceptions.SelectorSyntaxException
 * @since PowerNukkitX 2.0.0
 */
public enum SelectorType {
    /**
     * Selects all players (@a).
     */
    ALL_PLAYERS("a"),
    /**
     * Selects all entities (@e).
     */
    ALL_ENTITIES("e"),
    /**
     * Selects a random player (@r). Can select a random non-player entity by specifying the type.
     * @see: https://minecraft.fandom.com/wiki/Target_selectors
     */
    RANDOM_PLAYER("r"),
    /**
     * Selects the command executor itself (@s).
     */
    SELF("s"),
    /**
     * Selects the nearest player (@p).
     */
    NEAREST_PLAYER("p"),
    /**
     * Selects the NPC initiator (for NPC command execution).
     */
    NPC_INITIATOR("initiator");

    /**
     * The string token used in selector syntax (e.g., "a", "e", "p").
     */
    @Getter
    private final String token;

    /**
     * Constructs a SelectorType with the given token.
     *
     * @param token the selector token
     */
    SelectorType(String token) {
        this.token = token;
    }

    /**
     * Parses a string token and returns the corresponding {@link SelectorType}.
     * <p>
     * Throws {@link cn.nukkit.command.exceptions.SelectorSyntaxException} if the token is not recognized.
     *
     * @param type the selector token (e.g., "a", "e", "p", "r", "s", "initiator")
     * @return the matching SelectorType
     * @throws SelectorSyntaxException if the token is unknown
     */
    public static SelectorType parseSelectorType(String type) throws SelectorSyntaxException {
        return switch (type) {
            case "a" -> SelectorType.ALL_PLAYERS;
            case "e" -> SelectorType.ALL_ENTITIES;
            case "r" -> SelectorType.RANDOM_PLAYER;
            case "s" -> SelectorType.SELF;
            case "p" -> SelectorType.NEAREST_PLAYER;
            case "initiator" -> SelectorType.NPC_INITIATOR;
            default -> throw new SelectorSyntaxException("Unknown selector type: " + type);
        };
    }
}
