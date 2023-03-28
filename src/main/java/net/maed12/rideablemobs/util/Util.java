package net.maed12.rideablemobs.util;

import org.bukkit.entity.*;

public class Util {
    public static boolean canSwim(Entity entity) {
        return entity instanceof WaterMob ||
                entity instanceof Drowned ||
                entity instanceof Guardian ||
                entity instanceof Turtle;
    }
    public static boolean canFly(Entity entity) {
        return entity instanceof Flying ||
                entity instanceof Allay ||
                entity instanceof Bat ||
                entity instanceof Bee ||
                entity instanceof Blaze ||
                entity instanceof EnderDragon ||
                entity instanceof Parrot ||
                entity instanceof Vex ||
                entity instanceof Wither;
    }
}
