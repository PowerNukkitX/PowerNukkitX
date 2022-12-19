package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamList;

/**
 * 代表一个抽象的命令节点，类型T对应节点解析结果类型<br>
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public interface IParamNode<T> {

    /**
     * 负责填充该参数节点,覆写该方法需要实现对接受参数arg的验证以及解析成为对应类型T的结果
     * <br>
     * 当验证失败或者解析失败,请抛出异常{@link CommandSyntaxException}
     *
     * @param arg the arg
     * @throws CommandSyntaxException the command syntax exception
     */
    void fill(String arg) throws CommandSyntaxException;

    /**
     * 获取已被{@link #fill(String)}填充后的节点值,会自动转型为接受类型E,不会判断是否能成功转型<br>有可能抛出{@link ClassCastException}
     */
    <E> E get();

    /**
     * 将节点重置会初始化状态,以待下次填充{@link #fill(String)}
     */
    void reset();

    /**
     * 该节点是否已经得到结果<br>
     * 该方法返回值为false时,将会一直重复对该节点执行填充{@link #fill(String)}直到该方法返回true或者命令输入参数用完
     */
    boolean hasResult();

    /**
     * 该命令节点是否为可选值,可选值不一定需要被填充{@link #fill(String)}
     */
    boolean isOptional();

    /**
     * 这个方法用于初始化{@link ParamList}和一些能从{@link cn.nukkit.command.data.CommandParameter CommandParameter}得到的参数,例如optional enumData等，插件不需要调用
     *
     * @param parent   the parent
     * @param name     the name
     * @param optional the optional
     * @param type     the type
     * @param enumData the enum data
     * @param postFix  the post fix
     * @return the param node
     * @throws IllegalArgumentException the illegal argument exception
     */
    default IParamNode<T> init(ParamList parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        return this;
    }
}
