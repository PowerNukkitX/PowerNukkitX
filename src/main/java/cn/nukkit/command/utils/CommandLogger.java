package cn.nukkit.command.utils;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.blockentity.ICommandBlock;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.permission.Permissible;
import cn.nukkit.utils.TextFormat;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;
import java.util.StringJoiner;

@PowerNukkitXOnly
@Since("1.19.40-r3")
@Log4j2
public record CommandLogger(Command command, CommandSender sender, String[] args, boolean isPlayer,
                            boolean sendCommandFeedback) {
    public CommandLogger(Command command, CommandSender sender, String[] args) {
        this(command, sender, args,
                sender.isPlayer() || sender instanceof BlockEntityCommandBlock || sender instanceof ExecutorCommandSender,
                sender.getPosition().getLevel().getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK));
    }

    /**
     * 如果发送者是玩家则按playerK playerV格式化后输出<br>
     * 如果是控制台则输出默认Tips {@code commands.generic.usage}
     */
    public void outputError(String playerK, String[] playerV) {
        this.outputError(playerK, playerV, "commands.generic.usage", new String[]{"\n" + command.getCommandFormatTips()});
    }

    /**
     * 如果是玩家则输出默认SyntaxErrors {@code Key=commands.generic.syntax}<br>
     * 如果发送者是控制台则按Key Value格式化后输出
     */
    public void outputError(int errorIndex, String key, String[] value) {
        this.outputError("commands.generic.syntax", this.syntaxErrorsValue(errorIndex), key, value);
    }

    /**
     * 如果是玩家则输出按playerK playerV格式化后输出<br>
     * 如果发送者是控制台则按consoleK consoleV格式化后输出
     */
    public void outputError(@Nullable String playerK, @Nullable String[] playerV, String consoleK, String[] consoleV) {
        if (isPlayer) {
            if (sendCommandFeedback) {
                if (playerK != null && playerV != null) {
                    sender.sendMessage(new CommandOutputContainer(playerK, playerV, false));
                } else sender.sendMessage(new TranslationContainer(consoleK, consoleV));
            }
        } else sender.sendMessage(new TranslationContainer(consoleK, consoleV));
    }

    /**
     * 如果是玩家则输出默认SyntaxErrors {@code Key=commands.generic.syntax}<br>
     * 如果发送者是控制台则按Key Value格式化后输出
     */
    public void outputInfo(String playerK, String[] playerV, String consoleK, String[] consoleV) {
        if (isPlayer) {
            if (sendCommandFeedback) {
                if (playerK != null && playerV != null) {
                    sender.sendMessage(new CommandOutputContainer(playerK, playerV, true));
                } else sender.sendMessage(new TranslationContainer(consoleK, consoleV));
            }
        } else sender.sendMessage(new TranslationContainer(consoleK, consoleV));
    }

    /**
     * 给命令目标的反馈信息
     *
     * @param rawtext  给命令目标的反馈信息
     * @param receiver 命令目标
     * @param params   给命令目标的反馈信息参数
     */
    public void outputObjectWhisper(String rawtext, Player receiver, Object... params) {
        if (sendCommandFeedback) {
            receiver.sendRawTextMessage(RawText.fromRawText(String.format(rawtext, params)));
        }
    }

    /**
     * 输出一条命令的成功信息
     *
     * @param successCount 成功执行的数量
     * @param playerK      当发送者是玩家执行的key
     * @param playerV      当发送者是玩家执行的value
     * @param consoleK     当发送者是控制台执行的key
     * @param consoleV     当发送者是控制台执行的value
     * @param rawtext      给命令目标的反馈信息
     * @param params       给命令目标的反馈信息参数
     */
    public void outputResult(int successCount, @Nullable String playerK, @Nullable String[] playerV, String consoleK, String[] consoleV, String rawtext, Object... params) {
        if ((sender instanceof ICommandBlock && !sender.getPosition().getLevel().getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT)) ||
                (sender instanceof ExecutorCommandSender exeSender && exeSender.getExecutor() instanceof ICommandBlock && !sender.getPosition().getLevel().getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT))) {
            return;
        }
        var consoleMessage = new TranslationContainer(consoleK, consoleV);
        String resultStr = "[" + sender.getName() + ": " + (!consoleMessage.getText().equals(sender.getServer().getLanguage().get(consoleMessage.getText())) ? "%" : "") + consoleMessage.getText() + "]";
        consoleMessage.setText(resultStr);
        if (isPlayer && playerK != null && playerV != null) {
            var playerMessage = new CommandOutputContainer(playerK, playerV, true, successCount);
            if (sendCommandFeedback)
                sender.sendMessage(playerMessage);
            broadcastAdminChannel(consoleMessage, rawtext, params);
        } else {
            sender.sendMessage(consoleMessage);
            broadcastAdminChannel(consoleMessage, rawtext, params);
        }
    }

    /**
     * 输出默认的命令格式错误信息
     *
     * @param errorIndex 发生错误的参数索引
     */
    public void outputSyntaxErrors(int errorIndex) {
        if (isPlayer) {
            if (sendCommandFeedback) {
                sender.sendMessage(new CommandOutputContainer("commands.generic.syntax", this.syntaxErrorsValue(errorIndex), false));
            }
        } else
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + command.getCommandFormatTips()));
    }

    /**
     * 输出默认的目标选择器错误信息
     */
    public void outputNoTargetMatch() {
        if (isPlayer) {
            if (sendCommandFeedback) {
                sender.sendMessage(new CommandOutputContainer("commands.generic.noTargetMatch", new String[]{}, false));
            }
        } else sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
    }

    /**
     * 输出默认的参数过小错误信息
     *
     * @param errorIndex 发生错误的参数索引
     * @param minimum    允许的最小值
     */
    public void outputNumTooSmall(int errorIndex, int minimum) {
        this.outputError("commands.generic.num.tooSmall", new String[]{args[errorIndex], " " + minimum}, "commands.generic.usage", new String[]{"\n" + command.getCommandFormatTips()});
    }

    private String[] syntaxErrorsValue(int errorIndex) {
        var join1 = new StringJoiner(" ", "/", " ");
        join1.add(command.getName());

        if (errorIndex == -1) {
            return new String[]{join1.toString(), " ", " "};
        } else if (errorIndex == args.length) {
            Arrays.stream(args).forEach(join1::add);
            return new String[]{join1.toString(), " ", " "};
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

    private void broadcastAdminChannel(TextContainer result, String rawtext, Object[] params) {
        Set<Permissible> users = sender.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        for (Permissible user : users) {
            if (user instanceof CommandSender commandSender) {
                if (!commandSender.equals(sender)) {
                    if (commandSender.isPlayer()) {
                        outputObjectWhisper(rawtext, (Player) commandSender, params);
                    } else if (user instanceof ConsoleCommandSender) {
                        ((ConsoleCommandSender) user).sendMessage(result);
                    }
                }
            }
        }
    }
}
