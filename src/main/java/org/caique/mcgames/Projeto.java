package org.caique.mcgames;

import org.bukkit.plugin.java.JavaPlugin;
import org.caique.mcgames.commands.LoginCommand;
import org.caique.mcgames.commands.RegisterCommand;
import org.caique.mcgames.database.DatabaseManager;
import org.caique.mcgames.events.PlayerListener;

public final class Projeto extends JavaPlugin {

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        save/DefaultConfig(); // Gera config.yml se não existir
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        // Registrar comandos
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));

        // Registrar eventos
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("Plugin iniciado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("Plugin desativado.");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}