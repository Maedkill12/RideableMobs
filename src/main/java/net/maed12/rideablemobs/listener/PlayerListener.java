package net.maed12.rideablemobs.listener;

import net.maed12.rideablemobs.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Objects;

public class PlayerListener implements Listener {
    private final JavaPlugin plugin;

    public PlayerListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
        if (!e.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        Entity entity = e.getRightClicked();
        if (entity instanceof Vehicle || entity.getPassengers().size() >= 1 || entity instanceof EnderDragon) {
            return;
        }
        Player player = e.getPlayer();
        if (!player.hasPermission("rideablemobs.ride." + entity.getName().toLowerCase())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no-permission-ride"))));
            return;
        }
        entity.addPassenger(player);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) e.getEntity();
        Entity vehicle = e.getDismounted();
        if (Util.canSwim(vehicle) && vehicle.isInWater() && !player.isSneaking()) {
            e.setCancelled(true);
        }
    }
}
