package net.maed12.rideablemobs.util;


import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.WaterMob;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Util {

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
}
