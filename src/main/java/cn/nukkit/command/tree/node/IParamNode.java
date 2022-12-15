package cn.nukkit.command.tree.node;

import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamNodeType;
import cn.nukkit.command.tree.ParamTree;

public interface IParamNode<T> {
    void fill(String arg) throws CommandSyntaxException;

    <E> E get();

    void reset();

    boolean hasResult();

    boolean isOptional();

    ParamNodeType type();

    /**
     * 插件不需要调用，这个方法用于初始化{@link ParamTree}和一些能从{@link cn.nukkit.command.data.CommandParameter CommandParameter}得到的参数,例如optional enumData等
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
    default IParamNode<T> init(ParamTree parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
}
