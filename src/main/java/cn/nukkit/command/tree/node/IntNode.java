package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IntNode extends ParamNode<Integer> {
    private static final Predicate<String> INT_PREDICATE = Pattern.compile("^(?:[-+]?(?:0|[1-9][0-9]*))$").asPredicate();

    public IntNode(boolean optional) {
        super(optional);
    }

    @Override
    public void fill(String arg, Object... extras) throws CommandSyntaxException {
        try {
            if (INT_PREDICATE.test(arg)) {
                this.value = Integer.parseInt(arg);
            } else throw new CommandSyntaxException();
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException();
        }
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.INT;
    }
}
