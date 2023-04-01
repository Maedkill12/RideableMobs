package net.maed12.rideablemobs.util;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Player;
import org.bukkit.entity.WaterMob;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {
    private static Plugin plugin;
    public static final Set<World> ENABLED_WORLDS = new HashSet<>();


    public static void onEnable(Plugin plugin) {
        Util.plugin = plugin;
        getWorlds();
    }

    public static void onReload(CommandSender sender) {
        plugin.reloadConfig();
        ENABLED_WORLDS.clear();
        getWorlds();
        sender.sendMessage(ChatColor.GREEN + "RideableMobs reload complete.");
    }

    public static boolean canSwim(Entity entity) {
        return switch (entity.getType()) {
            case DROWNED, GUARDIAN, TURTLE, AXOLOTL -> true;
            default -> entity instanceof WaterMob;
        };
    }
    public static boolean canFly(Entity entity) {
        return switch (entity.getType()) {
            case ALLAY, BAT, BEE, BLAZE, ENDER_DRAGON, PARROT, VEX, WITHER -> true;
            default -> entity instanceof Flying;
        };
    }

    public static boolean isWorldEnabled(Player player) {
        return ENABLED_WORLDS.contains(player.getWorld());
    }

    private static void getWorlds() {
        List<String> worlds = plugin.getConfig().getStringList("worlds");
        worlds.forEach(world -> {
            World w = Bukkit.getWorld(world);
            ENABLED_WORLDS.add(w);
        });
    }
}
