package cn.nukkit.command.selector.args.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import com.dfsek.terra.lib.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class DZ extends ScopeArgument {
    @Nullable
    @Override
    public List<Predicate<Entity>> getPredicates(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        var z = basePos.getZ();
        var dz = Double.parseDouble(arguments[0]);
        return Lists.newArrayList(entity -> ParseUtils.checkBetween(z, z + dz, entity.getZ()));
    }

    @Override
    public String getKeyName() {
        return "dz";
    }
}
