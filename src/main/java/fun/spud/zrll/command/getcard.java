package fun.spud.zrll.command;

import fun.spud.zrll.HelloMinecraft;
import fun.spud.zrll.util.MySql;
import fun.spud.zrll.util.random;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;

public class getcard implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            //控制台你获取个鬼啊
            return false;
        }
        HelloMinecraft.instance.getLogger().info("Player " + commandSender.getName() + " have got a spud++.");
        String tid = random.getRandomTID("A-");
        ItemStack ticket = new ItemStack(Material.PAPER);
        ItemMeta im = ticket.getItemMeta();
        Objects.requireNonNull(im).setLore(Collections.singletonList(tid));
        Objects.requireNonNull(im).setDisplayName("[Spud++]");
        ticket.setItemMeta(im);
        new BukkitRunnable() {
            @Override
            public void run() {
                try
                {
                    Connection connection = MySql.getSQLConnection();
                    PreparedStatement preparedstatement = connection.prepareStatement("INSERT INTO ticket (tid,money) VALUES (?, 0);");
                    preparedstatement.setString(1, tid);
                    preparedstatement.execute();
                    preparedstatement.close();
                    connection.close();
                } catch(SQLException | ClassNotFoundException throwables)
                {
                    throwables.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HelloMinecraft.instance);
        Objects.requireNonNull(((Player) commandSender)).sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.newspud++", "You have get your new spud++, id: %s.")), tid));
        ((Player) commandSender).getInventory().addItem(ticket);
        return true;
    }
}
