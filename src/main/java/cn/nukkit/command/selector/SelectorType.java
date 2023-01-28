package cn.nukkit.command.selector;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import lombok.Getter;

/**
 * 所有可能的选择器类型
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public enum SelectorType {
    ALL_PLAYERS("a"),
    ALL_ENTITIES("e"),
    //可通过指定type选择随机非玩家实体
    //https://minecraft.fandom.com/zh/wiki/%E7%9B%AE%E6%A0%87%E9%80%89%E6%8B%A9%E5%99%A8
    RANDOM_PLAYER("r"),
    SELF("s"),
    NEAREST_PLAYER("p"),
    NPC_INITIATOR("initiator");

    @Getter
    private final String token;

    SelectorType(String token) {
        this.token = token;
    }

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
