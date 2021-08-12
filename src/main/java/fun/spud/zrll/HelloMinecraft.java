package fun.spud.zrll;

import fun.spud.zrll.command.*;
import fun.spud.zrll.util.ComputerId;
import fun.spud.zrll.util.Des;
import fun.spud.zrll.util.MySql;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.logging.Logger;

public class HelloMinecraft extends JavaPlugin {
    static public FileConfiguration config;
    static public Logger logger;
    static public JavaPlugin instance;
    static public Economy econ = null;


    @Override
    public void onEnable(){
        getLogger().info("Hello Minecraft!");
        saveDefaultConfig();
        instance = this;
        logger = getLogger();
        config = getConfig();
        if(config.getDouble("configversion") < 1.0){
            logger.info("Configure file outdated, please update it as to provide full experiences.");
        }

        Objects.requireNonNull(Bukkit.getPluginCommand("breaksign")).setExecutor(new breaksign());
        Objects.requireNonNull(Bukkit.getPluginCommand("donebreaksign")).setExecutor(new donebreaksign());
        Objects.requireNonNull(Bukkit.getPluginCommand("charge")).setExecutor(new charge());
        Objects.requireNonNull(Bukkit.getPluginCommand("linkdb")).setExecutor(new linkdb());
        Objects.requireNonNull(Bukkit.getPluginCommand("inittable")).setExecutor(new inittable());
        Objects.requireNonNull(Bukkit.getPluginCommand("getcard")).setExecutor(new getcard());
        Objects.requireNonNull(Bukkit.getPluginCommand("updatedb")).setExecutor(new updatedb());
        Objects.requireNonNull(Bukkit.getPluginCommand("checkvalue")).setExecutor(new checkvalue());

        Bukkit.getPluginManager().registerEvents(new Gate(), this);
        Bukkit.getPluginManager().registerEvents(new TicketVender(), this);

        Des des = null;
        try {
            des = new Des(ComputerId.getComputerID());
        } catch (NoSuchAlgorithmException e) {
            this.getLogger().info("Can not find algorithm: md5.");
        }
        try {
            assert des != null;
            String username = des.decrypt(config.getString("MySql.username"));
            String password = des.decrypt(config.getString("MySql.password"));
            MySql.UpdateInformation(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!setupEconomy()){
            getLogger().info("Vault not detected, plugin disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Plugin loaded.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("111");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    @Override
    public void onDisable(){
        getConfig().set("MySql.ip", config.getString("MySql.ip", "127.0.0.1"));
        getConfig().set("MySql.port", config.getInt("MySql.port", 3306));
        getConfig().set("MySql.username", config.getString("MySql.username", "user"));
        getConfig().set("MySql.password", config.getString("MySql.password", "0000"));
        getConfig().set("MySql.dbname", config.getString("MySql.dbname", "spudticket"));
        saveConfig();
    }
}
