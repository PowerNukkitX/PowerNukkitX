package cn.nukkit.command.data.args;

@Deprecated
public class CommandArgRules {

    private boolean inverted;
    private String name;
    private String value;

    public boolean isInverted() {
        return inverted;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
