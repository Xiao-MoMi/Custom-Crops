package net.momirealms.customcrops.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CrowTask extends BukkitRunnable {

    private int timer;
    private Player player;
    private Location location;
    private Vector vector_1;
    private Vector vector_2;
    private float yaw;

    public CrowTask(Player player, Vector vector_1, Vector vector_2, Location location, float yaw) {
        this.player = player;
        this.timer = 0;
        this.vector_1 = vector_1;
        this.vector_2 = vector_2;
        this.location = location;
        this.yaw = yaw;
    }

    @Override
    public void run() {
        while (this.timer < 40) {
            timer++;
            location.add(vector_1).subtract(0,0.2,0);


        }
    }
}
