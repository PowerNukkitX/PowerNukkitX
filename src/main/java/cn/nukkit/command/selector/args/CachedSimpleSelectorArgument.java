package cn.nukkit.command.selector.args;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * 可缓存的目标选择器参数基类<p/>
 * 若一个选择器的参数返回的{@code List<Predicate<Entity>>}不具有时效性，则可继承此类实现对解析结果的缓存，提高性能
 */


public abstract class CachedSimpleSelectorArgument implements ISelectorArgument {

    Cache<Set<String>, Predicate<Entity>> cache;

    public CachedSimpleSelectorArgument() {
        this.cache = provideCacheService();
    }

    @Override
    public Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        var value = cache.getIfPresent(Sets.newHashSet(arguments));
        if (value == null) {
            value = cache(selectorType, sender, basePos, arguments);
            cache.put(Sets.newHashSet(arguments), value);
        }
        return value;
    }

    /**
     * 当未在缓存中找到解析结果时，则调用此方法对参数进行解析
     */
    protected abstract Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException;

    /**
     * 初始化缓存时调用此方法<p/>
     * 若需要自己的缓存实现，则可覆写此方法
     * @return {@code Cache<Set<String>, Predicate<Entity>>}
     */
    protected Cache<Set<String>, Predicate<Entity>> provideCacheService() {
        return Caffeine.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();
    }
}