package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ReloadCommand extends VanillaCommand {

    public ReloadCommand(String name) {
        super(name, "Reload the server/plugin");
        this.setPermission("nukkit.command.reload");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.commandParameters.put("function", new CommandParameter[]{
                CommandParameter.newEnum("function", false, new String[]{"function"})
        });
        this.commandParameters.put("plugin", new CommandParameter[]{
                CommandParameter.newEnum("plugin", new String[]{"plugin"}),
                CommandParameter.newType("plugin", CommandParamType.STRING)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);

        String form = parser.matchCommandForm();
        if (form == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        try {
            switch (form) {
                case "default" -> {
                    Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloading" + TextFormat.WHITE));
                    sender.getServer().reload();
                    Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloaded" + TextFormat.WHITE));
                    return true;
                }
                case "function" -> {
                    Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloading" + TextFormat.WHITE));
                    sender.getServer().getFunctionManager().reload();
                    Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloaded" + TextFormat.WHITE));
                    return true;
                }
                case "plugin" -> {
                    parser.parseString();//skip "plugin"
                    Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloading" + TextFormat.WHITE));
                    var pluginManager = sender.getServer().getPluginManager();
                    var plugin = pluginManager.getPlugin(parser.parseString());
                    //todo: 多语言
                    Command.broadcastCommandMessage(sender, "Reloading plugin §a" + plugin.getDescription().getName());
                    pluginManager.disablePlugin(plugin);
                    pluginManager.getPlugins().remove(plugin.getDescription().getName());
                    plugin = pluginManager.loadPlugin(plugin.getFile());
                    pluginManager.enablePlugin(plugin);
                    Command.broadcastCommandMessage(sender, "Plugin §a" + plugin.getDescription().getName() + " §freloaded!");
                    Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloaded" + TextFormat.WHITE));
                    return true;
                }
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }
}
