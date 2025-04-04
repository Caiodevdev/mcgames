package org.caique.mcgames.events;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.caique.mcgames.Projeto;
import org.caique.mcgames.database.PlayerData;
import org.caique.mcgames.utils.MessageUtils;

import java.sql.SQLException;

public class PlayerListener implements Listener {
    private final Projeto plugin;
    private final PlayerData playerData;

    public PlayerListener(Projeto plugin) {
        this.plugin = plugin;
        this.playerData = new PlayerData(plugin.getDatabaseManager());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            if (!playerData.isRegistered(player)) {
                MessageUtils.sendTitle(player, "&eBem-vindo!", "&fRegistre-se com /register", 10, 9999, 10);
                MessageUtils.sendMessage(player, "&eUse /register <senha> <confirmação> para começar!");
            } else if (!playerData.isLoggedIn(player)) {
                MessageUtils.sendTitle(player, "&eBem-vindo de volta!", "&fLogue com /login", 10, 9999, 10);
                MessageUtils.sendMessage(player, "&eUse /login <senha> para continuar!");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao verificar jogador: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            Player player = event.getPlayer();
            if (playerData.isLoggedIn(player)) {
                playerData.setLoggedIn(player, false);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao desconectar jogador: " + e.getMessage());
        }
    }

    private boolean blockAction(Player player) {
        try {
            if (!playerData.isLoggedIn(player)) {
                MessageUtils.sendTitle(player, "&cAtenção!", "&fVocê precisa logar primeiro!", 10, 40, 10);
                MessageUtils.sendMessage(player, "&cUse /login <senha> ou /register <senha> <confirmação>");
                MessageUtils.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }
            return false;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao verificar login: " + e.getMessage());
            return true;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (blockAction(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (blockAction(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (blockAction(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (blockAction(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (blockAction((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        try {
            if (!playerData.isLoggedIn(event.getPlayer()) &&
                    (event.getFrom().getX() != event.getTo().getX() ||
                            event.getFrom().getZ() != event.getTo().getZ())) {
                event.setTo(event.getFrom());
                MessageUtils.sendTitle(event.getPlayer(), "&cAtenção!", "&fVocê precisa logar primeiro!", 10, 40, 10);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao verificar movimento: " + e.getMessage());
        }
    }
}