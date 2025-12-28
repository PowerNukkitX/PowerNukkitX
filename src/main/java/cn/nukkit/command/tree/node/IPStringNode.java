package cn.nukkit.command.tree.node;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Parses and validates an IP address argument as a {@link String} value for PowerNukkitX command trees.
 * <p>
 * This node is not used by default and must be manually specified. It uses a regex pattern to validate IPv4 addresses
 * and sets the value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Validates the argument as an IPv4 address using a regex pattern.</li>
 *   <li>Sets the parsed IP address as a string or triggers an error if invalid.</li>
 *   <li>Used for IP address parameter parsing when manually specified.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for IP address parameter parsing when manually specified.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "192.168.1.1"
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see String
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 * 验证IP地址并解析为{@link String}值
 * <p>
 * 不会默认使用，需要手动指定
 */
public class IPStringNode extends StringNode {
    private static final Predicate<String> IP_PREDICATE = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$").asPredicate();

    @Override
    public void fill(String arg) {
        if (IP_PREDICATE.test(arg)) this.value = arg;
        else this.error();
    }
}
