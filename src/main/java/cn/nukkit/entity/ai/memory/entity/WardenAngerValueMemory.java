package cn.nukkit.entity.ai.memory.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.memory.IMemory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.21-r4")
public class WardenAngerValueMemory implements IMemory<Map<Entity, Integer>> {

    protected Map<Entity, Integer> angerValue;

    public WardenAngerValueMemory() {
        this(new HashMap<>());
    }

    public WardenAngerValueMemory(Map<Entity, Integer> angerValue) {
        this.angerValue = angerValue;
    }

    @Nullable
    @Override
    public Map<Entity, Integer> getData() {
        return angerValue;
    }

    @Override
    public void setData(@Nullable Map<Entity, Integer> data) {
        this.angerValue = data;
    }

    @Override
    public boolean hasData() {
        return angerValue.isEmpty();
    }
}
