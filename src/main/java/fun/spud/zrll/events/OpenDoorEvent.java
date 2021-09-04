package fun.spud.zrll.events;

import fun.spud.zrll.HelloMinecraft;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;
import org.jetbrains.annotations.NotNull;

public class OpenDoorEvent extends ServerEvent {
    private static final HandlerList handlers = new HandlerList();
    private Location location;
    private int delay = HelloMinecraft.config.getInt("close-delay", 2000);

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
