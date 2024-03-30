package online.smyhw.mc.plugin.astronomican;

import org.bukkit.Effect;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

import static online.smyhw.mc.plugin.astronomican.Utils.isWithinRing;

public class Torch {
    //星炬坐标
    private Location torch_loc;

    //星炬等级(覆盖范围)
    private List<EffectRange> level_range;

    public Torch(Location torch_loc,List<EffectRange> level_range){
        this.torch_loc = torch_loc;
        this.level_range = level_range;
    }

    /**
     * 返回指定坐标在该信标的影响下所受到的效果列表
     * @return 效果列表
     */
    public List<EffectRange> for_me(Location loc){
        if(!loc.getWorld().getName().equals(torch_loc.getWorld().getName())){
            return new ArrayList<>();
        }
        List<EffectRange> res = new ArrayList<>();
        for(EffectRange one_er : this.level_range){
             if(!isWithinRing(this.torch_loc.getBlockX(),this.torch_loc.getBlockZ(), one_er.range_min, one_er.range_max, loc.getBlockX(),loc.getBlockZ())){
                continue;
            }
            res.add(one_er);
        }
        return res;
    }
}
