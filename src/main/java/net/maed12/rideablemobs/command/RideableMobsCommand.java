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
import org.bukkit.util.StringUtil;

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
            EnumSet<EntityType> validEntityTypes = EnumSet.allOf(EntityType.class);
            if (!validEntityTypes.contains(EntityType.valueOf(args[2].toUpperCase()))) {
                sender.sendMessage(ChatColor.RED + "Invalid entity type.");
                return false;
            }
            EntityType entityType = EntityType.valueOf(args[2].toUpperCase());
            boolean value = Boolean.parseBoolean(args[3]);
            PermissionAttachment attachment = permissions.getOrDefault(player.getUniqueId(), player.addAttachment(plugin));
            attachment.setPermission("rideablemobs.ride." + args[2].toLowerCase(), value);
            permissions.put(player.getUniqueId(), attachment);
            sender.sendMessage(ChatColor.GREEN + "Permission " + (value ? "set" : "removed") + " successfully.");

            Optional<String> message = Optional.ofNullable(plugin.getConfig().getString(value ? "granted-permission" : "revoked-permission"));
            if (message.isPresent()) {
                message = message.map(m -> m.replaceAll("%entity_type%", entityType.name()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.get()));
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Invalid command.");
        return false;
    }
  

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return switch (args.length) {
            case 1 -> StringUtil.copyPartialMatches(args[0], List.of("permission"), new ArrayList<>());
            case 2 -> StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), new ArrayList<>());
            case 3 -> StringUtil.copyPartialMatches(args[2], Arrays.stream(EntityType.values()).filter(EntityType::isAlive).map(entityType -> entityType.name().toLowerCase()).toList(), new ArrayList<>());
            case 4 -> StringUtil.copyPartialMatches(args[3], List.of("true", "false"), new ArrayList<>());
            default -> null;
        };
    }
}
