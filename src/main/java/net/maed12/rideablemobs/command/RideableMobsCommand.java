package net.maed12.rideablemobs.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class RideableMobsCommand implements CommandExecutor, TabCompleter {
    private JavaPlugin plugin;
    private HashMap<UUID, PermissionAttachment> permissions;

    public RideableMobsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        permissions = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("rideablemobs.commands")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Invalid usage. Use /rideablemobs <command>.");
            return false;
        }
        if ("permission".equals(args[0])) {
            if (args[1] == null) {
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                return false;
            }
            if (args[2] == null) {
                return false;
            }
            EntityType entityType = EntityType.valueOf(args[2].toUpperCase());
            if (!entityType.isAlive()) {
                return false;
            }
            if (args[3] == null) {
                return false;
            }
            boolean hasPermission = Boolean.parseBoolean(args[3]);
            PermissionAttachment attachment;
            if (!permissions.containsKey(player.getUniqueId())) {
                attachment = player.addAttachment(plugin);
                permissions.put(player.getUniqueId(), attachment);
            } else {
                attachment = permissions.get(player.getUniqueId());
            }
            if (hasPermission) {
                attachment.setPermission("rideablemobs.ride." + entityType.name().toLowerCase(), true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(plugin.getConfig().getString("granted-permission")).replaceAll("%entity_type%", entityType.name()))));
            } else {
                attachment.unsetPermission("rideablemobs.ride." + entityType.name().toLowerCase());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(plugin.getConfig().getString("revoked-permission")).replaceAll("%entity_type%", entityType.name()))));
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid command.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.add("permission");
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                results.add(player.getName());
            }
        } else if (args.length == 3) {
            for (EntityType entityType : EntityType.values()) {
                if (entityType.isAlive()) {
                    results.add(entityType.name().toLowerCase());
                }
            }
        } else if (args.length == 4) {
            results.add("true");
            results.add("false");
        }
        return results;
    }
}
