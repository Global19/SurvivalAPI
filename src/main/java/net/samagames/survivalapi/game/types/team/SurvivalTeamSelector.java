package net.samagames.survivalapi.game.types.team;

import net.samagames.api.games.Status;
import net.samagames.api.gui.AbstractGui;
import net.samagames.survivalapi.game.SurvivalTeam;
import net.samagames.survivalapi.game.types.SurvivalTeamGame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class SurvivalTeamSelector implements Listener
{
    private static SurvivalTeamSelector instance;
    private final SurvivalTeamGame game;
    private HashMap<UUID, AbstractGui> playersGui;

    public SurvivalTeamSelector(SurvivalTeamGame game) throws IllegalAccessException
    {
        if (instance != null)
        {
            throw new IllegalAccessException("Instance already defined!");
        }

        instance = this;

        this.game = game;
        this.playersGui = new HashMap<>();
    }

    public static SurvivalTeamSelector getInstance()
    {
        return instance;
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (this.game.getStatus().equals(Status.IN_GAME))
            event.getHandlers().unregister(this);
        else if (event.getItem() != null && event.getItem().getType() == Material.NETHER_STAR)
            this.openGui(event.getPlayer(), new GuiSelectTeam());
    }


    @EventHandler
    public void clickEvent(InventoryClickEvent event)
    {
        if (this.game.getStatus().equals(Status.IN_GAME))
        {
            event.getHandlers().unregister(this);
        }
        else if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getView().getType() != InventoryType.PLAYER)
        {
            AbstractGui gui = this.playersGui.get(event.getWhoClicked().getUniqueId());

            if (gui != null)
            {
                String action = gui.getAction(event.getSlot());

                if (action != null)
                    gui.onClick((Player) event.getWhoClicked(), event.getCurrentItem(), action, event.getClick());

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if (this.game.getStatus().equals(Status.IN_GAME))
        {
            event.getHandlers().unregister(this);
            return;
        }

        if (!this.game.getStatus().equals(Status.IN_GAME))
        {
            event.getBlock().setType(Material.AIR);

            SurvivalTeam team = this.game.getPlayerTeam(event.getPlayer().getUniqueId());
            String name = event.getLine(0);
            name = name.trim();

            if (!name.isEmpty())
            {
                team.setTeamName(name);
                event.getPlayer().sendMessage(this.game.getCoherenceMachine().getGameTag() + " " + ChatColor.GREEN + "Le nom de votre équipe est désormais : " + team.getChatColor() + team.getTeamName());
                this.openGui(event.getPlayer(), new GuiSelectTeam());
            }
            else
            {
                event.getPlayer().sendMessage(this.game.getCoherenceMachine().getGameTag() + " " + ChatColor.RED + "Le nom de l'équipe ne peut être vide.");
                this.openGui(event.getPlayer(), new GuiSelectTeam());
            }
        }
    }

    public void openGui(Player player, AbstractGui gui)
    {
        if (this.playersGui.containsKey(player.getUniqueId()))
        {
            player.closeInventory();
            this.playersGui.remove(player.getUniqueId());
        }

        this.playersGui.put(player.getUniqueId(), gui);
        gui.display(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event)
    {
        if (!this.game.getStatus().equals(Status.IN_GAME))
            return;

        if (event.getMessage().startsWith("!"))
        {
            event.setCancelled(true);
            String message = event.getMessage().substring(1);
            SurvivalTeam team = this.game.getPlayerTeam(event.getPlayer().getUniqueId());

            if (team != null)
            {
                event.setCancelled(true);

                for (Player player : this.game.getPlugin().getServer().getOnlinePlayers())
                    player.sendMessage(team.getChatColor() + "[" + team.getTeamName() + "] " + event.getPlayer().getName() + " : " + ChatColor.WHITE + message);
            }
        }
        else
        {
            SurvivalTeam team = this.game.getPlayerTeam(event.getPlayer().getUniqueId());

            if (team != null)
            {
                event.setCancelled(true);
                String message = team.getChatColor() + "(Equipe) " + event.getPlayer().getName() + " : " + ChatColor.GOLD + ChatColor.ITALIC + event.getMessage();

                for (UUID id : team.getPlayersUUID())
                {
                    Player player = this.game.getPlugin().getServer().getPlayer(id);

                    if (player != null)
                        player.sendMessage(message);
                }
            }
        }
    }
}