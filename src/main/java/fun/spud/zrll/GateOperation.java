package fun.spud.zrll;

import fun.spud.zrll.events.CloseDoorEvent;
import fun.spud.zrll.events.OpenDoorEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Gate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Timer;
import java.util.TimerTask;

public class GateOperation implements Listener {
    @EventHandler
    public void openGate(OpenDoorEvent e){
        Gate blockgate;
        blockgate = (Gate) e.getLocation().getBlock().getBlockData();
        blockgate.setOpen(true);
        e.getLocation().getBlock().setBlockData(blockgate);

        Timer timer = new Timer();
        CloseGate timertask = new CloseGate(timer);

        timertask.setLocation(e.getLocation());
        timer.schedule(timertask, e.getDelay());
    }

    @EventHandler
    public void closeGate(CloseDoorEvent e){
        Location location = e.getLocation();
        Gate gate = (Gate) location.getBlock().getBlockData();
        gate.setOpen(false);
        location.getBlock().setBlockData(gate);
    }
}

class CloseGate extends TimerTask {
    Timer timer;
    Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public CloseGate(Timer timer){
        this.timer = timer;
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                CloseDoorEvent event = new CloseDoorEvent();
                event.setLocation(location);
                Bukkit.getPluginManager().callEvent(event);
            }
        }.runTask(HelloMinecraft.instance);
        timer.cancel();
    }
}