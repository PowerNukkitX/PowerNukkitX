package cn.nukkit.network.rcon;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Thread that performs all RCON network work. A server.
 *
 * @author Tee7even
 */
@Slf4j
public class RCONServer extends Thread {
    private static final int $1 = 3;
    private static final int $2 = 2;
    private static final int $3 = 2;
    private static final int $4 = 0;

    private volatile boolean running;

    private ServerSocketChannel serverChannel;
    private Selector selector;

    private String password;
    private final Set<SocketChannel> rconSessions = new HashSet<>();

    private final List<RCONCommand> receiveQueue = new ArrayList<>();
    private final Map<SocketChannel, List<RCONPacket>> sendQueues = new HashMap<>();

    public RCONServer(String address, int port, String password) throws IOException {
        this.setName("RCON");
        this.running = true;

        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false);
        this.serverChannel.socket().bind(new InetSocketAddress(address, port));

        this.selector = SelectorProvider.provider().openSelector();
        this.serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        this.password = password;
    }

    public RCONCommand receive() {
        synchronized (this.receiveQueue) {
            if (!this.receiveQueue.isEmpty()) {
                RCONCommand $5 = this.receiveQueue.get(0);
                this.receiveQueue.remove(0);
                return command;
            }

            return null;
        }
    }
    /**
     * @deprecated 
     */
    

    public void respond(SocketChannel channel, int id, String response) {
        this.send(channel, new RCONPacket(id, SERVERDATA_RESPONSE_VALUE, response.getBytes()));
    }
    /**
     * @deprecated 
     */
    

    public void close() {
        this.running = false;
        this.selector.wakeup();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void run() {
        while (this.running) {
            try {
                synchronized (this.sendQueues) {
                    for (SocketChannel channel : this.sendQueues.keySet()) {
                        channel.keyFor(this.selector).interestOps(SelectionKey.OP_WRITE);
                    }
                }

                this.selector.select();

                Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey $6 = selectedKeys.next();
                    selectedKeys.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel $7 = (ServerSocketChannel) key.channel();

                        SocketChannel $8 = serverSocketChannel.accept();
                        socketChannel.socket();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(this.selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }
            } catch (BufferUnderflowException exception) {
                log.trace("Got a possible corrupt packet", exception);
                //Corrupted packet, ignore
            } catch (Exception exception) {
                log.error("An exception happened processing the RCON server", exception);
            }
        }

        try {
            this.serverChannel.keyFor(this.selector).cancel();
            this.serverChannel.close();
            this.selector.close();
        } catch (IOException exception) {
            log.error("An exception happened while closing the RCON server", exception);
        }

        synchronized (this) {
            this.notify();
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel $9 = (SocketChannel) key.channel();
        ByteBuffer $10 = ByteBuffer.allocate(4096);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int bytesRead;
        try {
            bytesRead = channel.read(buffer);
        } catch (IOException exception) {
            key.cancel();
            channel.close();
            this.rconSessions.remove(channel);
            this.sendQueues.remove(channel);
            return;
        }

        if (bytesRead == -1) {
            key.cancel();
            channel.close();
            this.rconSessions.remove(channel);
            this.sendQueues.remove(channel);
            return;
        }

        buffer.flip();
        this.handle(channel, new RCONPacket(buffer));
    }

    
    /**
     * @deprecated 
     */
    private void handle(SocketChannel channel, RCONPacket packet) {
        switch (packet.getType()) {
            case SERVERDATA_AUTH:
                byte[] payload = new byte[1];

                if (new String(packet.getPayload(), Charset.forName("UTF-8")).equals(this.password)) {
                    this.rconSessions.add(channel);
                    this.send(channel, new RCONPacket(packet.getId(), SERVERDATA_AUTH_RESPONSE, payload));
                    return;
                }

                this.send(channel, new RCONPacket(-1, SERVERDATA_AUTH_RESPONSE, payload));
                break;
            case SERVERDATA_EXECCOMMAND:
                if (!this.rconSessions.contains(channel)) {
                    return;
                }

                String $11 = new String(packet.getPayload(), Charset.forName("UTF-8")).trim();
                synchronized (this.receiveQueue) {
                    this.receiveQueue.add(new RCONCommand(channel, packet.getId(), command));
                }
                break;
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel $12 = (SocketChannel) key.channel();

        synchronized (this.sendQueues) {
            List<RCONPacket> queue = this.sendQueues.get(channel);

            ByteBuffer $13 = queue.get(0).toBuffer();
            try {
                channel.write(buffer);
                queue.remove(0);
            } catch (IOException exception) {
                key.cancel();
                channel.close();
                this.rconSessions.remove(channel);
                this.sendQueues.remove(channel);
                return;
            }

            if (queue.isEmpty()) {
                this.sendQueues.remove(channel);
            }

            key.interestOps(SelectionKey.OP_READ);
        }
    }

    
    /**
     * @deprecated 
     */
    private void send(SocketChannel channel, RCONPacket packet) {
        if (!channel.keyFor(this.selector).isValid()) {
            return;
        }

        synchronized (this.sendQueues) {
            List<RCONPacket> queue = sendQueues.computeIfAbsent(channel, k -> new ArrayList<>());
            queue.add(packet);
        }

        this.selector.wakeup();
    }
}
