package cn.nukkit.entity.ai.controller;

import cn.nukkit.entity.EntityIntelligent;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.Arrays;
import java.util.function.Predicate;

public class ConditionalController implements IController {

    private Object2ObjectArrayMap<Predicate<EntityIntelligent>, IController> controllers = new Object2ObjectArrayMap<>();

    public ConditionalController(Pair<Predicate<EntityIntelligent>, IController>... controllers) {
        Arrays.stream(controllers).forEach(pair -> this.controllers.put(pair.first(), pair.second()));
    }

    @Override
    public boolean control(EntityIntelligent entity) {
        boolean successful = false;
        for(Object2ObjectMap.Entry<Predicate<EntityIntelligent>, IController> entry : controllers.object2ObjectEntrySet()) {
            if(entry.getKey().test(entity)) {
                if(entry.getValue().control(entity)) {
                    successful = true;
                }
            }
        }
        return successful;
    }
}
