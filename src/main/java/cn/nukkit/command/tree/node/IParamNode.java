package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public interface IParamNode<T> {
    /**
     * 负责填充该参数节点，覆写该方法需要实现对接受参数arg的验证以及解析成为对应类型T的结果
     * <br>
     * 当验证失败或者解析失败，请抛出异常{@link CommandSyntaxException}
     * <br>
     * 如果需要输出错误信息，请使用
     *
     * @param arg the arg
     * @throws CommandSyntaxException the command syntax exception
     */
    void fill(String arg) throws CommandSyntaxException;

    <E> E get();

    void reset();

    boolean hasResult();

    boolean isOptional();

    /**
     * 这个方法用于初始化{@link ParamTree}和一些能从{@link cn.nukkit.command.data.CommandParameter CommandParameter}得到的参数,例如optional enumData等，插件不需要调用
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
