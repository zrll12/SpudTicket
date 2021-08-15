package fun.spud.zrll;

import fun.spud.zrll.command.breaksign;
import fun.spud.zrll.util.BreakBlockList;
import fun.spud.zrll.util.MySql;
import fun.spud.zrll.util.random;
import fun.spud.zrll.varclass.Equal;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TicketVender  implements Listener {
    BreakBlockList chargeList = new BreakBlockList();

    @EventHandler
    public void getCard(PlayerInteractEvent e){
        if(e.isBlockInHand() || Objects.requireNonNull(e.getClickedBlock()).getType() != Material.OAK_WALL_SIGN || e.getAction() != Action.LEFT_CLICK_BLOCK){
            return;
        }
        Sign sign = (Sign) e.getClickedBlock().getState();
        if(sign.getLine(0).equals("[ticket]")) {
            Equal equal = new Equal();
            if (breaksign.breakBlockList.check(e.getPlayer().getUniqueId(), equal) != null) {
                e.getPlayer().sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.brokeblock", "You have broke a block.")));
                return;
            }
            else{
                e.setCancelled(true);
            }
            if (sign.getLine(1).equals("Get your spud++!")) {
                HelloMinecraft.instance.getLogger().info("Player " + e.getPlayer().getName() + " have got a spud++.");
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
                e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.newspud++", "You have get your new spud++, id: %s")), tid));
                e.getPlayer().getInventory().addItem(ticket);
            }

            if(sign.getLine(1).equals("Charge your spud++!")){
                if(e.getItem() == null){
                    e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred when charging, please send the following message to the admins: %s")), "No such ticket"));
                    return;
                }
                String tid = getTID(e.getItem());
                if(tid == null){
                    e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred when charging, please send the following message to the admins: %s")), "No such ticket"));
                    return;
                }
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        try {
                            Connection connection = MySql.getSQLConnection();
                            PreparedStatement preparedstatement = connection.prepareStatement("SELECT * FROM ticket WHERE tid = ?");
                            preparedstatement.setString(1, tid);
                            ResultSet resultset = preparedstatement.executeQuery();
                            if(!resultset.next()){
                                e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred when charging, please send the following message to the admins: %s")), "No such ticket"));
                                return;
                            }
                            double moneyleft = resultset.getDouble("money");
                            e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.inputamount", "Please input the amount in the chat area, your balance is: %s")), moneyleft));

                            ChargeInfo object = new ChargeInfo();
                            object.setUUID(e.getPlayer().getUniqueId());
                            object.setTid(tid);
                            equal1 cequal = new equal1();
                            chargeList.insert(object, cequal);
                        } catch (SQLException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(HelloMinecraft.instance);
            }

            if(sign.getLine(1).equals("Check money left")){
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(e.getItem() == null){
                            e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred when charging, please send the following message to the admins: %s")), "No such ticket"));
                            return;
                        }
                        String tid = getTID(e.getItem());
                        if(tid == null){
                            e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred when charging, please send the following message to the admins: %s")), "No such ticket"));
                            return;
                        }
                        Connection connection;
                        try {
                            connection = MySql.getSQLConnection();
                            PreparedStatement preparedstatement = connection.prepareStatement("SELECT * FROM ticket WHERE tid = ?");
                            preparedstatement.setString(1, tid);
                            ResultSet resultset = preparedstatement.executeQuery();
                            if(!resultset.next()){
                                e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred, please send the following message to the admins: %s.")), "No such ticket."));
                                return;
                            }
                            double money = resultset.getDouble("money");
                            preparedstatement.close();
                            connection.close();
                            e.getPlayer().sendMessage(String.format(HelloMinecraft.config.getString("lang.moneyleft", "Money left: %s"), money));
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(HelloMinecraft.instance);
            }
        }
    }

    @EventHandler
    public void getChat(PlayerChatEvent e){
        equal1 equal = new equal1();
        ChargeInfo info = new ChargeInfo();
        info.setUUID(e.getPlayer().getUniqueId());
        ChargeInfo check = (ChargeInfo) chargeList.check(info, equal);
        if(check != null){
            e.setCancelled(true);
        }
        else {
            return;
        }
        chargeList.remove(info, equal);
        e.getPlayer().chat("/charge " + check.getTid() + " " + e.getMessage());
    }

    String getTID(ItemStack itemstack){
        ItemMeta itemmeta = itemstack.getItemMeta();
        if(!Objects.requireNonNull(itemmeta).hasLore()){
            return null;
        }
        List<String> lore = itemmeta.getLore();
        if(!itemmeta.getDisplayName().equals("[Spud++]")){
            return null;
        }
        return Objects.requireNonNull(lore).get(0);
    }
}

class equal1 extends Equal{
    @Override
    public boolean cmp(Object a, Object b) {
        ChargeInfo ca = (ChargeInfo) a,cb = (ChargeInfo) b;
        return ca.getUUID() == cb.getUUID();
    }
}

class ChargeInfo {
    UUID uuid;
    String tid;

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getTid() {
        return tid;
    }
}
