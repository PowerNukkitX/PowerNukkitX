package cn.nukkit.command.selector.args;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 与{@link CachedSimpleSelectorArgument}类似，但是适用于过滤器模式。此处不做过多解释<p/>
 * @see CachedSimpleSelectorArgument
 */


public abstract class CachedFilterSelectorArgument implements ISelectorArgument {

    Cache<Set<String>, Function<List<Entity>, List<Entity>>> cache;

    public CachedFilterSelectorArgument() {
        this.cache = provideCacheService();
    }

    @Override
    public Function<List<Entity>, List<Entity>> getFilter(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        var value = cache.getIfPresent(Sets.newHashSet(arguments));
        if (value == null) {
            value = cache(selectorType, sender, basePos, arguments);
            cache.put(Sets.newHashSet(arguments), value);
        }
        return value;
    }

    @Override
    public boolean isFilter() {
        return true;
    }

    /**
     * 当未在缓存中找到解析结果时，则调用此方法对参数进行解析
     */
    protected abstract Function<List<Entity>, List<Entity>> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException;

    /**
     * 初始化缓存时调用此方法<p/>
     * 若需要自己的缓存实现，则可覆写此方法
     * @return {@code Cache<Set<String>, Function<List<Entity>, List<Entity>>>}
     */
    protected Cache<Set<String>, Function<List<Entity>, List<Entity>>> provideCacheService() {
        return Caffeine.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();
    }
}
