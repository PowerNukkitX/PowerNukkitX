package cn.nukkit.command.tree.node;

import cn.nukkit.command.utils.RawText;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import com.google.gson.JsonSyntaxException;


/**
 * 解析为{@link RawText}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#RAWTEXT RAWTEXT}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class RawTextNode extends ParamNode<RawText> {

    @Override
    public void fill(String arg) {
        try {
            this.value = RawText.fromRawText(arg);
        } catch (JsonSyntaxException e) {
            int index;
            String s = e.getMessage();
            s = s.substring(s.indexOf("column") + 7, s.indexOf("path") - 1);
            try {
                index = Integer.parseInt(s);
            } catch (NumberFormatException ignore) {
                this.error();
                return;
            }
            if (index == arg.length() + 1) {
                this.error(new CommandOutputMessage("JSON parsing error:"),
                        new CommandOutputMessage(arg.substring(0, arg.length() - 1) + "" + arg.substring(arg.length() - 1) + "§f<<"));
                return;
            } else if (index == 1) {
                this.error(new CommandOutputMessage("JSON parsing error:"),
                        new CommandOutputMessage("§f>>§c" + arg.charAt(0) + arg.substring(1)));
                return;
            }
            index -= 2;
            this.error(new CommandOutputMessage("JSON parsing error:"),
                    new CommandOutputMessage(arg.substring(0, index) + "§f<<§c" + arg.charAt(index) + arg.substring(index + 1, arg.length() - 1)));
        }
    }
}
