package cn.nukkit.command.data;

public class CommandInput {

    private CommandParameter[] parameters = CommandParameter.EMPTY_ARRAY;

    public CommandParameter[] getParameters() {
        return parameters;
    }

    public void setParameters(CommandParameter[] parameters) {
        this.parameters = parameters;
    }
}
