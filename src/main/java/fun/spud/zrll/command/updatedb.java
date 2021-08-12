package fun.spud.zrll.command;

import fun.spud.zrll.HelloMinecraft;
import fun.spud.zrll.util.MySql;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Objects;

public class updatedb implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            Connection connection = MySql.getSQLConnection();
            Statement statement = connection.createStatement();
            ResultSet resultset;
            FileConfiguration ticketdata = null, dealsdata = null;
            int maxticket = 0, maxdeals = 0;
            //ticket
            resultset = statement.executeQuery("SELECT * FROM ticket");
            for(int i = 0; resultset.next(); i++) {
                ticketdata.set(i + ".tid", resultset.getString("tid"));
                ticketdata.set(i + ".money", resultset.getInt("money"));
                maxticket = i;
            }
            //deals
            resultset = statement.executeQuery("SELECT * FROM deals");
            for(int i = 0; resultset.next(); i++) {
                dealsdata.set(i + ".time", resultset.getLong("time"));
                dealsdata.set(i + ".tid", resultset.getString("tid"));
                dealsdata.set(i + ".money", resultset.getInt("money"));
                dealsdata.set(i + ".action", resultset.getInt("action"));
                dealsdata.set(i + ".station", resultset.getString("station"));
                maxdeals = i;
            }
            //drop all tables
            statement.execute("DROP TABLE ticket");
            statement.execute("DROP TABLE deals");
            MySql.InitTable();
            //insert ticket
            for (int i = 0; i <= maxticket; i++) {
                PreparedStatement preparedstatement = connection.prepareStatement("INSERT INTO ticket (tid,money) VALUES (?, ?);");
                preparedstatement.setString(1, Objects.requireNonNull(ticketdata).getString(i + ".tid", ""));
                preparedstatement.setInt(2, ticketdata.getInt(i + ".money", 0));
                preparedstatement.executeQuery();
                preparedstatement.close();
            }
            //insert deals
            for (int i = 0; i <= maxdeals; i++) {
                PreparedStatement preparedstatement = connection.prepareStatement("INSERT INTO deals (time,tid,station,action,money) VALUES (?, ?, ?, ?, ?);");
                preparedstatement.setLong(1, Objects.requireNonNull(dealsdata).getLong(i + ".time", 0));
                preparedstatement.setString(2, dealsdata.getString(i + ".tid", "0"));
                preparedstatement.setString(3, dealsdata.getString(i + ".station", ""));
                preparedstatement.setInt(4, dealsdata.getInt(i + ".action", 0));
                preparedstatement.setInt(5, dealsdata.getInt(i + ".station", 0));
                preparedstatement.executeQuery();
                preparedstatement.close();
            }
            connection.close();
            if(commandSender instanceof Player){
                commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.dbupdated", "Database information changed.")));
            }
            else{
                commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.dbupdated", "All tables are updated.")));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
