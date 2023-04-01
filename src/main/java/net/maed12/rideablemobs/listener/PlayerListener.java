package net.maed12.rideablemobs.listener;

import net.maed12.rideablemobs.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PlayerListener implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<Material, Collection<PotionEffect>> foodEffects = new HashMap<>();

    public PlayerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        foodEffects.put(Material.ENCHANTED_GOLDEN_APPLE, List.of(
            new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3),
            new PotionEffect(PotionEffectType.REGENERATION, 600, 1),
            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0),
            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0)
        ));
        foodEffects.put(Material.GOLDEN_APPLE, List.of(
            new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0),
            new PotionEffect(PotionEffectType.REGENERATION, 100, 1)
        ));
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        EquipmentSlot hand = e.getHand();
        if (hand != EquipmentSlot.HAND) {
            return;
        }
        Entity entity = e.getRightClicked();
        if (entity.getPassengers().size() >= 1 || (entity instanceof Vehicle && !(entity instanceof ZombieHorse || entity instanceof SkeletonHorse))) {
            return;
        }
        Player player = e.getPlayer();
        if (!Util.isWorldEnabled(player)) {
            return;
        }
        EntityType type = entity.getType();
        String entityType = entity instanceof ComplexEntityPart || entity instanceof ComplexLivingEntity ? "ender_dragon" : type.name().toLowerCase();
        boolean isEnabled = entity instanceof ComplexEntityPart || entity instanceof ComplexLivingEntity ? plugin.getConfig().getBoolean("ender_dragon") : plugin.getConfig().getBoolean(entityType);
        if (!isEnabled) {
            String message = plugin.getConfig().getString("entity-disabled");
            assert message != null;
            if (!message.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
            return;
        }
        if (!player.hasPermission("rideablemobs.ride." + entityType)) {
            String message = plugin.getConfig().getString("no-permission-ride");
            assert message != null;
            if (!message.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
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
                        livingEntity.addPotionEffects(foodEffects.get(Material.ENCHANTED_GOLDEN_APPLE));
                        assert ai != null;
                        livingEntity.setHealth(ai.getValue());
                        player.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), 5);
                        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                            stack.setAmount(stack.getAmount() - 1);
                        }
                    }
                    case GOLDEN_APPLE -> {
                        livingEntity.addPotionEffects(foodEffects.get(Material.GOLDEN_APPLE));
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
        } else if (entity instanceof ComplexEntityPart entityPart) {
            ComplexLivingEntity parent = entityPart.getParent();
            EnderDragon enderDragon = (EnderDragon) parent;
            enderDragon.setPhase(EnderDragon.Phase.FLY_TO_PORTAL);
            enderDragon.addPassenger(player);
        } else if (!(entity instanceof ArmorStand)) {
            entity.addPassenger(player);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        Entity vehicle = e.getDismounted();
        if (vehicle instanceof EnderDragon enderDragon) {
            enderDragon.setPhase(EnderDragon.Phase.HOVER);;
        }
        if (Util.canSwim(vehicle) && vehicle.isInWater() && !player.isSneaking()) {
            e.setCancelled(true);
        }
    }
}
