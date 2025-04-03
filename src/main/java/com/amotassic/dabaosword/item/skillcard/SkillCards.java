package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.api.skill.Relation;
import com.amotassic.dabaosword.api.skill.SkillExecutor;
import com.amotassic.dabaosword.api.skill.SkillInfo;
import com.amotassic.dabaosword.item.skillcard.skills.Qun;
import com.amotassic.dabaosword.item.skillcard.skills.Shu;
import com.amotassic.dabaosword.item.skillcard.skills.Wei;
import com.amotassic.dabaosword.item.skillcard.skills.Wu;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class SkillCards {
    public static final Map<Item, List<SkillExecutor>> SKILL_MAP = new HashMap<>();
    public static final List<SkillItem> SKILLS = new ArrayList<>();
    //魏
    public static final SkillItem
    CHENGXIANG = register("chengxiang", new Wei.Chengxiang()),
    DAOSHU = register("daoshu", new Wei.Daoshu()),
    DUANLIANG = register("duanliang", new Wei.Duanliang()),
    FANGZHU = register("fangzhu", new Wei.Fangzhu()),
    XINGSHANG = register("xingshang", new Wei.Xingshang()),
    GANGLIE = register("ganglie", new Wei.Ganglie()),
    GONGAO = register("gongao", new Wei.Gongao()),
    JIANXIONG = register("jianxiong", new Wei.Jianxiong()),
    JUEQING = register("jueqing", new Wei.Jueqing()),
    LUOSHEN = register("luoshen", new Wei.Luoshen()),
    QINGGUO = register("qingguo", new Wei.Qingguo()),
    LUOYI = register("luoyi", new Wei.Luoyi()),
    QICE = register("qice", new Wei.Qice()),
    QUANJI = register("quanji", new Wei.Quanji()),
    SHANZHUAN = register("shanzhuan", new Wei.Shanzhuan()),
    SHENSU = register("shensu", new Wei.Shensu()),
    YIJI = register("yiji", new Wei.Yiji()),
    //蜀
    BENXI = register("benxi", new Shu.Benxi()),
    HUILEI = register("huilei", new Shu.Huilei()),
    HUOJI = register("huoji", new Shu.Huoji()),
    KANPO = register("kanpo", new Shu.Kanpo()),
    JIZHI = register("jizhi", new Shu.Jizhi()),
    KUANGGU = register("kuanggu", new Shu.Kuanggu()),
    LIEGONG = register("liegong", new Shu.Liegong()),
    LONGDAN = register("longdan", new Shu.Longdan()),
    PAOXIAO = register("paoxiao", new Shu.Paoxiao()),
    RENDE = register("rende", new Shu.Rende()),
    TIEJI = register("tieji", new Shu.Tieji()),
    WUSHENG = register("wusheng", new Shu.Wusheng()),
    //吴
    BUQU = register("buqu", new Wu.Buqu()),
    FANJIAN = register("fanjian", new Wu.Fanjian()),
    FENYIN = register("fenyin", new Wu.Fenyin()),
    GONGXIN = register("gongxin", new Wu.Gongxin()),
    GUOSE = register("guose", new Wu.Guose()),
    LIULI = register("liuli", new Wu.Liuli()),
    LIANYING = register("lianying", new Wu.Lianying()),
    KUROU = register("kurou", new Wu.Kurou()),
    POJUN = register("pojun", new Wu.Pojun()),
    QIXI = register("qixi", new Wu.Qixi()),
    SHIXIN = register("shixin", new Wu.Shixin()),
    XIAOJI = register("xiaoji", new Wu.Xiaoji()),
    YINGZI = register("yingzi", new Wu.Yingzi()),
    ZHIHENG = register("zhiheng", new Wu.Zhiheng()),
    ZHIJIAN = register("zhijian", new Wu.Zhijian()),
    //群
    DUANCHANG = register("duanchang", new Qun.Duanchang()),
    JIJIU = register("jijiu", new Qun.Jijiu()),
    JIUCHI = register("jiuchi", new Qun.Jiuchi()),
    JIZHAN = register("jizhan", new Qun.Jizhan()),
    LEIJI = register("leiji", new Qun.Leiji()),
    LUANJI = register("luanji", new Qun.Luanji()),
    TAOLUAN = register("taoluan", new Qun.Taoluan()),
    WEIMU = register("weimu", new Qun.Weimu()),
    MASHU = register("mashu", new Qun.Mashu()),

    FEIYING = register("feiying", new Qun.Feiying());

    /**注册技能物品，同时注册技能的全局监听效果*/
    public static <T extends SkillItem> T register(String name, T item) {
        T skill = Registry.register(Registries.ITEM, new Identifier("dabaosword", name), item);
        SKILLS.add(skill);
        return skill;
    }

    private static void addSkillEffect(Item skill) {
        List<SkillExecutor> effectDatas = new ArrayList<>();
        Class<?> skillClass = skill.getClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Method method : skillClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SkillInfo.class)) {
                SkillInfo info = method.getAnnotation(SkillInfo.class);
                MethodHandle handle;
                try {
                    handle = lookup.unreflect(method).bindTo(skill);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                SkillExecutor effectData = new SkillExecutor(info.trigger(), Relation.getPredicate(info.relation()), handle);
                effectDatas.add(effectData);
            }
        }
        SKILL_MAP.put(skill, effectDatas);
    }

    public static void register() {
        Registries.ITEM.stream().filter(i -> i instanceof ISkill).forEach(SkillCards::addSkillEffect);
    }
}
