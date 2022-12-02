package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public interface SmeltingRecipe extends Recipe {
    @PowerNukkitOnly
    Item getInput();
}
