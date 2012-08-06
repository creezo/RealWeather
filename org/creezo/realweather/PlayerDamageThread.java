package org.creezo.realweather;

import org.bukkit.entity.Player;

/**
 *
 * @author creezo
 */
public class PlayerDamageThread implements Runnable {
    private final Player player;
    private final int damage;
    private final RealWeather plugin;

    public PlayerDamageThread(Player player, int damage, RealWeather plugin) {
        this.player = player;
        this.damage = damage;
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < damage; i++) {
            player.damage(1);
            if(damage-i!=1) {
                try {
                    Thread.sleep((plugin.Config.getVariables().getCheckDelay(plugin.Config.getVariables().getGameDifficulty())*1000)/damage);
                } catch (InterruptedException ex) {
                    RealWeather.log(ex.getMessage());
                }
            }
        }
    }
    
}
