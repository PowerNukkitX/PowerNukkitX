package cn.nukkit.entity.ai.route.finder.impl;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.data.Node;
import cn.nukkit.entity.ai.route.finder.IRouteFinder;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ConditionalAStarRouteFinder implements IRouteFinder {

    private final EntityIntelligent entity;
    private Object2ObjectArrayMap<Predicate<EntityIntelligent>, IRouteFinder> routeFinders = new Object2ObjectArrayMap<>();

    public ConditionalAStarRouteFinder(EntityIntelligent entity, Pair<Predicate<EntityIntelligent>, IRouteFinder>... routeFinders) {
        this.entity = entity;
        Arrays.stream(routeFinders).forEach(pair -> this.routeFinders.put(pair.first(), pair.second()));
    }

    public Optional<IRouteFinder> getRouteFinder() {
        for(Object2ObjectMap.Entry<Predicate<EntityIntelligent>, IRouteFinder> entry : routeFinders.object2ObjectEntrySet()) {
            if(entry.getKey().test(this.entity)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isSearching() {
        return getRouteFinder().map(IRouteFinder::isSearching).orElse(false);
    }

    @Override
    public boolean isFinished() {
        return getRouteFinder().map(IRouteFinder::isFinished).orElse(false);
    }

    @Override
    public boolean isInterrupt() {
        return getRouteFinder().map(IRouteFinder::isInterrupt).orElse(true);
    }

    @Override
    public boolean isReachable() {
        return getRouteFinder().map(IRouteFinder::isReachable).orElse(false);
    }

    @Override
    public boolean search() {
        return getRouteFinder().map(IRouteFinder::search).orElse(false);
    }

    @Override
    public Vector3 getStart() {
        return getRouteFinder().map(IRouteFinder::getStart).orElse(Vector3.ZERO);
    }

    @Override
    public void setStart(Vector3 vector3) {
        getRouteFinder().ifPresent(r -> r.setStart(vector3));
    }

    @Override
    public Vector3 getTarget() {
        return getRouteFinder().map(IRouteFinder::getTarget).orElse(Vector3.ZERO);
    }

    @Override
    public void setTarget(Vector3 vector3) {
        getRouteFinder().ifPresent(r -> r.setTarget(vector3));
    }

    @Override
    public Vector3 getReachableTarget() {
        return getRouteFinder().map(IRouteFinder::getReachableTarget).orElse(Vector3.ZERO);
    }

    @Override
    public List<Node> getRoute() {
        return getRouteFinder().map(IRouteFinder::getRoute).orElse(List.of());
    }

    @Override
    public boolean hasNext() {
        return getRouteFinder().map(IRouteFinder::hasNext).orElse(false);
    }

    @Override
    public @Nullable Node next() {
        return getRouteFinder().map(IRouteFinder::next).orElse(null);
    }

    @Override
    public boolean hasCurrentNode() {
        return getRouteFinder().map(IRouteFinder::hasCurrentNode).orElse(false);
    }

    @Override
    public Node getCurrentNode() {
        return getRouteFinder().map(IRouteFinder::getCurrentNode).orElse(null);
    }

    @Override
    public int getNodeIndex() {
        return getRouteFinder().map(IRouteFinder::getNodeIndex).orElse(0);
    }

    @Override
    public void setNodeIndex(int index) {
        getRouteFinder().ifPresent(r -> r.setNodeIndex(index));
    }

    @Override
    public Node getNode(int index) {
        return getRouteFinder().map(r -> r.getNode(index)).orElse(null);
    }
}
