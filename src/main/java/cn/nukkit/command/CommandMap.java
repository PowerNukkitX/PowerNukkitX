package cn.nukkit.command;

import java.util.List;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface CommandMap {

    /**
     * 注册全部命令
     *
     * @param fallbackPrefix 命令标签前缀，当命令label重复时用于区分
     * @param commands       the commands
     */
    void registerAll(String fallbackPrefix, List<? extends Command> commands);

    /**
     * 注册命令
     *
     * @param fallbackPrefix 命令标签前缀，当命令label重复时用于区分
     * @param command        the command
     * @return 当命令label重复时返回false, 此时你无法使用label来获取和执行命令，不过你仍然可以使用fallbackPrefix:label来获取命令
     */
    boolean register(String fallbackPrefix, Command command);

    /**
     * 注册命令
     *
     * @param fallbackPrefix 命令标签前缀，当命令label重复时用于区分
     * @param command        the command
     * @param label          the label
     * @return the boolean
     */
    boolean register(String fallbackPrefix, Command command, String label);

    /**
     * 注册一个基于注解开发的命令
     *
     * @param object the object
     */
    void registerSimpleCommands(Object object);

    /**
     * 执行命令
     *
     * @param sender  the sender
     * @param cmdLine the cmd line
     * @return the int 返回0代表执行失败, 返回大于等于1代表执行成功<br>Returns 0 for failed execution, greater than or equal to 1 for successful execution
     */
    int executeCommand(CommandSender sender, String cmdLine);

    /**
     * 清理全部的插件命令
     */
    void clearCommands();

    /**
     * 从给定命令名称或者别名获取命令对象
     *
     * @param name the name
     * @return the command
     */
    Command getCommand(String name);

    /***
     *
     * @param commands names of commands to unregistering
     */
    void unregister(String... commands);

}
