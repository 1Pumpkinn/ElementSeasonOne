package hs.elementPlugin.util;

import hs.elementPlugin.ElementPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

/**
 * Utility for common metadata operations
 */
public class MetadataUtil {
    private final ElementPlugin plugin;

    public MetadataUtil(ElementPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Set metadata with expiration time
     */
    public void setTimed(Entity entity, String key, long durationMillis) {
        long expireAt = System.currentTimeMillis() + durationMillis;
        entity.setMetadata(key, new FixedMetadataValue(plugin, expireAt));
    }

    /**
     * Check if timed metadata is still valid
     */
    public boolean isValid(Entity entity, String key) {
        if (!entity.hasMetadata(key)) return false;

        long expireAt = entity.getMetadata(key).get(0).asLong();
        return System.currentTimeMillis() < expireAt;
    }

    /**
     * Set owner UUID metadata
     */
    public void setOwner(Entity entity, String key, UUID owner) {
        entity.setMetadata(key, new FixedMetadataValue(plugin, owner.toString()));
    }

    /**
     * Get owner UUID from metadata
     */
    public UUID getOwner(Entity entity, String key) {
        if (!entity.hasMetadata(key)) return null;

        try {
            String uuidStr = entity.getMetadata(key).get(0).asString();
            return UUID.fromString(uuidStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Remove metadata if expired
     */
    public void removeIfExpired(Entity entity, String key) {
        if (!isValid(entity, key)) {
            entity.removeMetadata(key, plugin);
        }
    }
}