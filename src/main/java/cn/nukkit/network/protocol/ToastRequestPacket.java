package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ToastRequestPacket extends DataPacket{

    public String title;
    public String content;

    public ToastRequestPacket() {
        title = "Default Title"; // 设置默认的title值
        content = "Default Content"; // 设置默认的content值
    }

    /**
     *
     * @param title 标题
     * @param content 内容
     */
    public  ToastRequestPacket(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 获取进度标题字符串
     * @return String
     */
    public String getTitle() {
        return title;
    }
    /**
     * 设置标题字符串
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * 获取进度内容字符串
     * @return String
     */
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public byte pid() {
        return ProtocolInfo.TOAST_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        this.title = this.getString();
        this.content = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.title);
        this.putString(this.content);
    }
}
