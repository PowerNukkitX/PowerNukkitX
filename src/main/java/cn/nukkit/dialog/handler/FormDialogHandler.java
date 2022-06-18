package cn.nukkit.dialog.handler;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.dialog.response.FormResponseDialog;

import java.util.function.Consumer;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface FormDialogHandler {

    static FormDialogHandler withoutPlayer(Consumer<FormResponseDialog> responseConsumer) {
        return (player, response) -> responseConsumer.accept(response);
    }

    void handle(Player player, FormResponseDialog response);
}
