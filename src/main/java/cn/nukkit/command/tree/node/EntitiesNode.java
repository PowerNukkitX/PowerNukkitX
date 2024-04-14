package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 解析为{@code List<Entity>}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#TARGET TARGET}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class EntitiesNode extends TargetNode<Entity> {

    //todo 支持uuid 或者 xuid
    @Override
    public void fill(String arg) {
        List<Entity> entities;
        if (arg.isBlank()) {
            this.error();
        } else if (EntitySelectorAPI.getAPI().checkValid(arg)) {
            try {
                entities = EntitySelectorAPI.getAPI().matchEntities(paramList.getParamTree().getSender(), arg);
            } catch (SelectorSyntaxException exception) {
                error(exception.getMessage());
                return;
            }
            this.value = entities;
        } else {
            entities = Lists.newArrayList();
            Player player = Server.getInstance().getPlayer(arg);
            if (player != null) {
                entities.add(player);
            }
            this.value = entities;
        }
    }
}
