package cn.nukkit.command;

/**
 * An interface what can be implemented by classes which listens to command executing.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.plugin.PluginBase
 * @see cn.nukkit.command.CommandExecutor#onCommand
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface CommandExecutor {
    /**
     * Called when a command is executed.</p>
     *
     * A command can be such a form like {@code /a_LABEL an_arg1 AN_ARG2...}. At this time, the value of
     * variable {@code label} is {@code "a_label"}, and the values of elements of array {@code args} are
     * {@code "an_arg1","AN_ARG2",...}. Notice that the value of variable {@code label} will be converted to
     * lower case, but the cases of elements of array {@code args} won't change.</p>
     *
     * If this function returns {@code false}, Nukkit will send command usages to command sender, to explain that
     * the command didn't work normally. If your command works properly, a {@code true} should be returned to explain
     * that the command works.</p>
     *
     * If you want to test whether a command sender has the permission to execute a command,
     * you can use {@link cn.nukkit.command.Command#testPermissionSilent}.</p>
     *
     * @param sender The sender of this command, this can be a player or a console.
     *
     * @param command The command to send.
     *
     * @param label  Label of the command.
     *
     * @param args  Arguments of this command.
     *
     * @return Whether this command is executed successfully.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
