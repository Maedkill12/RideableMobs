package net.maed12.rideablemobs.listener;

import net.maed12.rideablemobs.util.Util;
import net.minecraft.world.food.FoodMetaData;
import org.bukkit.*;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Objects;

public class PlayerListener implements Listener {
    private final JavaPlugin plugin;

    public PlayerListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (!e.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        Entity entity = e.getRightClicked();
        if (entity.getPassengers().size() >= 1 || entity instanceof EnderDragon || (entity instanceof Vehicle && !(entity instanceof ZombieHorse || entity instanceof SkeletonHorse))) {
            return;
        }
        EntityType type = entity.getType();
        boolean isEnabled = plugin.getConfig().getBoolean(type.name().toLowerCase());
        Player player = e.getPlayer();
        if (!isEnabled) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("entity-disabled"))));
            return;
        }
        if (!player.hasPermission("rideablemobs.ride." + entity.getType().name().toLowerCase())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no-permission-ride"))));
            return;
        }
        boolean shouldBeEmptyHand = plugin.getConfig().getBoolean("requires-empty-hand");
        if (shouldBeEmptyHand && !player.getInventory().getItemInMainHand().getType().isAir()) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            Material material = stack.getType();
            if (material.isEdible()) {
                LivingEntity livingEntity = (LivingEntity) entity;
                AttributeInstance ai = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                switch (material) {
                    case ENCHANTED_GOLDEN_APPLE -> {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));
                        assert ai != null;
                        livingEntity.setHealth(ai.getValue());
                        player.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), 5);
                        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                            stack.setAmount(stack.getAmount() - 1);
                        }
                    }
                    case GOLDEN_APPLE -> {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
                        assert ai != null;
                        livingEntity.setHealth(Math.min(livingEntity.getHealth() + 10D, ai.getValue()));
                        player.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), 3);
                        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                            stack.setAmount(stack.getAmount() - 1);
                        }
                    }
                    case APPLE -> {
                        assert ai != null;
                        livingEntity.setHealth(Math.min(livingEntity.getHealth() + 3D, ai.getValue()));
                        player.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), 1);
                        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                            stack.setAmount(stack.getAmount() - 1);
                        }
                    }
                }
            }
            return;
        }
        if (entity instanceof ArmorStand && !player.isSneaking()) {
            entity.addPassenger(player);
        } else if (!(entity instanceof ArmorStand)) {
            entity.addPassenger(player);
        }
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
