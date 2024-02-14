package cn.nukkit.command.tree.node;

/**
 * @author daoge_cmd <br>
 * Date: 2023/6/11 <br>
 * PowerNukkitX Project <br>
 */
public class RelativeFloatNode extends RelativeNumberNode<Float> {
    @Override
    public void fill(String arg) {
        if (arg.startsWith("~")) {
            if (arg.length() == 1) {
                this.value = 0f;
            } else {
                try {
                    this.value = Float.parseFloat(arg.substring(1));
                } catch (NumberFormatException e) {
                    this.error();
                }
            }
        } else {
            try {
                this.value = Float.parseFloat(arg);
            } catch (NumberFormatException e) {
                this.error();
            }
        }
    }

    @Override
    public Float get(Float base) {
        return base + this.value;
    }
}
