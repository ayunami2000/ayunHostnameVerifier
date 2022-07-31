package me.ayunami2000.ayunHostnameVerifier;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
    private final List<String> hostnames = new ArrayList<>();
    private String kickMessage = "Invalid hostname!";
    private boolean includePort = true;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        List<String> configHostnames = this.getConfig().getStringList("hostnames");
        if (configHostnames == null) {
            this.hostnames.add("localhost");
        } else {
            for (String hostname : configHostnames) {
                this.hostnames.add(hostname.toLowerCase());
            }
        }
        this.kickMessage = this.getConfig().getString("message", this.kickMessage);
        this.includePort = this.getConfig().getBoolean("port", this.includePort);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Listener) this);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String hostname = event.getHostname();
        if (!includePort && hostname.contains(":")) {
            hostname = hostname.substring(0, hostname.lastIndexOf(':'));
        }
        if (!this.hostnames.contains(hostname.toLowerCase())) {
            event.setKickMessage(this.kickMessage);
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
        }
    }
}