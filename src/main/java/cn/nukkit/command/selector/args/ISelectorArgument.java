package cn.nukkit.command.selector.args;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 此接口描述了一个选择器参数<p/>
 */


public interface ISelectorArgument extends Comparable<ISelectorArgument>{
    /**
     * 根据给定的参数表返回特定的{@code List<Predicate<Entity>>}
     * @param arguments 参数列表
     * @param selectorType 选择器类型
     * @param sender 命令发送者
     * @param basePos 若此参数解析需要用到相对坐标，则应该以此坐标为依据<p/>
     *                若此参数需要修改参照坐标(例如x,y,z参数)，则应当在这个参数上修改<p/>
     *                在一条解析链上的参数只会使用一个Location对象
     * @return {@code Predicate<Entity>}
     * @throws SelectorSyntaxException 当解析出错
     */
    @Nullable
    default Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        return null;
    }

    /**
     * 仅当方法isFilter()返回true时调用此方法<p/>
     * 方法返回的实体集合将传递到下一个参数
     * @param arguments 参数列表
     * @param selectorType 选择器类型
     * @param sender 命令发送者
     * @param basePos 若此参数解析需要用到相对坐标，则应该以此坐标为依据<p/>
     *                若此参数需要修改参照坐标(例如x,y,z参数)，则应当在这个参数上修改<p/>
     *                在一条解析链上的参数只会使用一个Location对象
     * @return 实体过滤器
     * @throws SelectorSyntaxException 当解析出错
     */
    default Function<List<Entity>, List<Entity>> getFilter(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        return null;
    }

    /**
     * 获取此参数的名称
     * @return 参数名称
     */
    String getKeyName();

    /**
     * 解析优先级定义了各个参数的解析顺序<p/>
     * 优先级越高(数字越小)的参数，其越先被解析，且其解析结果将会影响下个参数的解析
     * @return 此参数的解析优先级
     */
    int getPriority();

    /**
     * 是否启用过滤器模式<p/>
     * 若一个参数为过滤器模式，解析器将不会调用getPredicate()方法而是调用filter()方法<p/>
     * 这意味着参数将对到达此参数节点的所有实体进行检测，而不是相对于单个实体进行检测<p/>
     * 对于部分参数（例如"c"），需要启用此功能
     * @return 是否启用过滤器模式
     */
    default boolean isFilter() {
        return false;
    }

    /**
     * 若一个参数有默认值（即此方法返回非null值），则在解析时若给定参数表中没有此参数，会以此默认值参与解析
     *
     * @param values 参数列表
     * @param selectorType 选择器类型
     * @param sender 命令执行者
     * @return 此参数的默认值
     */
    @Nullable
    default String getDefaultValue(Map<String, List<String>> values, SelectorType selectorType, CommandSender sender) {
        return null;
    }

    @Override
    default int compareTo(@NotNull ISelectorArgument o) {
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}
