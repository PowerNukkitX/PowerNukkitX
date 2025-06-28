package cn.nukkit.form.window;

import cn.nukkit.Player;
import cn.nukkit.form.response.ModalResponse;
import cn.nukkit.network.protocol.types.ModalFormCancelReason;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
public class ModalForm extends Form<ModalResponse> {
    protected String content = "";

    @Setter(AccessLevel.NONE)
    protected Consumer<Player> yes = player -> {};
    @Setter(AccessLevel.NONE)
    protected Consumer<Player> no = player -> {};

    protected String yesText = "";
    protected String noText = "";

    public ModalForm(String title) {
        super(title);
    }

    public ModalForm(String title, String content) {
        super(title);

        this.content = content;
    }

    @Override
    public ModalForm title(String title) {
        return (ModalForm) super.title(title);
    }

    public ModalForm text(String yes, String no) {
        this.yesText = yes;
        this.noText = no;
        return this;
    }

    public ModalForm onYes(Consumer<Player> yes) {
        this.yes = yes;
        return this;
    }

    public ModalForm onNo(Consumer<Player> no) {
        this.no = no;
        return this;
    }

    public ModalForm yes(String text, Consumer<Player> yes) {
        this.yesText = text;
        this.yes = yes;
        return this;
    }

    public ModalForm no(String text, Consumer<Player> no) {
        this.noText = text;
        this.no = no;
        return this;
    }

    public void supplyYes(Player player) {
        this.yes.accept(player);
    }

    public void supplyNo(Player player) {
        this.no.accept(player);
    }

    @Override
    public ModalForm onSubmit(BiConsumer<Player, ModalResponse> submitted) {
        return (ModalForm) super.onSubmit(submitted);
    }
    @Override
    public ModalForm onClose(Consumer<Player> callback) {
        return (ModalForm) super.onClose(callback);
    }

    @Override
    public ModalForm onCancel(BiConsumer<Player, ModalFormCancelReason> cancel) {
        return (ModalForm) super.onCancel(cancel);
    }

    @Override
    public ModalForm send(Player player) {
        return (ModalForm) super.send(player);
    }

    @Override
    public ModalForm send(Player player, int id) {
        return (ModalForm) super.send(player, id);
    }

    @Override
    public ModalForm sendUpdate(Player player) {
        return (ModalForm) super.sendUpdate(player);
    }

    @Override
    public String windowType() {
        return "modal";
    }

    @Override
    public String toJson() {
        this.object.addProperty("type", this.windowType());
        this.object.addProperty("title", this.title);
        this.object.addProperty("content", this.content);
        this.object.addProperty("button1", this.yesText);
        this.object.addProperty("button2", this.noText);

        return this.object.toString();
    }

    @Override
    public ModalResponse respond(Player player, String formData, ModalFormCancelReason cancelReason) {
        if(cancelReason != null) {
            if(cancelReason == ModalFormCancelReason.USER_CLOSED) this.supplyClosed(player);
            else this.supplyCancelled(player, cancelReason);
            return null;
        }

        boolean yes;
        if (!super.handle(player, formData)) {
            this.supplyClosed(player);
            return null;
        } else yes = formData.trim().equals("true");

        if (yes) this.supplyYes(player);
        else this.supplyNo(player);

        ModalResponse response = new ModalResponse(yes ? 0 : 1, yes);
        this.supplySubmitted(player, response);
        return response;
    }

    @Override
    public <M> ModalForm putMeta(String key, M object) {
        return (ModalForm) super.putMeta(key, object);
    }
}
