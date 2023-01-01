package cn.nukkit.entity.component;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class SimpleEntityComponentGroup implements EntityComponentGroup{

    protected Map<Class<? extends EntityComponent>, EntityComponent> components = new ConcurrentHashMap<>();

    public SimpleEntityComponentGroup(EntityComponent... components) {
        this(new HashSet<>(List.of(components)));
    }

    public SimpleEntityComponentGroup(Set<EntityComponent> components) {
        for (EntityComponent component : components) {
            this.components.put(component.getClass(), component);
        }
    }

    @Override
    public Map<Class<? extends EntityComponent>, EntityComponent> getComponents() {
        return components;
    }
}
