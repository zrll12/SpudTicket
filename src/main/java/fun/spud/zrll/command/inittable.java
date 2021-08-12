package fun.spud.zrll.command;

import fun.spud.zrll.HelloMinecraft;
import fun.spud.zrll.util.MySql;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class inittable implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        MySql.InitTable();
        if(commandSender instanceof Player){
            commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.tableinit", "All table initialized.")));
        }
        else{
            HelloMinecraft.instance.getLogger().info(Objects.requireNonNull(HelloMinecraft.config.getString("lang.tableinit", "All table initialized.")));
        }
        return true;
    }
}
