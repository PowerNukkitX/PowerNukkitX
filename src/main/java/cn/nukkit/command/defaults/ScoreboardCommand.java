package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;

public class ScoreboardCommand extends VanillaCommand {

    public ScoreboardCommand(String name) {
        super(name,"commands.scoreboard.description", "commands.scoreboard.usage");
        this.setPermission("nukkit.command.scoreboard");
        this.commandParameters.clear();
        this.commandParameters.put("objectives-add", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("add",new String[]{"add"}),
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newEnum("dummy",new String[]{"dummy"}),
                CommandParameter.newType("displayname", CommandParamType.STRING)
        });
        this.commandParameters.put("objectives-list", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("list",new String[]{"list"}),
        });
        this.commandParameters.put("objectives-remove", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("remove",new String[]{"remove"}),
                CommandParameter.newType("objective", CommandParamType.STRING)
        });
        this.commandParameters.put("objectives-setdisplay-list-sidebar", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("setdisplay",new String[]{"setdisplay"}),
                CommandParameter.newEnum("slot",new String[]{"list","sidebar"}),
                CommandParameter.newType("objective",true, CommandParamType.STRING),
                CommandParameter.newEnum("slot",true,new String[]{"list","sidebar"})
        });
        this.commandParameters.put("objectives-setdisplay-belowname", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("setdisplay",new String[]{"setdisplay"}),
                CommandParameter.newEnum("slot",new String[]{"belowname"}),
                CommandParameter.newType("objective",true, CommandParamType.STRING)
        });
        this.commandParameters.put("players-add-remove-set", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("ars",new String[]{"add","remove","set"}),
                CommandParameter.newType("player", CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newType("score", CommandParamType.INT)
        });
        this.commandParameters.put("players-list", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("list",new String[]{"list"}),
                CommandParameter.newType("playername",CommandParamType.WILDCARD_TARGET)//allow *
        });
        this.commandParameters.put("players-operation", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("op",new String[]{"operation"}),
                CommandParameter.newType("targetName",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("targetObjective", CommandParamType.STRING),
                CommandParameter.newEnum("operation",new String[]{"+=","-=","*=","/=","%=","=","<",">","><"}),
                CommandParameter.newType("selector",CommandParamType.TARGET),
                CommandParameter.newType("targetObjective", CommandParamType.STRING)
        });
        this.commandParameters.put("players-random", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("random",new String[]{"random"}),
                CommandParameter.newType("player",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newType("min", CommandParamType.INT),
                CommandParameter.newType("max", CommandParamType.INT)
        });
        this.commandParameters.put("players-reset", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("reset",new String[]{"reset"}),
                CommandParameter.newType("player",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING)
        });
        this.commandParameters.put("players-test", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("test",new String[]{"test"}),
                CommandParameter.newType("player",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newType("min", CommandParamType.WILDCARD_INT),
                CommandParameter.newType("max", CommandParamType.WILDCARD_INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        CommandParser parser = new CommandParser(this,sender,args);
        try{
            String form = parser.matchCommandForm();
            if (form == null){
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return false;
            };
            switch(form){
                case "objectives-add" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();//skip "objectives"

                }
                case "objectives-list" -> {
                }
                case "objectives-remove" -> {

                }
                case "objectives-setdisplay-list-sidebar" -> {

                }
                case "objectives-setdisplay-belowname" -> {

                }
                case "players-add-remove-set" -> {

                }
                case "players-list" -> {

                }
                case "players-operation" -> {

                }
                case "players-random" -> {

                }
                case "players-reset" -> {

                }
                case "players-test" -> {

                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
