package fun.spud.zrll.command;

import fun.spud.zrll.HelloMinecraft;
import fun.spud.zrll.util.ComputerId;
import fun.spud.zrll.util.Des;
import fun.spud.zrll.util.MySql;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Objects;


public class linkdb implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (Arrays.stream(args).count() < 5) {
            return false;
        }
        Des des = null;
        try {
            des = new Des(ComputerId.getComputerID());
        } catch (NoSuchAlgorithmException e) {
            HelloMinecraft.instance.getLogger().info("Can not find algorithm: md5.");
        }
        HelloMinecraft.config.set("MySql.ip", args[0]);
        HelloMinecraft.config.set("MySql.port", args[1]);
        HelloMinecraft.config.set("MySql.dbname", args[2]);
        try {
            HelloMinecraft.config.set("MySql.username", Objects.requireNonNull(des).encrypt(args[3]));
            HelloMinecraft.config.set("MySql.password", des.encrypt(args[4]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MySql.UpdateInformation(args[3], args[4]);
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection;
                Statement statement;
                try {
                    connection = MySql.getSQLConnection();
                    statement = connection.createStatement();
                    statement.execute("CREATE TABLE IF NOT EXISTS `ticket`  (\n" +
                            "  `tid` TEXT NOT NULL,\n" +
                            "  `money` double NOT NULL,\n" +
                            "  PRIMARY KEY (`money`)\n" +
                            ");");
                    statement.close();
                    connection.close();
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HelloMinecraft.instance);
        if (commandSender instanceof Player) {
            commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.dbinformationchang", "Database information changed.")));
        } else {
            HelloMinecraft.instance.getLogger().info(Objects.requireNonNull(HelloMinecraft.config.getString("lang.dbinformationchang", "Database information changed.")));
        }
        return true;
    }
}
