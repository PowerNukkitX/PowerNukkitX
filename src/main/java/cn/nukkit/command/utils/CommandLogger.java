package cn.nukkit.command.utils;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.ICommandBlock;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import cn.nukkit.permission.Permissible;
import cn.nukkit.utils.TextFormat;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@PowerNukkitXOnly
@Since("1.19.50-r4")
@Log4j2
public record CommandLogger(Command command, CommandSender sender, String[] args,
                            CommandOutputContainer outputContainer) {
    public CommandLogger(Command command, CommandSender sender, String[] args) {
        this(command, sender, args, new CommandOutputContainer());
    }

    /**
     * 输出一条命令成功执行的消息给命令发送者<br>参数message可以是纯文本，也可以是语言文本key<br>如果是语言文本key将会翻译成对应message
     *
     * @param message the message
     */
    public void outputSuccess(String message) {
        this.outputSuccess(false, false, message);
    }

    public void outputSuccess(String key, String... params) {
        this.outputSuccess(false, false, key, params);
    }

    public void outputSuccess(String key, List<String> params) {
        this.outputSuccess(false, false, key, params.toArray(new String[]{}));
    }

    /**
     * 输出一条命令成功执行的消息给命令发送者<br>参数message可以是纯文本，也可以是语言文本key<br>如果是语言文本key将会翻译成对应message
     *
     * @param broadcastAdminChannel 是否广播消息给其他在管理员频道的玩家
     * @param broadcastConsole      the broadcast console
     * @param key                   the key
     * @param params                the params
     */
    public void outputSuccess(boolean broadcastAdminChannel, boolean broadcastConsole, String key, String... params) {
        if (isNotSend()) return;
        var container = new CommandOutputContainer(key, params, 0);
        this.sender.sendCommandOutput(container);
        if (broadcastAdminChannel) {
            broadcastAdminChannel(broadcastConsole, key, params);
        }
    }


    public void outputError(String message) {
        this.outputError(message, new String[]{});
    }

    public void outputError(String key, String... params) {
        this.outputError(false, false, key, params);
    }

    /**
     * 输出一条错误信息
     *
     * @param broadcastAdminChannel the broadcast admin channel
     * @param broadcastConsole      the broadcast console
     * @param key                   语言文本key/错误信息
     * @param params                语言文本参数/空
     */
    public void outputError(boolean broadcastAdminChannel, boolean broadcastConsole, String key, String... params) {
        if (isNotSend()) return;
        var container = new CommandOutputContainer(key, params, 0);
        sender.sendCommandOutput(container);
        if (broadcastAdminChannel) {
            broadcastAdminChannel(broadcastConsole, key, params);
        }
    }

    /**
     * 输出{@link #outputContainer}中的所有信息.
     */
    public void output() {
        this.output(false, false);
    }

    public void output(boolean broadcastAdminChannel) {
        this.output(broadcastAdminChannel, false);
    }

    /**
     * 输出{@link #outputContainer}中的所有信息.
     *
     * @param broadcastAdminChannel 是否广播消息给其他在管理员频道的玩家
     */
    public void output(boolean broadcastAdminChannel, boolean broadcastConsole) {
        if (isNotSend()) return;
        this.sender.sendCommandOutput(this.outputContainer);
        if (broadcastAdminChannel) {
            for (var msg : this.outputContainer.getMessages()) {
                broadcastAdminChannel(broadcastConsole, msg.getMessageId(), msg.getParameters());
            }
        }
        this.outputContainer.getMessages().clear();
    }

    /**
     * 添加一条信息到{@link #outputContainer}中.
     *
     * @param key the key
     * @return the command logger
     */
    public CommandLogger addMessage(String key) {
        this.outputContainer.getMessages().add(new CommandOutputMessage(key));
        return this;
    }

    /**
     * 添加一条信息到{@link #outputContainer}中.
     *
     * @param key    the key
     * @param params the params
     * @return the command logger
     */
    public CommandLogger addMessage(String key, String... params) {
        this.outputContainer.getMessages().add(new CommandOutputMessage(key, params));
        return this;
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
     * 给命令目标的反馈信息
     *
     * @param receiver 命令目标
     * @param key      the key
     * @param params   给命令目标的反馈信息参数
     */
    public void outputObjectWhisper(Player receiver, String key, String... params) {
        if (isNotSend()) return;
        receiver.sendMessage(new TranslationContainer(key, params));
    }

    /**
     * 给命令目标的反馈信息
     *
     * @param rawtext  给命令目标的反馈信息
     * @param receiver 命令目标
     * @param params   给命令目标的反馈信息参数
     */
    public void outputObjectWhisper(Player receiver, String rawtext, Object... params) {
        if (isNotSend()) return;
        receiver.sendRawTextMessage(RawText.fromRawText(String.format(rawtext, params)));
    }

    /**
     * 输出默认的命令格式错误信息,会提示命令发送者在指定索引处发生错误
     *
     * @param errorIndex 发生错误的参数索引
     */
    public void outputSyntaxErrors(int errorIndex) {
        if (isNotSend()) return;
        if (sender instanceof Player player) {
            player.sendCommandOutput(new CommandOutputContainer("commands.generic.syntax", this.syntaxErrorsValue(errorIndex), 0));
        } else if (sender instanceof ExecutorCommandSender executorCommandSender) {
            executorCommandSender.sendCommandOutput(new CommandOutputContainer("commands.generic.syntax", this.syntaxErrorsValue(errorIndex), 0));
        } else
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + command.getCommandFormatTips()));
    }

    /**
     * 输出一个目标选择器没有匹配目标的错误信息
     */
    public void outputNoTargetMatch() {
        this.outputError("commands.generic.noTargetMatch", new String[]{});
    }

    /**
     * 输出一个目标选择器匹配目标过多的错误信息
     */
    public void outputTooManyTargets() {
        this.outputError("commands.generic.tooManyTargets", new String[]{});
    }

    /**
     * 输出一个参数过小的错误信息，会提示命令发送者指定位置的参数最小值不能低于minimum
     *
     * @param errorIndex 发生错误的参数索引
     * @param minimum    允许的最小值
     */
    public void outputNumTooSmall(int errorIndex, int minimum) {
        this.outputError("commands.generic.num.tooSmall", args[errorIndex], " " + minimum);
    }

    private String[] syntaxErrorsValue(int errorIndex) {
        var join1 = new StringJoiner(" ", "/", " ");
        join1.add(command.getName());

        if (errorIndex == -1) {
            return new String[]{join1.toString(), " ", " "};
        } else if (errorIndex == args.length) {
            Arrays.stream(args).forEach(join1::add);
            return new String[]{join1.toString(), "", ""};
        }

        for (int i = 0; i < errorIndex; ++i) {
            join1.add(args[i]);
        }
        var join2 = new StringJoiner(" ", " ", "");
        for (int i = errorIndex + 1, len = args.length; i < len; ++i) {
            join2.add(args[i]);
        }
        return new String[]{join1.toString(), args[errorIndex], join2.toString()};
    }

    private void broadcastAdminChannel(boolean broadcastConsole, String key, String[] value) {
        CommandSender target = sender;
        if (target instanceof ExecutorCommandSender executorCommandSender) target = executorCommandSender.getExecutor();

        if (target instanceof ICommandBlock) return;

        Set<Permissible> users = target.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        users.remove(target);
        var message = new TranslationContainer(TextFormat.clean(key), value);
        String resultStr = "[" + target.getName() + ": " + (!message.getText().equals(target.getServer().getLanguage().get(message.getText())) ? "%" : "") + message.getText() + "]";
        String coloredStr = TextFormat.GRAY + "" + TextFormat.ITALIC + resultStr;
        message.setText(coloredStr);

        for (Permissible user : users) {
            if (user instanceof CommandSender commandSender) {
                if (broadcastConsole) {
                    commandSender.sendMessage(message);
                } else if (!(commandSender instanceof ConsoleCommandSender)) {
                    commandSender.sendMessage(message);
                }
            }
        }
    }

    //only player
    private boolean isNotSend() {
        return sender instanceof Player && !sender.getPosition().getLevel().getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK);
    }
}
