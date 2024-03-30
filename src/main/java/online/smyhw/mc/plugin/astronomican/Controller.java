package online.smyhw.mc.plugin.astronomican;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Controller extends Thread{
    public Controller(){
        this.start();
    }

    private boolean is_finish = false;

    @Override
    public void run(){
        Astronomican.logger.info("异步扫描线程启动...");
        while(!is_finish){
            try {
                Thread.sleep(Astronomican.effect_interval*1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Collection<Player> player_list = (Collection<Player>) Bukkit.getServer().getOnlinePlayers();
//            Astronomican.logger.info("获取到玩家 -> "+player_list);
            for (Player one_player : player_list){
                Location target_loc = one_player.getLocation();
                List<EffectRange> player_final_effect = new ArrayList<>();
                for(Torch one_torch : Astronomican.exist_torch){
                    List<EffectRange> ler = one_torch.for_me(target_loc);
                    player_final_effect = Utils.handle_dup_effect(player_final_effect,ler);
                }
                player_final_effect = Utils.remove_gl(player_final_effect);
                Utils.apply_effect(player_final_effect,one_player);

            }
        }
        Astronomican.logger.info("异步扫描线程退出完成!");
    }

    public void s_stop_it(){
        Astronomican.logger.info("异步扫描线程尝试退出...");
        this.is_finish = true;
    }
}
