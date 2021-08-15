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
        Bukkit.getPluginManager().registerEvents(new GateOperation(), this);

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

    public void saveDefaultConfig(){
        if(getConfig().getDouble("configversion", 0) <= 1.1){
            logger.info("Configure file outdated, now updating it to the least version.");
            getConfig().set("configversion", 1.1);
            getConfig().set("MySql.jdbcname", getConfig().getString("MySql.jdbcname", "com.mysql.jdbc.Driver"));
            getConfig().set("MySql.ip", getConfig().getString("MySql.ip", "127.0.0.1"));
            getConfig().set("MySql.port", getConfig().getInt("MySql.port", 3306));
            getConfig().set("MySql.username", getConfig().getString("MySql.username", "user"));
            getConfig().set("MySql.password", getConfig().getString("MySql.password", "0000"));
            getConfig().set("MySql.dbname", getConfig().getString("MySql.dbname", "spudticket"));
            getConfig().set("ticket-price", 2);
            getConfig().set("lang.notaplayer", "不是玩家，请输入完整的参数");
            getConfig().set("lang.dbinformationchange", "数据库信息已刷新");
            getConfig().set("tableinit", "已初始化所有数据表格");
            getConfig().set("newspud++", "已为您颁发新的spud++, id为 %s");
            getConfig().set("readytobreaksign", "您现在可以破坏告示牌了");
            getConfig().set("donebreaksign", "您现在不能再破坏告示牌了");
            getConfig().set("notinarray", "您不在可以破坏告示牌的列表里");
            getConfig().set("brokeblock", "您破坏了一个方块");
            getConfig().set("noenoughmoney", "您没有足够的余额");
            getConfig().set("gateclosed", "该检票口已关闭");
            getConfig().set("dbupdated", "数据库已经全部更新");
            getConfig().set("passgate", "%s 从 %s，余额：%s");
            getConfig().set("getin", "进站");
            getConfig().set("getout", "出站");
            getConfig().set("chargescucceed", "您已成功为将$%s充值进%s，您目前的余额为：%s");
            getConfig().set("chargeuke", "遇到未知错误，请将以下信息提交给管理员：%s");
            getConfig().set("moneyleft", "余额：%s");
            getConfig().set("inputamount", "请在聊天栏输入充值金额，您目前的余额为：%s");
            getConfig().set("inputfailed", "您的输入不合法，请输入一个数字，充值失败，请重新点击告示牌");
            saveConfig();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
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
