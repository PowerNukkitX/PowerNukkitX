package cn.nukkit.item.utils;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import java.util.*;


public final class RepairEntry {
    private static final short EXPR_VERSION = 13;

    private final List<String> itemIds = new ArrayList<>();
    private Float  numericAmount;
    private String amountExpression;

    private RepairEntry() {}

    public static RepairEntry set(String... ids) {
        RepairEntry e = new RepairEntry();
        if (ids != null) for (String id : ids) if (id != null && !id.isBlank()) e.itemIds.add(id);
        return e;
    }

    public static RepairEntry set(Item... items) {
        RepairEntry e = new RepairEntry();
        if (items != null) for (Item it : items) if (it != null) e.itemIds.add(it.getId());
        return e;
    }

    public RepairEntry amount(float amount) {
        this.numericAmount = amount;
        this.amountExpression = null;
        return this;
    }

    public RepairEntry amountExpr(String expression) {
        this.amountExpression = expression;
        this.numericAmount = null;
        return this;
    }

    public CompoundTag toNbt() {
        if (itemIds.isEmpty()) {
            return new CompoundTag().putList("items", new ListTag<>());
        }

        ListTag<CompoundTag> itemsList = new ListTag<>();
        for (String id : itemIds) {
            itemsList.add(new CompoundTag().putString("name", id));
        }

        CompoundTag entry = new CompoundTag().putList("items", itemsList);

        if (amountExpression != null && !amountExpression.isBlank()) {
            entry.putCompound("repair_amount", new CompoundTag()
                    .putString("expression", amountExpression)
                    .putShort("version", EXPR_VERSION));
        } else if (numericAmount != null) {
            entry.putFloat("repair_amount", numericAmount);
        }
        return entry;
    }
}

