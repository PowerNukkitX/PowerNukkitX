package cn.nukkit.form.response;

/**
 * Interface used for {@link cn.nukkit.form.window.Form}
 */
public abstract class Response {

    final CustomResponse asCustom() {
        return as(CustomResponse.class);
    }

    final ModalResponse asModal() {
        return as(ModalResponse.class);
    }

    final SimpleResponse asSimple() {
        return as(SimpleResponse.class);
    }

    private  <T extends Response> T as(Class<T> type) {
        return type.cast(this);
    }

}
