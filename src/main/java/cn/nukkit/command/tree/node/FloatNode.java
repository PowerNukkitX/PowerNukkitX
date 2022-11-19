package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FloatNode extends ParamNode<Double> {
    private static final Predicate<String> FLOAT_PREDICATE = Pattern.compile("^(-?\\d+)(\\.\\d+)?$").asPredicate();

    public FloatNode(boolean optional) {
        super(optional);
    }

    @Override
    public void fill(String arg, Object... extras) throws CommandSyntaxException {
        try {
            if (FLOAT_PREDICATE.test(arg)) {
                this.value = Double.parseDouble(arg);
            } else throw new CommandSyntaxException();
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    @Override
    public boolean hasResult() {
        return false;
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.FLOAT;
    }
}
