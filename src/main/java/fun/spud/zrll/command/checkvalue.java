package fun.spud.zrll.command;

import fun.spud.zrll.HelloMinecraft;
import fun.spud.zrll.util.MySql;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class checkvalue implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 1){
            return false;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                String tid = args[0];
                Connection connection;
                try {
                    connection = MySql.getSQLConnection();
                    PreparedStatement preparedstatement = connection.prepareStatement("SELECT * FROM ticket WHERE tid = ?");
                    preparedstatement.setString(1, tid);
                    ResultSet resultset = preparedstatement.executeQuery();
                    if(!resultset.next()){
                        commandSender.sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred, please send the following message to the admins: %s.")), "No such ticket."));
                    }
                    double money = resultset.getDouble("money");
                    preparedstatement.close();
                    connection.close();
                    commandSender.sendMessage(String.format(HelloMinecraft.config.getString("lang.moneyleft", "Money left: %s"), money));
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HelloMinecraft.instance);
        return true;
    }
}
