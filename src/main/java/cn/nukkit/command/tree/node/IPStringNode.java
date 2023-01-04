package cn.nukkit.command.tree.node;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IPStringNode extends StringNode {
    private static final Predicate<String> IP_PREDICATE = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$").asPredicate();

    @Override
    public void fill(String arg) {
        if (IP_PREDICATE.test(arg)) this.value = arg;
        else this.error();
    }
}
