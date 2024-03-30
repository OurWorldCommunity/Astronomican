package online.smyhw.mc.plugin.astronomican;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Utils {


    /**
     * 处理效果列表
     * (仅适用于只有两个核心列表的情况)
     * @param org 原始效果列表
     * @param sub 减免效果列表
     * @return 最终效果列表
     */
    @Deprecated
    public static List<EffectRange> handle_effects(List<EffectRange> org,List<EffectRange> sub){
        List<EffectRange> final_res = new ArrayList<>();
        List<EffectRange> org_copy = new ArrayList<>(org);
        for(EffectRange one_effect : sub){
            PotionEffectType sub_type = one_effect.eff_mit.getType();
            //对应原始列表效果
            EffectRange corr_org = null;
            for(EffectRange one_eff : org_copy){
                if(one_eff.eff_mit.getType() == sub_type){
                    corr_org  = one_eff;
                }
            }
            //原始列表中没有这个效果，直接添加
            if(corr_org==null){
                final_res.add(one_effect);
                continue;
            }
            //从原始列表中删除匹配到的效果
            org_copy.remove(corr_org);
            //如果增减相异，处理增减
            if(corr_org.gl != one_effect.gl){
                EffectRange gla = null;
                EffectRange glb = null;
                if(corr_org.gl){
                    gla = corr_org;
                    glb = one_effect;
                }else{
                    gla = one_effect;
                    glb = corr_org;
                }
                int final_amp = gla.eff_mit.getAmplifier() - glb.eff_mit.getAmplifier();//等级
                if(final_amp>0){
                    PotionEffect pe = new PotionEffect(gla.eff_mit.getType(),Astronomican.dur_time,final_amp);
                    EffectRange er = new EffectRange(-1,-1,pe,true);
                    final_res.add(er);
                }else{
                    //效果完全抵消
                    continue;
                }
            }
            //如果都是减益，则不处理
            if(!corr_org.gl) {
                continue;
            }
            //都是增益，叠加等级
            if(corr_org.gl){
                int final_amp = one_effect.eff_mit.getAmplifier() - corr_org.eff_mit.getAmplifier();
                PotionEffect pe = new PotionEffect(one_effect.eff_mit.getType(),Astronomican.dur_time,final_amp);
                EffectRange er = new EffectRange(-1,-1,pe,true);
                final_res.add(er);
            }
            continue;
        }
        //添加没有匹配到的效果
        final_res.addAll(org_copy);
        return final_res;
    }


    /**
     * 合并两个效果列表
     * @param er_1 需合并列表
     * @param er_2 需合并列表
     * @return 合并完成的列表
     */
    public static List<EffectRange> handle_dup_effect(List<EffectRange> er_1,List<EffectRange> er_2){
        List<EffectRange> all_list = new ArrayList<>();
        List<EffectRange> final_list = new ArrayList<>();
        all_list.addAll(er_1);
        all_list.addAll(er_2);
        for (int index = 0;index< all_list.size();index++){
            EffectRange curr_eff = all_list.get(index);
            //找一样类型的效果
            for (EffectRange next_er : new ArrayList<>(all_list.subList(index+1,all_list.size()))){
                if(!next_er.eff_mit.getType().getName().equals(curr_eff.eff_mit.getType().getName())){
                    continue;
                }
                //从列表中删除这个
//                Astronomican.logger.info("合并事件：");
//                Astronomican.logger.info("A："+next_er.gl);
//                Astronomican.logger.info("A："+next_er.eff_mit.getType().getName());
//                Astronomican.logger.info("A："+next_er.eff_mit.getAmplifier());
//                Astronomican.logger.info("B："+curr_eff.gl);
//                Astronomican.logger.info("B："+curr_eff.eff_mit.getType().getName());
//                Astronomican.logger.info("B："+curr_eff.eff_mit.getAmplifier());
                all_list.remove(next_er);
                int amp_er = get_amp(next_er);
                int amp_cu = get_amp(curr_eff);
                int amp_final = amp_cu+amp_er;
                curr_eff = set_amp(next_er,amp_final);
//                Astronomican.logger.info("C："+curr_eff.gl);
//                Astronomican.logger.info("C："+curr_eff.eff_mit.getType().getName());
//                Astronomican.logger.info("C："+curr_eff.eff_mit.getAmplifier());
            }
            final_list.add(curr_eff);
        }
//        for(EffectRange er : final_list){
//            Astronomican.logger.info(er.toString());
//        }

        return final_list;
    }


    /**
     * 删除所有减益
     * @param input 传入效果列表
     * @return 结果
     */
    public static List<EffectRange> remove_gl(List<EffectRange> input){
        for(int i = 0;i<input.size();i++){
            EffectRange er = input.get(i);
            if(!er.gl){
                input.remove(i);
                i--;
                continue;
            }
            if(get_amp(er)<0){
                input.remove(i);
                i--;
                continue;
            }
        }
        return input;
    }


    /**
     * 获取剩余等级
     * @param er 效果
     * @return 等级
     */
    public static int get_amp(EffectRange er){
        int amp = er.eff_mit.getAmplifier();
        if(er.gl){
            return amp;
        }else {
            return -amp;
        }
    }

    /**
     * 设置效果等级
     * 返回证等级为正数，使用gl判断正负面
     * @param er 效果
     * @param amp 等级
     * @return 结果
     */
    public static EffectRange set_amp(EffectRange er,int amp){
        EffectRange res = new EffectRange(0,0,null,false);
        if(amp>=0){
            res.gl = true;
            res.eff_mit = new PotionEffect(er.eff_mit.getType(),Astronomican.dur_time,amp);
        } else {
            res.gl = false;
            res.eff_mit = new PotionEffect(er.eff_mit.getType(),Astronomican.dur_time,-amp);
        }
        return res;
    }



    public static void load_cfg(){
        //常规配置项
        Astronomican.dur_time = Astronomican.config.getInt("config.dur_time",15);
        Astronomican.effect_interval = Astronomican.config.getInt("config.effect_interval",10);

        Astronomican.exist_torch = new ArrayList<>();

        //读取信标配置
        ConfigurationSection section = Astronomican.config.getConfigurationSection("data");
        for (String torch_name : section.getKeys(false)) {
            Astronomican.logger.info("解析星炬 -> "+torch_name);
            //读取信标中心
            String world_name = Astronomican.config.getString("data."+torch_name+".center_loc.world_name","world");
            int x = Astronomican.config.getInt("data."+torch_name+".center_loc.x",0);
            int y = Astronomican.config.getInt("data."+torch_name+".center_loc.y",0);
            int z = Astronomican.config.getInt("data."+torch_name+".center_loc.z",0);
            //读取效果列表
            List<EffectRange> erf = load_EffectRange("data."+torch_name+".effect_list");


            World world = Bukkit.getWorld(world_name);

            //合法性检查
            if(world==null){
                Astronomican.logger.warning("世界<"+world_name+">没有找到!");
                continue;
            }

            Location loc = new Location(world,x,y,z);
            Torch torch = new Torch(loc,erf);
            Astronomican.exist_torch.add(torch);
        }
    }

    /**
     * 这个独立的方法只为了在指定位置读效果列表
     * @param cfg_path 配置定位字符串
     * @return 效果列表
     */
    public static List<EffectRange> load_EffectRange(String cfg_path){
        Astronomican.logger.info("读取效果列表 -> "+cfg_path);
        List<EffectRange> res = new ArrayList<>();
        List<Map<?, ?>> eff_list = Astronomican.config.getMapList(cfg_path);
        for(Map one_effect : eff_list){
            String eff_name = one_effect.keySet().toArray()[0]+"";
            Astronomican.logger.info("效果名称 -> "+eff_name);
            Map<String, Object> sub_map = (Map<String, Object>) one_effect.get(eff_name);
            int level = (Integer)sub_map.getOrDefault("level",100)-1;
            int range_min = (Integer)sub_map.getOrDefault("range_min",1);
            int range_max = (Integer)sub_map.getOrDefault("range_max",999);
            boolean gl = (Boolean) sub_map.getOrDefault("gl",true);
            Astronomican.logger.info("等级 -> "+level);
            Astronomican.logger.info("范围 -> "+range_min+" - "+range_max);
            Astronomican.logger.info("是否增益 -> "+gl);
            PotionEffectType pet = PotionEffectType.getByName(eff_name);
            //检查合法性
            if(pet==null){
                Astronomican.logger.warning("效果名称<"+eff_name+">无效(配置定位"+cfg_path+")，正确的名称请参考:https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html");
                continue;
            }
            if(range_max!=-1 && range_min>range_max){
                Astronomican.logger.warning("效果范围最大值不能小于最小值(配置定位"+cfg_path+")");
                continue;
            }

            PotionEffect pe = new PotionEffect(pet,Astronomican.dur_time,level);
            EffectRange er = new EffectRange(range_min,range_max,pe,gl);
            res.add(er);
        }
        return res;
    }


    /**
     * 给定圆心和两个半径，计算目标点是否在圆环内
     * @param c_x 圆心x
     * @param c_y 圆心y
     * @param range_min 半径1
     * @param range_max 半径2
     * @param t_x 目标点x
     * @param t_y 目标点y
     * @return 是否在范围内
     */
    public static boolean isWithinRing(int c_x, int c_y, int range_min, int range_max, int t_x ,int t_y) {
        long distance = (long)Math.abs(c_x - t_x) * (long)Math.abs(c_x - t_x) + (long)Math.abs(c_y - t_y) * (long)Math.abs(c_y - t_y);
        boolean res_min = distance >= Math.pow((long)range_min, 2);
        boolean res;
        if (range_max==-1){
            res = res_min;
        }else{
            res = (distance <= Math.pow((long)range_max, 2)) && res_min;
        }

//        Astronomican.logger.warning("距离 -> "+Math.sqrt(distance));
//        Astronomican.logger.warning("圆心 -> "+c_x+" , "+c_y);
//        Astronomican.logger.warning("半径 -> "+range_min);
//        Astronomican.logger.warning("半径 -> "+range_max);
//        Astronomican.logger.warning("目标 -> "+t_x + " , "+t_y);
//
//        Astronomican.logger.warning("结果 -> "+res);
        return res;
    }


    /**
     * 为指定玩家应用这些效果
     * @param ler 要应用的效果
     * @param player 要应用的玩家
     */
    public static void apply_effect(List<EffectRange> ler, Player player){
        new fk_async_apply_effect(ler,player);
    }
}

class fk_async_apply_effect extends BukkitRunnable {
    List<EffectRange> ler;
    Player player;
    public fk_async_apply_effect(List<EffectRange> ler, Player player){

        this.ler = ler;
        this.player = player;
        this.runTask(Astronomican.plugin);
    }

    @Override
    public void run() {
        for(EffectRange er: this.ler){
            player.addPotionEffect(er.eff_mit);
        }
    }
}