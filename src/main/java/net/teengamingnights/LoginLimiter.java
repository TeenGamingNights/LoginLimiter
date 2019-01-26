package net.teengamingnights;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LoginLimiter extends JavaPlugin implements Listener {
    FileConfiguration config = getConfig();
    double delay;
    String kickMessage;
    double lastLogin;
    double enabledAt;
    double enableDelay;
    String delayMessage;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.delay = 1000/config.getDouble("connectionsPerSecond");
        this.kickMessage = config.getString("kickMessage");
        this.enableDelay = config.getDouble("allowConnectionsAfter");
        this.delayMessage = config.getString("serverDelayMessage");
        this.enabledAt = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
        System.out.println("LoginLimiter - delay of " + enableDelay + "ms, " + delay + "ms per user");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable(){

    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e){
        if(enabledAt + enableDelay < System.currentTimeMillis())
            if(lastLogin+delay < System.currentTimeMillis()){
                e.allow();
                lastLogin = System.currentTimeMillis();
                System.out.println("Allowed player " + e.getPlayer().getDisplayName() + " to join");
            }else{
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, this.kickMessage);
                System.out.println("Disallowed " + e.getPlayer().getDisplayName() + " from joining (Joining too quickly)");
            }
        else {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, this.delayMessage);
            System.out.println("Disallowed " + e.getPlayer().getDisplayName() + " from joining (Server not ready yet)");
        }
    }
}