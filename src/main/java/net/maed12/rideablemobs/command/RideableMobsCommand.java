package net.maed12.rideablemobs.command;

import net.maed12.rideablemobs.util.Util;
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
import org.bukkit.util.StringUtil;

import java.util.*;

public class RideableMobsCommand implements CommandExecutor, TabCompleter {
    private JavaPlugin plugin;
    private static HashMap<UUID, PermissionAttachment> permissions;
    private static List<String> availableCommands;

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
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Invalid usage. Use /rideablemobs <command>.");
            return false;
        }
        if ("permission".equals(args[0])) {
            if (args.length < 4) {
                sender.sendMessage(ChatColor.RED + "Invalid usage. Use /rideablemobs permission <player> <entity_type> <true/false>.");
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return false;
            }
            EntityType entityType;
            try {
                entityType = EntityType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid entity type.");
                return false;
            }
            boolean value;
            try {
                value = Boolean.parseBoolean(args[3]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid value. Use true or false.");
                return false;
            }
            PermissionAttachment attachment = permissions.computeIfAbsent(player.getUniqueId(), playerUUID -> player.addAttachment(plugin));
            attachment.setPermission("rideablemobs.ride." + entityType.name().toLowerCase(), value);
            sender.sendMessage(ChatColor.GREEN + "Permission " + (value ? "set" : "removed") + " successfully.");

            Optional<String> message = Optional.of(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString(value ? "granted-permission" : "revoked-permission"))));
            message.map(m -> m.replaceAll("%entity_type%", entityType.name())).ifPresent(player::sendMessage);
            return true;
        } else if ("reload".equals(args[0])) {
            Util.onReload(sender);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Invalid command.");
        return false;
    }
  

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return switch (args.length) {
            case 1 -> StringUtil.copyPartialMatches(args[0], availableCommands, new ArrayList<>());
            case 2 -> StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), new ArrayList<>());
            case 3 -> StringUtil.copyPartialMatches(args[2], Arrays.stream(EntityType.values()).filter(EntityType::isAlive).map(entityType -> entityType.name().toLowerCase()).toList(), new ArrayList<>());
            case 4 -> StringUtil.copyPartialMatches(args[3], List.of("true", "false"), new ArrayList<>());
            default -> null;
        };
    }

    static {
        availableCommands = new ArrayList<>() {
            {
                this.add("permission");
                this.add("reload");
            }
        };
    }
}
