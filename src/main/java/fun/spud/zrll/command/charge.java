package fun.spud.zrll.command;

import fun.spud.zrll.HelloMinecraft;
import fun.spud.zrll.util.MySql;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class charge implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 2 && !(command instanceof Player)){
            HelloMinecraft.config.getString("lang.notaplayer", "Not a player, please provide full args.");
        }
        if (args.length == 3 && (!commandSender.hasPermission("spud.ticketmanage"))) {
            commandSender.sendMessage("Only admins can do this!");
            return true;
        }
        if (args.length < 2){
            return false;
        }
        String player = commandSender.getName();
        if(args.length == 3){
            player = args[2];
        }
        double amount;
        try{
            amount = Double.parseDouble(args[1]);
        } catch (IllegalArgumentException NumberFormatException){
            commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.inputfailed", "Error input, this is not a number")));
            return true;
        }
        if(HelloMinecraft.econ.getBalance(Bukkit.getPlayer(player)) <= amount){
            System.out.println(HelloMinecraft.econ.getBalance(Bukkit.getPlayer(player)));
            commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.noenoughmoney", "You do not have enough money.")));
            return true;
        }
        String finalPlayer = player;
        new BukkitRunnable(){
            @Override
            public void run() {
                Connection connection;
                try {
                    connection = MySql.getSQLConnection();
                    PreparedStatement preparedstatement = connection.prepareStatement("SELECT * FROM ticket WHERE tid=?");
                    preparedstatement.setString(1, args[0]);
                    preparedstatement.execute();
                    ResultSet resultset = preparedstatement.getResultSet();
                    if(!resultset.next()){
                        commandSender.sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred, please send the following message to the admins: %s")), "No such ticket."));
                    } EconomyResponse response = HelloMinecraft.econ.withdrawPlayer(Bukkit.getPlayer(finalPlayer), Double.parseDouble(args[1]));
                    if(!(response.transactionSuccess()) || response.amount != Double.parseDouble(args[1])){
                        commandSender.sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred when charging, please send the following message to the admins: %s.")), response.errorMessage));
                        return;
                    }
                    double money = resultset.getDouble("money");
                    preparedstatement.close();
                    money += amount;
                    preparedstatement = connection.prepareStatement("UPDATE ticket\n" +
                            "SET money=?\n" +
                            "WHERE tid=?");
                    preparedstatement.setDouble(1, money);
                    preparedstatement.setString(2, args[0]);
                    preparedstatement.execute();
                    preparedstatement = connection.prepareStatement("INSERT INTO deals (time,tid,station,action, money) VALUES (?, ?, 0, 3, ?);");
                    preparedstatement.setLong(1, System.currentTimeMillis());
                    preparedstatement.setString(2, args[0]);
                    preparedstatement.setDouble(3, amount);
                    preparedstatement.execute();
                    preparedstatement.close();
                    connection.close();
                    String message = HelloMinecraft.config.getString("lang.chargescucceed", "You have successfully charged %s into %s. Now yu have %s");
                    if(commandSender instanceof Player){
                        assert message != null;
                        commandSender.sendMessage(String.format(message, args[1], args[0], money));
                    }
                    HelloMinecraft.instance.getLogger().info(finalPlayer + " has charged " + args[1] + " in to " + args[0] + ". Now you have " + money + ".");
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HelloMinecraft.instance);
        return true;
    }
}
