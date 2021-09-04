package fun.spud.zrll.events;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;
import org.jetbrains.annotations.NotNull;

public class CloseDoorEvent extends ServerEvent {
    private static final HandlerList handlers = new HandlerList();
    private Location location;

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
