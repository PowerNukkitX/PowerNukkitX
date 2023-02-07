package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.RequiredArgsConstructor;

import org.jetbrains.annotations.NotNull;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
@RequiredArgsConstructor
public enum StructureBlockType {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    INVALID("Structure Block"),

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    DATA("Data Structure Block"),

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    SAVE("Save Structure Block"),

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    LOAD("Load Structure Block"),

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    CORNER("Corner Structure Block"),

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    EXPORT("Export Structure Block");

    private final String englishName;

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    public String getEnglishName() {
        return englishName;
    }

    private static final StructureBlockType[] VALUES = StructureBlockType.values();

    public static StructureBlockType from(int id) {
        return VALUES[id];
    }
}
