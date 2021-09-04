package fun.spud.zrll.command;

import fun.spud.zrll.HelloMinecraft;
import fun.spud.zrll.util.BreakBlockList;
import fun.spud.zrll.varclass.Equal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class breaksign implements CommandExecutor {
    public static BreakBlockList breakBlockList = new BreakBlockList();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Equal equal = new Equal();
        breakBlockList.insert(((Player) commandSender).getUniqueId(), equal);
        commandSender.sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.readytobreaksign", "Now you are ready to break the signs.")));
        return true;
    }
}
