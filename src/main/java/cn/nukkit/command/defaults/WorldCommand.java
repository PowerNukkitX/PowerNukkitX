package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;

@PowerNukkitXOnly
@Since("1.19.50-r3")
public class WorldCommand extends VanillaCommand {

    public WorldCommand(String name) {
        super(name, "nukkit.command.world.description");
        this.setPermission("nukkit.command.world");
        this.commandParameters.clear();
        this.commandParameters.put("tp",
                new CommandParameter[]{
                   CommandParameter.newEnum("tp", new String[]{"tp"}),
                   CommandParameter.newType("world", CommandParamType.STRING)
                });
        this.commandParameters.put("list",
                new CommandParameter[]{
                   CommandParameter.newEnum("list", new String[]{"list"})
                });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender) || !sender.isPlayer()) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);

        String form = parser.matchCommandForm();
        if (form == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        try {
            switch (form) {
                case "list" -> {
                    var strBuilder = new StringBuilder();
                    Server.getInstance().getLevels().values().forEach(level -> {
                        strBuilder.append(level.getName());
                        strBuilder.append(", ");
                    });
                    sender.sendMessage(new TranslationContainer("nukkit.command.world.availableLevels",strBuilder.toString()));
                    return true;
                }
                case "tp" -> {
                    parser.parseString();//skip "tp"
                    var levelName = parser.parseString();
                    var level = Server.getInstance().getLevelByName(levelName);
                    if (level == null) {
                        if (Server.getInstance().loadLevel(levelName)) {
                            level = Server.getInstance().getLevelByName(levelName);
                        } else {
                            sender.sendMessage(new TranslationContainer("nukkit.command.world.levelNotFound", levelName));
                            return false;
                        }
                    }
                    sender.asPlayer().teleport(level.getSafeSpawn());
                    sender.sendMessage(new TranslationContainer("nukkit.command.world.successTp", levelName));
                    return true;
                }
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }
}
