package cn.nukkit.command.selector;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import lombok.Getter;

/**
 * 所有可能的选择器类型
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public enum SelectorType {
    ALL_PLAYERS("a"),
    ALL_ENTITIES("e"),
    RANDOM_PLAYER("r"),
    SELF("s"),
    NEAREST_PLAYER("p"),
    NPC_INITIATOR("initiator");

    @Getter
    private final String token;

    SelectorType(String token) {
        this.token = token;
    }

    public static SelectorType parseSelectorType(String type) {
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
