package cn.nukkit.command.data.args;

@Deprecated
public class CommandArg {

    private CommandArgRules[] rules;
    private String selector;

    public CommandArgRules[] getRules() {
        return rules;
    }

    public String getSelector() {
        return selector;
    }
}
