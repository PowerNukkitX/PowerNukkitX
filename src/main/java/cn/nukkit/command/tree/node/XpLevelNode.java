package cn.nukkit.command.tree.node;

public class XpLevelNode extends ParamNode<Integer> {
    @Override
    public void fill(String arg) {
        if (arg.endsWith("l") || arg.endsWith("L")) {
            try {
                this.value = Integer.parseInt(arg.substring(0, arg.length() - 1));
            } catch (NumberFormatException e) {
                this.error();
            }
        } else error();
    }
}
