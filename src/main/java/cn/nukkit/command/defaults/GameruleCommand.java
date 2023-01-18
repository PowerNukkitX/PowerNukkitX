package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;

import java.util.*;

public class GameruleCommand extends VanillaCommand {

    public GameruleCommand(String name) {
        super(name, "commands.gamerule.description");
        this.setPermission("nukkit.command.gamerule");
        this.commandParameters.clear();

        GameRules rules = GameRules.getDefault();
        List<String> boolGameRules = new ArrayList<>();
        List<String> intGameRules = new ArrayList<>();
        List<String> floatGameRules = new ArrayList<>();
        List<String> unknownGameRules = new ArrayList<>();

        rules.getGameRules().forEach((rule, value) -> {
            if (rule.isDeprecated()) {
                return;
            }
            switch (value.getType()) {
                case BOOLEAN -> boolGameRules.add(rule.getName().toLowerCase());
                case INTEGER -> intGameRules.add(rule.getName().toLowerCase());
                case FLOAT -> floatGameRules.add(rule.getName().toLowerCase());
                default -> unknownGameRules.add(rule.getName().toLowerCase());
            }
        });
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        if (!boolGameRules.isEmpty()) {
            this.commandParameters.put("boolGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("BoolGameRule", boolGameRules)),
                    CommandParameter.newEnum("value", true, CommandEnum.ENUM_BOOLEAN)
            });
        }
        if (!intGameRules.isEmpty()) {
            this.commandParameters.put("intGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("IntGameRule", intGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.INT)
            });
        }
        if (!floatGameRules.isEmpty()) {
            this.commandParameters.put("floatGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("FloatGameRule", floatGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.FLOAT)
            });
        }
        if (!unknownGameRules.isEmpty()) {
            this.commandParameters.put("unknownGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("UnknownGameRule", unknownGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.STRING)
            });
        }
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        GameRules rules = sender.getPosition().level.getGameRules();
        var list = result.getValue();
        String ruleStr = list.getResult(0);
        if (result.getKey().equals("default")) {
            StringJoiner rulesJoiner = new StringJoiner(", ");
            for (GameRule rule : rules.getRules()) {
                rulesJoiner.add(rule.getName().toLowerCase());
            }
            log.addSuccess(rulesJoiner.toString()).output();
            return 1;
        } else if (!list.hasResult(1)) {
            Optional<GameRule> gameRule = GameRule.parseString(ruleStr);
            if (gameRule.isEmpty() || !rules.hasRule(gameRule.get())) {
                log.addSyntaxErrors(0).output();
                return 0;
            }
            log.addSuccess(gameRule.get().getName().toLowerCase() + " = " + rules.getString(gameRule.get())).output();
            return 1;
        }

        Optional<GameRule> optionalRule = GameRule.parseString(ruleStr);
        if (optionalRule.isEmpty()) {
            log.addSyntaxErrors(0).output();
            return 0;
        }
        switch (result.getKey()) {
            case "boolGameRules" -> {
                boolean value = list.getResult(1);
                rules.setGameRule(optionalRule.get(), value);
            }
            case "intGameRules" -> {
                int value = list.getResult(1);
                rules.setGameRule(optionalRule.get(), value);
            }
            case "floatGameRules" -> {
                float value = list.getResult(1);
                rules.setGameRule(optionalRule.get(), value);
            }
            case "unknownGameRules" -> {
                String value = list.getResult(1);
                rules.setGameRules(optionalRule.get(), value);
            }
        }
        var str = list.getResult(1);
        log.addSuccess("commands.gamerule.success", optionalRule.get().getName().toLowerCase(), str.toString()).output();
        return 1;
    }
}
