package cn.nukkit.item.utils;

import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.ArrayList;
import java.util.List;


public final class RepairEntry {
    private static final short EXPR_VERSION = 13;

    private final List<String> itemIds = new ArrayList<>();
    private Float numericAmount;
    private String amountExpression;

    private RepairEntry() {
    }

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

    public NbtMap toNbt() {
        if (itemIds.isEmpty()) {
            return NbtMap.builder().putList("items", NbtType.COMPOUND, new ObjectArrayList<>()).build();
        }

        final List<NbtMap> itemsList = new ObjectArrayList<>();
        for (String id : itemIds) {
            itemsList.add(NbtMap.builder().putString("name", id).build());
        }

        NbtMap entry = NbtMap.builder().putList("items", NbtType.COMPOUND, itemsList).build();

        if (amountExpression != null && !amountExpression.isBlank()) {
            entry = entry.toBuilder().putCompound("repair_amount", NbtMap.builder()
                    .putString("expression", amountExpression)
                    .putShort("version", EXPR_VERSION)
                    .build()
            ).build();
        } else if (numericAmount != null) {
            entry = entry.toBuilder().putFloat("repair_amount", numericAmount).build();
        }
        return entry;
    }
}

