package org.creezo.realwinter;

import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author creezo
 */
public class PlayerHealthControler implements Runnable {
    private final Player player;
    private List<Player> PlayerHealthControler = RealWinter.PlayerHealthControler;
    private HashMap<Integer, Integer> PlayerHealthBuffer = RealWinter.PlayerHealthBuffer;
    private final RealWinter plugin;
    PlayerHealthControler(Player player, RealWinter plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    @Override
    public synchronized void run() {
        long WaitTime = 3000;
        int HPBufferDifference = 0, HPBufferBefore = 0, HPBufferCurrent = 0;
        PlayerHealthBuffer.put(player.getEntityId(), 0);
        while(PlayerHealthControler.contains(player)) {
            HPBufferCurrent = PlayerHealthBuffer.get(player.getEntityId());
            if(HPBufferCurrent!=0) {
                HPBufferDifference = HPBufferCurrent - HPBufferBefore;
                if(HPBufferCurrent > 3) {
                    if(WaitTime > 1000) WaitTime -= 100;
                } else if(HPBufferCurrent < 3) {
                    if(WaitTime < 8000) WaitTime += 100;
                }
                if(!RealWinter.PlayerIceHashMap.get(player.getEntityId())) {
                    if(HPBufferCurrent > 0 && HPBufferCurrent <= 6) {
                        if(!RealWinter.Config.getVariables().getBiomes().getWinter().isWinterKilliing() && player.getHealth() > 1) {
                            player.damage(1);
                            DamageEvent DamageEvent = new DamageEvent(player, 1, player.getHealth());
                            plugin.getServer().getPluginManager().callEvent(DamageEvent);
                        } else if(RealWinter.Config.getVariables().getBiomes().getWinter().isWinterKilliing()) {
                            player.damage(1);
                            DamageEvent DamageEvent = new DamageEvent(player, 1, player.getHealth());
                            plugin.getServer().getPluginManager().callEvent(DamageEvent);
                        }
                        HPBufferBefore = HPBufferCurrent;
                        HPBufferCurrent--;
                    } else if(HPBufferCurrent > 6) {
                        if(!RealWinter.Config.getVariables().getBiomes().getWinter().isWinterKilliing() && player.getHealth() > 2) {
                            player.damage(2);
                            DamageEvent DamageEvent = new DamageEvent(player, 2, player.getHealth());
                            plugin.getServer().getPluginManager().callEvent(DamageEvent);
                        } else if(RealWinter.Config.getVariables().getBiomes().getWinter().isWinterKilliing()) {
                            player.damage(2);
                            DamageEvent DamageEvent = new DamageEvent(player, 2, player.getHealth());
                            plugin.getServer().getPluginManager().callEvent(DamageEvent);
                        }
                        HPBufferBefore = HPBufferCurrent;
                        HPBufferCurrent -= 2;
                    } else if(HPBufferCurrent < 0) {
                        player.damage(-1);
                        HPBufferBefore = HPBufferCurrent;
                        HPBufferCurrent++;
                    }
                } else {
                    HPBufferCurrent = 0;
                    HPBufferBefore = 0;
                    PlayerHealthBuffer.put(player.getEntityId(), HPBufferCurrent);
                }
            } else {
                if(WaitTime > 3000) {
                    WaitTime -= 50;
                } else if(WaitTime < 3000) {
                    WaitTime += 50;
                }
            }
            if(HPBufferDifference != 0) {
                PlayerHealthBuffer.put(player.getEntityId(), HPBufferCurrent);
            }
            try {
                Thread.sleep(WaitTime);
            } catch (InterruptedException ex) {  }
        }
    }
    
}
