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
        while(RealWeather.PlayerDamage.containsKey(player) && RealWeather.Running) {
            int damage = RealWeather.PlayerDamage.get(player);
            for (int i = 0; i < damage; i++) {
                if(!plugin.Config.getVariables().getBiomes().getWinter().isWinterKilliing() && player.getHealth() > 1) {
                    player.damage(1);
                    DamageEvent DamageEvent = new DamageEvent(player, 1, player.getHealth());
                    plugin.getServer().getPluginManager().callEvent(DamageEvent);
                } else if(plugin.Config.getVariables().getBiomes().getWinter().isWinterKilliing()) {
                    player.damage(1);
                    DamageEvent DamageEvent = new DamageEvent(player, 1, player.getHealth());
                    plugin.getServer().getPluginManager().callEvent(DamageEvent);
                }
                if(damage-i!=1) {
                    try {
                        Thread.sleep(((plugin.Config.getVariables().getCheckDelay(plugin.Config.getVariables().getGameDifficulty())*1000)-100)/damage);
                    } catch (InterruptedException ex) {
                        if(RealWeather.Config.getVariables().isDebugMode()) RealWeather.log("Interrupted.");
                        RealWeather.log(ex.getMessage());
                        break;
                    }
                }
                if(!RealWeather.Running) break;
            }
            try {
                if(RealWeather.Running && RealWeather.PlayerDamage.containsKey(player)) {
                    synchronized (RealWeather.PlayerDamagerMap.get(player)) {
                        RealWeather.PlayerDamagerMap.get(player).wait();
                    }
                }
            } catch (InterruptedException ex) {
                plugin.log(ex.getMessage());
            }
        }
        if(RealWeather.Config.getVariables().isDebugMode()) RealWeather.log("Damager thread finished.");
    }
    
}
