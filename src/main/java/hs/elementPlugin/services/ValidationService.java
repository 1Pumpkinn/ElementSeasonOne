package hs.elementPlugin.services;

import hs.elementPlugin.ElementPlugin;
import hs.elementPlugin.data.PlayerData;
import hs.elementPlugin.elements.ElementType;
import hs.elementPlugin.managers.TrustManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Centralized validation logic reused across abilities and listeners
 */
public class ValidationService {
    private final TrustManager trustManager;

    public ValidationService(TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    /**
     * Check if target is valid for abilities (not self, not trusted)
     */
    public boolean isValidTarget(Player attacker, LivingEntity target) {
        if (target.equals(attacker)) return false;

        if (target instanceof Player targetPlayer) {
            return !trustManager.isTrusted(attacker.getUniqueId(), targetPlayer.getUniqueId());
        }

        return true;
    }

    /**
     * Check if player has required upgrade level
     */
    public boolean hasUpgradeLevel(PlayerData pd, int required) {
        return pd.getCurrentElementUpgradeLevel() >= required;
    }

    /**
     * Check if player can use element-specific items
     */
    public boolean canUseElementItem(Player player, ElementType itemElement, PlayerData pd) {
        return pd.getCurrentElement() == itemElement;
    }
}