package cn.nukkit.command.data;

public class CommandOverload {

    private CommandInput input = new CommandInput();

    private boolean chaining;

    public CommandInput getInput() {
        return input;
    }

    public void setInput(CommandInput input) {
        this.input = input;
    }

    public boolean isChaining() {
        return chaining;
    }

    public void setChaining(boolean chaining) {
        this.chaining = chaining;
    }
}
