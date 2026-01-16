package hs.elementPlugin;

import hs.elementPlugin.commands.*;
import hs.elementPlugin.core.Constants;
import hs.elementPlugin.data.DataStore;
import hs.elementPlugin.elements.ElementType;
import hs.elementPlugin.elements.abilities.AbilityManager;
import hs.elementPlugin.elements.abilities.impl.air.*;
import hs.elementPlugin.elements.abilities.impl.death.*;
import hs.elementPlugin.elements.abilities.impl.earth.*;
import hs.elementPlugin.elements.abilities.impl.fire.*;
import hs.elementPlugin.elements.abilities.impl.frost.*;
import hs.elementPlugin.elements.abilities.impl.life.*;
import hs.elementPlugin.elements.abilities.impl.metal.*;
import hs.elementPlugin.elements.abilities.impl.water.*;
import hs.elementPlugin.elements.impl.death.DeathElementCraftListener;
import hs.elementPlugin.elements.impl.earth.listeners.EarthCharmListener;
import hs.elementPlugin.elements.impl.earth.listeners.EarthFriendlyMobListener;
import hs.elementPlugin.elements.impl.earth.listeners.EarthOreDropListener;
import hs.elementPlugin.elements.impl.fire.listeners.FireCombatListener;
import hs.elementPlugin.elements.impl.fire.listeners.FireImmunityListener;
import hs.elementPlugin.elements.impl.fire.listeners.FireballProtectionListener;
import hs.elementPlugin.elements.impl.frost.listeners.FrostFrozenPunchListener;
import hs.elementPlugin.elements.impl.frost.listeners.FrostPassiveListener;
import hs.elementPlugin.elements.impl.life.LifeElementCraftListener;
import hs.elementPlugin.elements.impl.metal.listeners.MetalArrowImmunityListener;
import hs.elementPlugin.elements.impl.metal.listeners.MetalChainStunListener;
import hs.elementPlugin.listeners.AbilityListener;
import hs.elementPlugin.listeners.GUIListener;
import hs.elementPlugin.listeners.items.AdvancedRerollerListener;
import hs.elementPlugin.listeners.items.ElementItemCraftListener;
import hs.elementPlugin.listeners.items.ElementItemDeathListener;
import hs.elementPlugin.listeners.items.RerollerListener;
import hs.elementPlugin.listeners.items.UpgraderListener;
import hs.elementPlugin.listeners.items.listeners.*;
import hs.elementPlugin.listeners.player.*;
import hs.elementPlugin.managers.*;
import hs.elementPlugin.services.EffectService;
import hs.elementPlugin.services.ValidationService;
import hs.elementPlugin.util.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for ElementPlugin
 * Handles initialization, registration, and lifecycle management
 */
public final class ElementPlugin extends JavaPlugin {

    // Core Data & Configuration
    private DataStore dataStore;
    private ConfigManager configManager;

    // Managers
    private ElementManager elementManager;
    private ManaManager manaManager;
    private TrustManager trustManager;
    private ItemManager itemManager;
    private AbilityManager abilityManager;

    // Services (New)
    private EffectService effectService;
    private ValidationService validationService;
    private TaskScheduler taskScheduler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeManagers();
        initializeServices();
        registerAbilities();
        registerCommands();
        registerListeners();
        registerRecipes();
        manaManager.start();

        getLogger().info("ElementPlugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Stop mana regeneration
        if (manaManager != null) {
            manaManager.stop();
        }

        // Flush all data to disk
        if (dataStore != null) {
            dataStore.flushAll();
        }

        getLogger().info("ElementPlugin disabled successfully!");
    }

    /**
     * Initialize core managers
     */
    private void initializeManagers() {
        getLogger().info("Initializing managers...");

        this.configManager = new ConfigManager(this);
        this.dataStore = new DataStore(this);
        this.trustManager = new TrustManager(this);
        this.manaManager = new ManaManager(this, dataStore, configManager);
        this.abilityManager = new AbilityManager(this);
        this.elementManager = new ElementManager(this, dataStore, manaManager, trustManager, configManager);
        this.itemManager = new ItemManager(this, manaManager, configManager);

        getLogger().info("Managers initialized!");
    }

    /**
     * Initialize new services (refactored architecture)
     */
    private void initializeServices() {
        getLogger().info("Initializing services...");

        this.effectService = new EffectService(this, elementManager);
        this.validationService = new ValidationService(trustManager);
        this.taskScheduler = new TaskScheduler(this);

        getLogger().info("Services initialized!");
    }

    /**
     * Register all element abilities
     */
    private void registerAbilities() {
        getLogger().info("Registering abilities...");

        // Air abilities
        abilityManager.registerAbility(ElementType.AIR, 1, new AirBlastAbility(this));
        abilityManager.registerAbility(ElementType.AIR, 2, new AirDashAbility(this));

        // Water abilities
        abilityManager.registerAbility(ElementType.WATER, 1, new WaterGeyserAbility(this));
        abilityManager.registerAbility(ElementType.WATER, 2, new WaterBeamAbility(this));

        // Fire abilities
        abilityManager.registerAbility(ElementType.FIRE, 1, new FireballAbility(this));
        abilityManager.registerAbility(ElementType.FIRE, 2, new MeteorShowerAbility(this));

        // Earth abilities
        abilityManager.registerAbility(ElementType.EARTH, 1, new EarthTunnelAbility(this));
        abilityManager.registerAbility(ElementType.EARTH, 2, new EarthCharmAbility(this));

        // Life abilities
        abilityManager.registerAbility(ElementType.LIFE, 1, new LifeRegenAbility(this));
        abilityManager.registerAbility(ElementType.LIFE, 2, new LifeHealingBeamAbility(this));

        // Death abilities
        abilityManager.registerAbility(ElementType.DEATH, 1, new DeathSummonUndeadAbility(this));
        abilityManager.registerAbility(ElementType.DEATH, 2, new DeathWitherSkullAbility(this));

        // Metal abilities
        abilityManager.registerAbility(ElementType.METAL, 1, new MetalChainAbility(this));
        abilityManager.registerAbility(ElementType.METAL, 2, new MetalDashAbility(this));

        // Frost abilities
        abilityManager.registerAbility(ElementType.FROST, 1, new FrostCircleAbility(this));
        abilityManager.registerAbility(ElementType.FROST, 2, new FrostPunchAbility(this));

        getLogger().info("Registered all element abilities!");
    }

    /**
     * Register all commands
     */
    private void registerCommands() {
        getLogger().info("Registering commands...");

        // Element info command
        ElementInfoCommand elementInfoCmd = new ElementInfoCommand(this);
        getCommand("elements").setExecutor(elementInfoCmd);
        getCommand("elements").setTabCompleter(elementInfoCmd);

        // Other commands
        getCommand("trust").setExecutor(new TrustCommand(this, trustManager));
        getCommand("element").setExecutor(new ElementCommand(this));
        getCommand("mana").setExecutor(new ManaCommand(manaManager, configManager));
        getCommand("util").setExecutor(new UtilCommand(this));

        getLogger().info("Commands registered successfully!");
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        getLogger().info("Registering listeners...");
        PluginManager pm = Bukkit.getPluginManager();

        // ========================================
        // CORE PLAYER LISTENERS (Refactored)
        // ========================================
        pm.registerEvents(new PlayerLifecycleListener(this, elementManager, manaManager, effectService), this);
        pm.registerEvents(effectService, this); // EffectService has @EventHandler methods
        pm.registerEvents(new GameModeListener(manaManager, configManager), this);

        // ========================================
        // COMBAT & TRUST LISTENERS
        // ========================================
        pm.registerEvents(new CombatListener(trustManager, elementManager), this);

        // ========================================
        // ABILITY LISTENERS
        // ========================================
        pm.registerEvents(new AbilityListener(this, elementManager), this);

        // ========================================
        // ITEM LISTENERS
        // ========================================
        // Element items
        pm.registerEvents(new ElementItemUseListener(this, elementManager, itemManager), this);
        pm.registerEvents(new ElementItemCraftListener(this, elementManager), this);
        pm.registerEvents(new ElementItemDeathListener(this, elementManager), this);
        pm.registerEvents(new ElementItemDropListener(this), this);
        pm.registerEvents(new ElementItemPickupListener(this, elementManager), this);
        pm.registerEvents(new ElementInventoryProtectionListener(this, elementManager), this);
        pm.registerEvents(new ElementCombatProjectileListener(itemManager), this);

        // Utility items
        pm.registerEvents(new RerollerListener(this), this);
        pm.registerEvents(new AdvancedRerollerListener(this), this);
        pm.registerEvents(new UpgraderListener(this, elementManager), this);

        // ========================================
        // GUI LISTENERS
        // ========================================
        pm.registerEvents(new GUIListener(this), this);

        // ========================================
        // AIR ELEMENT LISTENERS
        // ========================================
        pm.registerEvents(new hs.elementPlugin.elements.impl.air.listeners.FallDamageListener(elementManager), this);
        pm.registerEvents(new hs.elementPlugin.elements.impl.air.listeners.AirCombatListener(elementManager), this);

        // ========================================
        // WATER ELEMENT LISTENERS
        // ========================================
        pm.registerEvents(new hs.elementPlugin.elements.impl.water.listeners.WaterDrowningImmunityListener(elementManager), this);

        // ========================================
        // FIRE ELEMENT LISTENERS
        // ========================================
        pm.registerEvents(new FireImmunityListener(elementManager), this);
        pm.registerEvents(new FireCombatListener(elementManager, trustManager), this);
        pm.registerEvents(new FireballProtectionListener(), this);

        // ========================================
        // EARTH ELEMENT LISTENERS
        // ========================================
        pm.registerEvents(new EarthCharmListener(elementManager, this), this);
        pm.registerEvents(new EarthFriendlyMobListener(this, trustManager), this);
        pm.registerEvents(new EarthOreDropListener(elementManager), this);

        // ========================================
        // LIFE ELEMENT LISTENERS
        // ========================================
        pm.registerEvents(new hs.elementPlugin.elements.impl.life.listeners.LifeRegenListener(elementManager), this);
        pm.registerEvents(new LifeElementCraftListener(this, elementManager), this);

        // ========================================
        // DEATH ELEMENT LISTENERS
        // ========================================
        pm.registerEvents(new hs.elementPlugin.elements.impl.death.listeners.DeathRawFoodListener(elementManager), this);
        pm.registerEvents(new hs.elementPlugin.elements.impl.death.listeners.DeathFriendlyMobListener(this, trustManager), this);
        pm.registerEvents(new DeathElementCraftListener(this, elementManager), this);

        // ========================================
        // METAL ELEMENT LISTENERS
        // ========================================
        pm.registerEvents(new MetalArrowImmunityListener(elementManager), this);
        pm.registerEvents(new MetalChainStunListener(), this);

        // ========================================
        // FROST ELEMENT LISTENERS
        // ========================================
        pm.registerEvents(new FrostPassiveListener(this, elementManager), this);
        pm.registerEvents(new FrostFrozenPunchListener(this, elementManager), this);

        getLogger().info("Listeners registered successfully!");
    }

    /**
     * Register recipes
     */
    private void registerRecipes() {
        // Delay recipe registration by 1 second to ensure server is fully loaded
        taskScheduler.runLaterSeconds(() -> {
            getLogger().info("Registering recipes...");
            // Recipe registration happens in individual item classes
            getLogger().info("Recipes registered!");
        }, 1);
    }

    // ========================================
    // GETTERS
    // ========================================

    /**
     * Get the data store
     */
    public DataStore getDataStore() {
        return dataStore;
    }

    /**
     * Get the config manager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Get the element manager
     */
    public ElementManager getElementManager() {
        return elementManager;
    }

    /**
     * Get the mana manager
     */
    public ManaManager getManaManager() {
        return manaManager;
    }

    /**
     * Get the trust manager
     */
    public TrustManager getTrustManager() {
        return trustManager;
    }

    /**
     * Get the item manager
     */
    public ItemManager getItemManager() {
        return itemManager;
    }

    /**
     * Get the ability manager
     */
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    /**
     * Get the effect service (NEW)
     */
    public EffectService getEffectService() {
        return effectService;
    }

    /**
     * Get the validation service (NEW)
     */
    public ValidationService getValidationService() {
        return validationService;
    }

    /**
     * Get the task scheduler utility (NEW)
     */
    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }
}