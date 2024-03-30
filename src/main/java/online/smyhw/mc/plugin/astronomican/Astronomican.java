package online.smyhw.mc.plugin.astronomican;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static online.smyhw.mc.plugin.astronomican.Utils.load_cfg;

public final class Astronomican extends JavaPlugin implements Listener {

    public static Plugin plugin;

    public static Logger logger;

    public static FileConfiguration config;

    public static List<Torch> exist_torch;

    //效果持续时间
    public static int dur_time;
    //效果应用间隔
    public static int effect_interval;

    //异步扫描线程
    public static Controller controller;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("Astronomican·星炬 - 启动中...");
        this.getLogger().info("处理环境中....");
        plugin = this;
        logger = this.getLogger();
        logger.info("加载配置文件...");
        saveDefaultConfig();
        config = this.getConfig();
        logger.info("初始化星炬...");
        boolean async = config.getBoolean("config.async_enable",false);
        if(async){
            class async_loader extends Thread{
                public async_loader(){
                    this.start();
                }
                public void run(){
                    load_cfg();
                    controller = new Controller();
                }
            }
            new async_loader();
        }else{
            load_cfg();
            controller = new Controller();
        }

        this.getLogger().info("Astronomican·星炬 - 启动流程完成!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Astronomican·星炬 - 卸载中...");
        controller.s_stop_it();
        this.getLogger().info("Astronomican·星炬 - 卸载完成!");
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(args.length<1){
            return false;
        }
        switch (args[0]){
            case "reload":
                commandSender.sendMessage("软重载...");
                this.onDisable();
                this.onDisable();
                commandSender.sendMessage("软重载完成!");
                return true;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(args.length >= 1) return null;
            return Collections.singletonList("reload");
        }
        return null;
    }

}
