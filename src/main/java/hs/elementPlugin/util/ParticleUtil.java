package hs.elementPlugin.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/**
 * Utility for common particle patterns
 */
public class ParticleUtil {

    /**
     * Create a circle of particles at ground level
     */
    public static void spawnCircle(Location center, double radius, Particle particle, int points) {
        World world = center.getWorld();
        if (world == null) return;

        for (int i = 0; i < 360; i += 360 / points) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * radius;
            double z = Math.sin(rad) * radius;

            Location particleLoc = center.clone().add(x, 0.5, z);
            ensureAboveGround(particleLoc);

            world.spawnParticle(particle, particleLoc, 1, 0.1, 0.1, 0.1, 0, null, true);
        }
    }

    /**
     * Spawn expanding ring animation
     */
    public static void spawnExpandingRing(Location center, double startRadius,
                                          double endRadius, int steps, Particle particle) {
        World world = center.getWorld();
        if (world == null) return;

        double radiusIncrement = (endRadius - startRadius) / steps;

        for (int step = 0; step < steps; step++) {
            double currentRadius = startRadius + (step * radiusIncrement);
            spawnCircle(center, currentRadius, particle, 36);
        }
    }

    /**
     * Ensure particle location is above ground
     */
    private static void ensureAboveGround(Location loc) {
        while (loc.getBlock().getType().isSolid() && loc.getY() < loc.getY() + 3) {
            loc.add(0, 1, 0);
        }
    }

    /**
     * Spawn particle line between two points
     */
    public static void spawnLine(Location start, Location end, Particle particle, double spacing) {
        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) return;

        double distance = start.distance(end);
        int points = (int) (distance / spacing);

        for (int i = 0; i <= points; i++) {
            double t = i / (double) points;
            Location point = start.clone().add(
                    end.toVector().subtract(start.toVector()).multiply(t)
            );
            world.spawnParticle(particle, point, 1, 0.05, 0.05, 0.05, 0, null, true);
        }
    }
}