package cn.nukkit.command.utils;

import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.nbt.tag.CompoundTag;


public final class CommandUtils {
    public static boolean isHiddenInCommands(CustomBlock block) {
        CompoundTag nbt = block.getDefinition().nbt();
        CompoundTag menuCategory = nbt.getCompound("menu_category");
        return menuCategory.getByte("is_hidden_in_commands") == 1;
    }

    public static boolean isHiddenInCommands(CustomItem item) {
        CompoundTag nbt = item.getDefinition().nbt();
        CompoundTag components = nbt.getCompound("components");
        if (components.contains("item_properties")) {
            CompoundTag itemProps = components.getCompound("item_properties");
            return itemProps.getByte("is_hidden_in_commands") == 1;
        }
        return false;
    }
}