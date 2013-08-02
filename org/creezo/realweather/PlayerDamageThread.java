package org.creezo.realweather;

import org.bukkit.entity.Player;

/**
 *
 * @author creezo
 */
public class PlayerDamageThread implements Runnable {
    private final Player player;
    private final RealWeather plugin;

    public PlayerDamageThread(Player player, RealWeather plugin) {
        this.player = player;
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        while(plugin.PlayerDamage.containsKey(player) && plugin.Running) {
            int damage = plugin.PlayerDamage.get(player);
            if(damage == -1) {
                
            }
            for (int i = 0; i < damage; i++) {
                if(!plugin.Config.getVariables().getBiomes().getFreezing().isFreezingKilliing() && player.getHealth() > 1) {
                    player.damage(1);
                } else if(plugin.Config.getVariables().getBiomes().getFreezing().isFreezingKilliing()) {
                    player.damage(1);
                }
                if(damage-i!=1) {
                    try {
                        Thread.sleep(((plugin.Config.getVariables().getCheckDelay(plugin.Config.getVariables().getGameDifficulty())*1000)-100)/damage);
                    } catch (InterruptedException ex) {
                        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Interrupted.");
                        plugin.log(ex.getMessage());
                        break;
                    }
                }
                if(!plugin.Running) break;
            }
            try {
                if(plugin.Running && plugin.PlayerDamage.containsKey(player)) {
                    synchronized (plugin.PlayerDamagerMap.get(player)) {
                        plugin.PlayerDamagerMap.get(player).wait();
                    }
                }
            } catch (InterruptedException ex) {
                plugin.log(ex.getMessage());
            }
        }
        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Damager thread finished.");
    }
    
}
