package net.samagames.survivalapi.modules.gameplay;

import com.google.gson.JsonElement;
import net.samagames.survivalapi.SurvivalAPI;
import net.samagames.survivalapi.SurvivalPlugin;
import net.samagames.survivalapi.modules.AbstractSurvivalModule;
import net.samagames.survivalapi.modules.IConfigurationBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * CrippleModule class
 *
 * Copyright (c) for SamaGames
 * All right reserved
 */
public class CrippleModule extends AbstractSurvivalModule
{
    /**
     * Constructor
     *
     * @param plugin Parent plugin
     * @param api API instance
     * @param moduleConfiguration Module configuration
     */
    public CrippleModule(SurvivalPlugin plugin, SurvivalAPI api, Map<String, Object> moduleConfiguration)
    {
        super(plugin, api, moduleConfiguration);
        Validate.notNull(moduleConfiguration, "Configuration cannot be null!");
    }

    /**
     * Set Potion Effect SLOWNESS I when player takes fall damage
     * @param event Entity Damage Event
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL && !event.isCancelled())
            ((Player) event.getEntity()).addPotionEffect(PotionEffectType.SLOW.createEffect((int) this.moduleConfiguration.get("penalty-time") * 20, 1));
    }

    public static class ConfigurationBuilder implements IConfigurationBuilder
    {
        private int penaltyTime;

        public ConfigurationBuilder()
        {
            this.penaltyTime = 30;
        }

        @Override
        public Map<String, Object> build()
        {
            Map<String, Object> moduleConfiguration = new HashMap<>();

            moduleConfiguration.put("penalty-time", this.penaltyTime);

            return moduleConfiguration;
        }

        @Override
        public Map<String, Object> buildFromJson(Map<String, JsonElement> configuration) throws Exception
        {
            if (configuration.containsKey("penalty-time"))
                this.setPenaltyTime(configuration.get("penalty-time").getAsInt());

            return this.build();
        }

        public CrippleModule.ConfigurationBuilder setPenaltyTime(int penaltyTime)
        {
            this.penaltyTime = penaltyTime;
            return this;
        }
    }
}
