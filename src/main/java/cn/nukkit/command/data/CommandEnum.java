package cn.nukkit.command.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author CreeperFace
 */
public class CommandEnum {
    
    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableList.of("true", "false"));

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode",
            ImmutableList.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_BLOCK;

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_ITEM;

    static {
        /*ImmutableList.Builder<String> blocks = ImmutableList.builder();
        for (Field field : BlockID.class.getDeclaredFields()) {
            blocks.add(field.getName().toLowerCase());
        }*/
        ENUM_BLOCK = new CommandEnum("Block", /*blocks.build()*/ Collections.emptyList());

        ENUM_ITEM = new CommandEnum("Item", /*ImmutableList.copyOf(Arrays.stream(MinecraftItemID.values())
            .filter(it -> !it.isTechnical())
            .filter(it -> !it.isEducationEdition())
            .flatMap(it -> Stream.of(Stream.of(it.getNamespacedId())*//*, Arrays.stream(it.getAliases())*//*).flatMap(Function.identity()))
            .map(it-> it.substring(10).toLowerCase())
            .toArray(String[]::new)
        )*/ Collections.emptyList());
    }

    private String name;
    private List<String> values;
    private boolean limited = true;

    @Since("1.4.0.0-PN")
    public CommandEnum(String name, String... values) {
        this(name, Arrays.asList(values));
    }

    public CommandEnum(String name, List<String> values) {
        this(name, values, true);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public CommandEnum(String name, List<String> values, boolean limited) {
        this.name = name;
        this.values = values;
        this.limited = limited;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public boolean isLimited() {
        return limited;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
