package fun.spud.zrll.command;

import fun.spud.zrll.HelloMinecraft;
import fun.spud.zrll.varclass.Equal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class donebreaksign implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Equal equal = new Equal();
        if (breaksign.breakBlockList.remove(((Player) commandSender).getUniqueId(), equal)) {
            commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.donebreaksign", "Now you can not break signs anymore.")));
        } else {
            commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.notinarray", "You are not in the array which can break the sign.")));
        }
        return true;
    }
}
