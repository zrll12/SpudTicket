package fun.spud.zrll;

import fun.spud.zrll.command.breaksign;
import fun.spud.zrll.events.OpenDoorEvent;
import fun.spud.zrll.util.MySql;
import fun.spud.zrll.varclass.Equal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Gate implements Listener {
    Location getDoorPosition(BlockFace facing, Location sign, int direction){
        if (direction == 3){
            return null;
        }
        Location gate = sign;
        switch (facing){
            case EAST:
                gate = gate.add(-1,0,0);
                if (direction == 1){
                    //->
                    gate = gate.add(0,0,-1);
                }
                if (direction == 2){
                    //<-
                    gate = gate.add(0,0,1);
                }
                break;
            case WEST:
                gate = gate.add(1,0,0);
                if (direction == 1){
                    //->
                    gate = gate.add(0,0,1);
                }
                if (direction == 2){
                    //<-
                    gate = gate.add(0,0,-1);
                }
                break;
            case NORTH:
                gate = gate.add(0,0,1);
                if (direction == 1){
                    //->
                    gate = gate.add(-1,0,0);
                }
                if (direction == 2){
                    //<-
                    gate = gate.add(1,0,0);
                }
                break;
            case SOUTH:
                gate = gate.add(0,0,-1);
                if (direction == 1){
                    //->
                    gate = gate.add(1,0,0);
                }
                if (direction == 2){
                    //<-
                    gate = gate.add(-1,0,0);
                }
                break;
            default:
                return null;
        }
        return gate;
    }

    @EventHandler
    public void passGate(PlayerInteractEvent e){
        if(e.isBlockInHand() || Objects.requireNonNull(e.getClickedBlock()).getType() != Material.OAK_WALL_SIGN || !(e.hasItem()) || Objects.requireNonNull(e.getItem()).getType() != Material.PAPER || e.getAction() != Action.LEFT_CLICK_BLOCK){
            return;
        }
        Sign sign = (Sign) e.getClickedBlock().getState();
        if(sign.getLine(0).equals("[gate]")){
            Equal equal = new Equal();
            if (breaksign.breakBlockList.check(e.getPlayer().getUniqueId(), equal) != null) {
                e.getPlayer().sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.brokeblock", "You have broke a block.")));
                return;
            }
            else {
                e.setCancelled(true);
            }
            Location gate;
            String tid = getTID(e.getItem());
            int face; // 1: ->; 2:<-; 3: error(closed);
            if(sign.getLine(2).equals("<-")){
                face = 2;
            }
            else if(sign.getLine(2).equals("->")){
                face = 1;
            }
            else{
                face = 3;
            }

            gate = getDoorPosition(e.getBlockFace(), sign.getLocation(), face);


            if(sign.getLine(3).equals("in")){
                //Get in the station
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        Connection connection;
                        ResultSet resultset;
                        PreparedStatement preparedstatement;
                        try {
                            connection = MySql.getSQLConnection();
                            preparedstatement = connection.prepareStatement("SELECT * FROM ticket WHERE tid = ?");
                            preparedstatement.setString(1, tid);
                            resultset = preparedstatement.executeQuery();
                            if(!resultset.next()){
                                e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred when charging, please send the following message to the admins: %s.")), "No such ticket."));
                            }
                            double money = resultset.getDouble("money");
                            if(money < 10){
                                e.getPlayer().sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.noenoughmoney", "You do not have enough money in your card.")));
                                return;
                            }
                            preparedstatement.close();
                            //deals= 0:in, 1:out, 3:charge
                            preparedstatement = connection.prepareStatement("INSERT INTO deals (time,tid,station,action, money) VALUES (?, ?, ?, 0, 0);");
                            preparedstatement.setLong(1, System.currentTimeMillis());
                            preparedstatement.setString(2, tid);
                            preparedstatement.setString(3, sign.getLine(1));
                            preparedstatement.execute();
                            preparedstatement.close();
                            connection.close();

                            new BukkitRunnable(){
                                @Override
                                public void run() {
                                    OpenDoorEvent event = new OpenDoorEvent();
                                    event.setDelay(2000);
                                    event.setLocation(gate);
                                    Bukkit.getPluginManager().callEvent(event);
                                }
                            }.runTask(HelloMinecraft.instance);

                            e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.passgate", ", monet left: %s")),
                                    HelloMinecraft.config.getString("lang.getin", "Getting in"), sign.getLine(1), money));
                        } catch (ClassNotFoundException | SQLException classNotFoundException) {
                            classNotFoundException.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(HelloMinecraft.instance);
            }
            else if(sign.getLine(3).equals("out")){
                //Get out the station
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        Connection connection;
                        ResultSet resultset;
                        PreparedStatement preparedstatement;
                        try {
                            connection = MySql.getSQLConnection();
                            preparedstatement = connection.prepareStatement("SELECT * FROM ticket WHERE tid = ?");
                            preparedstatement.setString(1, tid);
                            resultset = preparedstatement.executeQuery();
                            if(!resultset.next()){
                                e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.chargeuke", "Unknown error has occurred when charging, please send the following message to the admins: %s.")), "No such ticket."));
                            }
                            double money = resultset.getDouble("money");
                            preparedstatement.close();

                            money -= HelloMinecraft.config.getDouble("ticket-price", 2);

                            if (money < 0){
                                e.getPlayer().sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.noenoughmoney", "You do not have enough money")));
                            }

                            preparedstatement = connection.prepareStatement("UPDATE ticket\n" +
                                    "SET money=?\n" +
                                    "WHERE tid=?");
                            preparedstatement.setDouble(1, money);
                            preparedstatement.setString(2, tid);
                            preparedstatement.execute();

                            //deals= 0:in, 1:out, 3:charge
                            preparedstatement = connection.prepareStatement("INSERT INTO deals (time,tid,station,action, money) VALUES (?, ?, ?, 1, 2);");
                            preparedstatement.setLong(1, System.currentTimeMillis());
                            preparedstatement.setString(2, tid);
                            preparedstatement.setString(3, sign.getLine(1));
                            preparedstatement.execute();
                            preparedstatement.close();
                            connection.close();

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    OpenDoorEvent event = new OpenDoorEvent();
                                    event.setDelay(2000);
                                    event.setLocation(gate);
                                    Bukkit.getPluginManager().callEvent(event);
                                }
                            }.runTask(HelloMinecraft.instance);

                            e.getPlayer().sendMessage(String.format(Objects.requireNonNull(HelloMinecraft.config.getString("lang.passgate", "%s at %s, monet left: %s")),
                                    HelloMinecraft.config.getString("lang.getout", "Getting out"), sign.getLine(1), money));
                        } catch (ClassNotFoundException | SQLException classNotFoundException) {
                            classNotFoundException.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(HelloMinecraft.instance);
            }
            else{
                //The gate is closed
                e.getPlayer().sendMessage(Objects.requireNonNull(HelloMinecraft.config.getString("lang.gateclosed", "This gate is closed.")));
            }
        }
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