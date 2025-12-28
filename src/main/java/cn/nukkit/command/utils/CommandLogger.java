/**
 * Provides a comprehensive logging and feedback utility for command execution in PowerNukkitX.
 * <p>
 * This record encapsulates all context and output for a command execution, including the command, sender, label, arguments,
 * output container, and plugin context. It offers a fluent API for adding success, error, and informational messages, handling
 * localization, formatting, and broadcasting to administrative channels. The logger supports outputting results to the sender,
 * broadcasting to admins, and whispering feedback to specific players. It also provides specialized error reporting for syntax,
 * parameter bounds, and selector issues, and integrates with plugin and server i18n systems.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Fluent API for adding success, error, and informational messages with localization support.</li>
 *   <li>Handles command output formatting, colorization, and message broadcasting.</li>
 *   <li>Supports output to sender, admin broadcast, and player whispering.</li>
 *   <li>Provides detailed error reporting for syntax, parameter bounds, and selector issues.</li>
 *   <li>Integrates with plugin and server i18n for multilingual feedback.</li>
 *   <li>Tracks and manages command output state and success count.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Instantiate with command context and use fluent methods to add messages and output results.</li>
 *   <li>Supports both plugin and internal command execution scenarios.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * CommandLogger logger = new CommandLogger(cmd, sender, label, args);
 * logger.addSuccess("commands.success").output();
 * logger.addError("commands.error").output();
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see Command
 * @see CommandSender
 * @see CommandOutputContainer
 * @see Plugin
 * @since PowerNukkitX 1.19.50
 */
package cn.nukkit.command.utils;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.ICommandBlock;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import cn.nukkit.permission.Permissible;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;


public record CommandLogger(Command command,
                            CommandSender sender,
                            String commandLabel,
                            String[] args,
                            CommandOutputContainer outputContainer,
                            Plugin plugin) {
    private static final byte SYNTAX_ERROR_LENGTH_LIMIT = 23;

    public CommandLogger(Command command, CommandSender sender, String commandLabel, String[] args) {
        this(command, sender, commandLabel, args, new CommandOutputContainer());
    }

    public CommandLogger(Command command, CommandSender sender, String commandLabel, String[] args, CommandOutputContainer outputContainer) {
        this(command, sender, commandLabel, args, outputContainer, InternalPlugin.INSTANCE);
    }

    public CommandLogger(Command command, CommandSender sender, String commandLabel, String[] args, Plugin plugin) {
        this(command, sender, commandLabel, args, new CommandOutputContainer(), plugin);
    }

    public CommandLogger addSuccess(String message) {
        return this.addSuccess(message, CommandOutputContainer.EMPTY_STRING);
    }

    public CommandLogger addSuccess(String key, List<String> params) {
        return this.addSuccess(key, params.toArray(CommandOutputContainer.EMPTY_STRING));
    }

    /**
     * 添加一条命令成功执行的消息，参数可以是纯文本，也可以是客戶端的多语言文本key.<br>默认输出颜色白色
     * <p>
     * Add a message that the command was successfully executed, the parameters can be plain text or the client's multilingual text key.<br>Default output color white
     *
     * @param key    the key
     * @param params the params
     */
    public CommandLogger addSuccess(String key, String... params) {
        if (TextFormat.getLastColors(key).isEmpty()) {
            if (Server.getInstance().getLanguage().internalGet(key) != null) key = TextFormat.WHITE + "%" + key;
            else key = TextFormat.WHITE + key;
        }

        this.outputContainer.getMessages().add(new CommandOutputMessage(key, params));
        this.outputContainer.incrementSuccessCount();
        return this;
    }

    /**
     * 添加一条命令错误执行的消息，参数可以是纯文本，也可以是客戶端的多语言文本key.<br>默认输出颜色红色
     * <p>
     * Add a command error message, either plain text or the client's multilingual text key.<br>Default output color red
     *
     * @param message the message
     * @return the command logger
     */
    public CommandLogger addError(String message) {
        return this.addError(message, CommandOutputContainer.EMPTY_STRING);
    }

    /**
     * 添加一条命令执行失败的错误消息，参数可以是纯文本，也可以是客戶端的多语言文本key.<br>默认输出颜色红色
     * <p>
     * Add a command execution failure error message, either plain text or the client's multilingual text key.<br>Default output color red
     *
     * @param key    语言文本key/错误信息
     * @param params 语言文本参数/空
     * @return the command logger
     */
    public CommandLogger addError(String key, String... params) {
        this.outputContainer.getMessages().add(new CommandOutputMessage(key, params));
        return this;
    }

    /**
     * 添加一条消息，参数可以是纯文本，也可以是客户端，服务端，以及{@link cn.nukkit.lang.PluginI18n PluginI18n}中的多语言文本，默认输出颜色红色
     * <p>
     * Add a message, the parameters can be plain text, or client-side, server-side, and multilingual text in {@link cn.nukkit.lang.PluginI18n PluginI18n}, default output color red
     *
     * @param key the key
     * @return the command logger
     */
    public CommandLogger addMessage(String key) {
        return this.addMessage(key, CommandOutputContainer.EMPTY_STRING);
    }

    /**
     * 添加一条消息，参数可以是纯文本，也可以是客户端，服务端，以及{@link cn.nukkit.lang.PluginI18n PluginI18n}中的多语言文本，默认输出颜色红色
     * <p>
     * Add a message, the parameters can be plain text, or client-side, server-side, and multilingual text in {@link cn.nukkit.lang.PluginI18n PluginI18n}, default output color red
     *
     * @param key    the key
     * @param params the params
     * @return the command logger
     */
    public CommandLogger addMessage(String key, String... params) {
        if (this.plugin instanceof PluginBase pluginBase) {
            var i18n = PluginI18nManager.getI18n(pluginBase);
            if (i18n != null) {
                String text;
                if (sender.isPlayer()) {
                    text = i18n.tr(sender.asPlayer().getLanguageCode(), key, params);
                } else {
                    text = i18n.tr(Server.getInstance().getLanguageCode(), key, params);
                }
                this.outputContainer.getMessages().add(new CommandOutputMessage(text, CommandOutputContainer.EMPTY_STRING));
                return this;
            }
        }
        this.outputContainer.getMessages().add(new CommandOutputMessage(Server.getInstance().getLanguage().tr(key, params), CommandOutputContainer.EMPTY_STRING));
        return this;
    }

    /**
     * 添加一条默认的命令格式错误信息,会提示命令发送者在指定索引处发生错误
     * <p>
     * Add a default command format error message that will alert the command sender of an error at the specified index
     *
     * @param errorIndex 发生错误的参数索引
     */
    public CommandLogger addSyntaxErrors(int errorIndex) {
        if (sender instanceof ConsoleCommandSender) {
            this.addMessage("commands.generic.usage", "\n" + command.getCommandFormatTips());
        } else this.addError("commands.generic.syntax", this.syntaxErrorsValue(errorIndex));
        return this;
    }

    /**
     * 添加一条目标选择器没有匹配目标的错误信息
     * <p>
     * Add an error message that the target selector matches too many targets
     */
    public CommandLogger addNoTargetMatch() {
        this.addError("commands.generic.noTargetMatch", CommandOutputContainer.EMPTY_STRING);
        return this;
    }

    /**
     * 添加一条目标选择器匹配目标过多的错误信息
     */
    public CommandLogger addTooManyTargets() {
        this.addError("commands.generic.tooManyTargets", CommandOutputContainer.EMPTY_STRING);
        return this;
    }

    /**
     * 添加一条参数过小的错误信息，会提示命令发送者指定位置的参数最小值不能低于minimum
     * <p>
     * Add an error message that the parameter is too small, prompting the command sender to specify a location where the minimum value of the parameter cannot be less than minimum
     *
     * @param errorIndex 发生错误的参数索引
     * @param minimum    允许的最小值
     */
    public CommandLogger addNumTooSmall(int errorIndex, int minimum) {
        this.addError("commands.generic.num.tooSmall", args[errorIndex], " " + minimum);
        return this;
    }

    /**
     * 添加一条Double参数过大的错误信息，会提示命令发送者指定位置的参数最大值不能超过maximum
     * <p>
     * Add a Double parameter too large error message, which will prompt the command sender to specify that the maximum value of the parameter at the location cannot exceed maximum
     *
     * @param errorIndex 发生错误的参数索引
     * @param maximum    允许的最大值
     */
    public CommandLogger addDoubleTooBig(int errorIndex, double maximum) {
        this.addError("commands.generic.double.tooBig", args[errorIndex], " " + maximum);
        return this;
    }

    /**
     * 添加一条Double参数过小的错误信息，会提示命令发送者指定位置的参数最小值不能低于minimum
     * <p>
     * Add a Double parameter is too small error message, which will prompt the command sender to specify the minimum value of the parameter at the location cannot be less than minimum
     *
     * @param errorIndex 发生错误的参数索引
     * @param minimum    允许的最小值
     */
    public CommandLogger addDoubleTooSmall(int errorIndex, double minimum) {
        this.addError("commands.generic.double.tooSmall", args[errorIndex], " " + minimum);
        return this;
    }

    /**
     * 添加一条无法访问世界外的方块的错误信息
     * <p>
     * Add an error message about not being able to access squares outside the world
     *
     * @return the command logger
     */
    public CommandLogger addOutOfWorld() {
        this.addError("commands.generic.outOfWorld", CommandOutputContainer.EMPTY_STRING);
        return this;
    }

    /**
     * 输出{@link #outputContainer}中的所有信息.
     */
    public void output() {
        this.output(false);
    }

    /**
     * 输出{@link #outputContainer}中的所有信息.
     *
     * @param broadcast the broadcast
     */
    public void output(boolean broadcast) {
        this.sender.sendCommandOutput(this.outputContainer);
        if (broadcast) {
            for (var msg : this.outputContainer.getMessages()) {
                broadcastAdminChannel(msg.getMessageId(), msg.getParameters());
            }
        }
        this.outputContainer.setSuccessCount(0);
        this.outputContainer.getMessages().clear();
    }

    /**
     * 标记{@link #outputContainer}的成功数量
     *
     * @param successCount the success count
     * @return the command logger
     */
    public CommandLogger successCount(int successCount) {
        this.outputContainer.setSuccessCount(successCount);
        return this;
    }

    /**
     * 输出给指定目标一条反馈信息
     * <p>
     * Output a feedback message to the specified receiver
     *
     * @param receiver 命令目标
     * @param key      the key
     * @param params   给命令目标的反馈信息参数
     */
    public void outputObjectWhisper(Player receiver, String key, String... params) {
        if (receiver.level.getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK)) {
            receiver.sendMessage(new TranslationContainer(key, params));
        }
    }

    /**
     * 输出给指定目标一条反馈信息
     * <p>
     * Output a feedback message to the specified receiver
     *
     * @param rawtext  给命令目标的反馈信息
     * @param receiver 命令目标
     * @param params   给命令目标的反馈信息参数
     */
    public void outputObjectWhisper(Player receiver, String rawtext, Object... params) {
        if (receiver.level.getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK)) {
            receiver.sendRawTextMessage(RawText.fromRawText(String.format(rawtext, params)));
        }
    }

    private String[] syntaxErrorsValue(int errorIndex) {
        var join1 = new StringJoiner(" ", "", " ");
        join1.add(commandLabel);

        if (errorIndex == -1) {
            var result = join1.toString();
            return new String[]{result.substring(Math.max(0, result.length() - SYNTAX_ERROR_LENGTH_LIMIT)), " ", " "};
        } else if (errorIndex == args.length) {
            Arrays.stream(args).forEach(join1::add);
            var result = join1.toString();
            return new String[]{result.substring(Math.max(0, result.length() - SYNTAX_ERROR_LENGTH_LIMIT)), "", ""};
        }

        for (int i = 0; i < errorIndex; ++i) {
            join1.add(args[i]);
        }
        var join2 = new StringJoiner(" ", " ", "");
        for (int i = errorIndex + 1, len = args.length; i < len; ++i) {
            join2.add(args[i]);
        }

        var end = args[errorIndex] + join2;
        if (end.length() >= SYNTAX_ERROR_LENGTH_LIMIT) {
            return new String[]{"", args[errorIndex], join2.toString()};
        } else {
            var result = join1.toString();
            return new String[]{result.substring(Math.max(0, join1.length() + end.length() - SYNTAX_ERROR_LENGTH_LIMIT)), args[errorIndex], join2.toString()};
        }
    }

    private void broadcastAdminChannel(String key, String[] value) {
        CommandSender target = sender;
        if (target instanceof ExecutorCommandSender executorCommandSender) target = executorCommandSender.getExecutor();
        if (target instanceof ICommandBlock) return;
        TranslationContainer message = broadcastMessage(key, value, target);

        Set<Permissible> users = target.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        users.remove(target);
        for (Permissible user : users) {
            if (user instanceof CommandSender commandSender) {
                commandSender.sendMessage(message);
            }
        }
    }

    private TranslationContainer broadcastMessage(String key, String[] value, CommandSender target) {
        var message = new TranslationContainer(TextFormat.clean(key), value);
        String resultStr = "[" + target.getName() + ": " + (!message.getText().equals(target.getServer().getLanguage().get(message.getText())) ? "%" : "") + message.getText() + "]";
        String coloredStr = TextFormat.GRAY + "" + TextFormat.ITALIC + resultStr;
        message.setText(coloredStr);
        return message;
    }
}
