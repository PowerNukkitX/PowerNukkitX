package cn.nukkit.form.window;

import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.utils.JSONUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public abstract class FormWindow {
    protected transient boolean $1 = false;
    protected final transient List<FormResponseHandler> handlers = new ObjectArrayList<>();
    /**
     * @deprecated 
     */
    

    public String getJSONData() {
        return JSONUtils.to(this);
    }

    public abstract void setResponse(String data);

    public abstract FormResponse getResponse();
    /**
     * @deprecated 
     */
    

    public boolean wasClosed() {
        return closed;
    }
    /**
     * @deprecated 
     */
    

    public void addHandler(FormResponseHandler handler) {
        this.handlers.add(handler);
    }

    public List<FormResponseHandler> getHandlers() {
        return handlers;
    }

}
