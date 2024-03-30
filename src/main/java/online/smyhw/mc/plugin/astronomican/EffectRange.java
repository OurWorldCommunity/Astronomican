package online.smyhw.mc.plugin.astronomican;

import org.bukkit.Effect;
import org.bukkit.potion.PotionEffect;

public class EffectRange {
    //范围
    public int range_max;
    public int range_min;
    //免疫效果
    public PotionEffect eff_mit;

    //true为增益，false为减益
    public boolean gl;

    public EffectRange(int range_min,int range_max,PotionEffect eff_mit,boolean gl){
        this.range_min = range_min;
        this.range_max = range_max;
        this.eff_mit = eff_mit;
        this.gl = gl;
    }


    @Override
    public String toString(){
        String re = "";
        re+= "\nzf -> "+this.gl;
        re+= "\nmax -> "+this.range_max;
        re+= "\nmin -> "+this.range_min;
        re+= "\neff_name -> "+this.eff_mit.getType().getName();
        re+= "\neff_level -> "+this.eff_mit.getAmplifier();
        return re;
    }
}
