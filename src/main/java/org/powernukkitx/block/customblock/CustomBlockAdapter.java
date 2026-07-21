package org.powernukkitx.block.customblock;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter that bridges the new BlockBuilder component system
 * with the existing {@link CustomBlock} interface.
 * <p>
 * Use this to create a CustomBlock from a BlockBuilder definition
 * without implementing all methods manually.
 * <p>
 * Example:
 * <pre>{@code
 * CustomBlockDefinition def = BlockBuilder.create("myplugin:my_block")
 *     .name("My Block")
 *     .texture("my_texture")
 *     .build();
 *
 * CustomBlock block = new CustomBlockAdapter(def) {
 *     // Override specific methods as needed
 * };
 * }</pre>
 */
public abstract class CustomBlockAdapter implements CustomBlock, BlockToItemConverter {
    protected final CustomBlockDefinition definition;
    protected final String id;

    public CustomBlockAdapter(@NotNull CustomBlockDefinition definition) {
        this.definition = definition;
        this.id = definition.identifier();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public CustomBlockDefinition getDefinition() {
        return definition;
    }

    @Override
    public Item toItem() {
        // Default: create a placeholder item
        // Override this in subclasses for proper item conversion
        return null;
    }

    @Override
    public double getFrictionFactor() {
        var comp = definition.getComponents();
        if (comp.contains("minecraft:friction")) {
            return comp.getCompound("minecraft:friction").getFloat("value");
        }
        return 0.4;
    }

    @Override
    public double getResistance() {
        var comp = definition.getComponents();
        if (comp.contains("minecraft:destructible_by_explosion")) {
            return comp.getCompound("minecraft:destructible_by_explosion").getInt("explosion_resistance");
        }
        return 0;
    }

    @Override
    public int getLightFilter() {
        var comp = definition.getComponents();
        if (comp.contains("minecraft:light_dampening")) {
            return comp.getCompound("minecraft:light_dampening").getByte("lightLevel");
        }
        return 15;
    }

    @Override
    public int getLightLevel() {
        var comp = definition.getComponents();
        if (comp.contains("minecraft:light_emission")) {
            return comp.getCompound("minecraft:light_emission").getByte("emission");
        }
        return 0;
    }

    @Override
    public double getHardness() {
        var comp = definition.getComponents();
        if (comp.contains("minecraft:destructible_by_mining")) {
            float time = comp.getCompound("minecraft:destructible_by_mining").getFloat("value");
            // Convert mining time to hardness
            return time < 0 ? -1 : time * 1.5;
        }
        return 0;
    }

    @Override
    public Block toBlock() {
        return ((Block) this).clone();
    }
}