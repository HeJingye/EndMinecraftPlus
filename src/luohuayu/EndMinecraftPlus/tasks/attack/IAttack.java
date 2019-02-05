package luohuayu.EndMinecraftPlus.tasks.attack;

import java.util.Map;

public abstract class IAttack {
    public String ip;
    public int port;

    public int attack_time;
    public int attack_maxconnect;
    public int attack_joinsleep;

    public IAttack(String ip, int port, int time, int maxconnect, int joinsleep) {
        this.ip = ip;
        this.port = port;
        this.attack_time = time;
        this.attack_maxconnect = maxconnect;
        this.attack_joinsleep = joinsleep;
    }

    public abstract void start();

    public abstract void stop();
}
