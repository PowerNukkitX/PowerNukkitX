package cn.nukkit.recipe.special;

import cn.nukkit.recipe.SmithingTrimRecipe;
import cn.nukkit.recipe.descriptor.ItemTagDescriptor;

public class SmithingArmorTrimCorrectedRecipe extends SmithingTrimRecipe {

    public SmithingArmorTrimCorrectedRecipe() {
        super("minecraft:smithing_armor_trim_corrected",
                new ItemTagDescriptor("minecraft:trimmable_armors", 1),
                new ItemTagDescriptor("minecraft:trim_materials", 1),
                new ItemTagDescriptor("minecraft:trim_templates", 1),
                "smithing_table");
    }
}
