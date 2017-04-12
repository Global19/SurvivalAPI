package net.samagames.survivalapi.modules.block;

import com.google.gson.JsonElement;
import net.samagames.survivalapi.SurvivalAPI;
import net.samagames.survivalapi.SurvivalPlugin;
import net.samagames.survivalapi.modules.AbstractSurvivalModule;
import net.samagames.survivalapi.modules.IConfigurationBuilder;
import net.samagames.survivalapi.modules.utility.DropTaggingModule;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TorchThanCoalModule class
 *
 * Copyright (c) for SamaGames
 * All right reserved
 */
public class TorchThanCoalModule extends AbstractSurvivalModule
{
    /**
     * Constructor
     *
     * @param plugin Parent plugin
     * @param api API instance
     * @param moduleConfiguration Module configuration
     */
    public TorchThanCoalModule(SurvivalPlugin plugin, SurvivalAPI api, Map<String, Object> moduleConfiguration)
    {
        super(plugin, api, moduleConfiguration);
        Validate.notNull(moduleConfiguration, "Configuration cannot be null!");
    }

    /**
     * Replace coal by torch
     *
     * @param event Event
     */
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event)
    {
        if (event.getEntityType() != EntityType.DROPPED_ITEM)
            return;

        if (event.getEntity().hasMetadata("playerDrop"))
            return;

        if (event.getEntity().getItemStack().getType() == Material.COAL)
            event.getEntity().setItemStack(new ItemStack(Material.TORCH, (int) this.moduleConfiguration.get("torch")));
    }

    @Override
    public List<Class<? extends AbstractSurvivalModule>> getRequiredModules()
    {
        List<Class<? extends AbstractSurvivalModule>> requiredModules = new ArrayList<>();

        requiredModules.add(DropTaggingModule.class);

        return requiredModules;
    }

    public static class ConfigurationBuilder implements IConfigurationBuilder
    {
        private int torch;

        public ConfigurationBuilder()
        {
            this.torch = 3;
        }

        @Override
        public Map<String, Object> build()
        {
            Map<String, Object> moduleConfiguration = new HashMap<>();

            moduleConfiguration.put("torch", this.torch);

            return moduleConfiguration;
        }

        @Override
        public Map<String, Object> buildFromJson(Map<String, JsonElement> configuration) throws Exception
        {
            if (configuration.containsKey("torch"))
                this.setTorchAmount(configuration.get("torch").getAsInt());

            return this.build();
        }

        public ConfigurationBuilder setTorchAmount(int torch)
        {
            this.torch = torch;
            return this;
        }
    }
}
