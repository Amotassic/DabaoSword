package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.network.QicePayload;
import com.amotassic.dabaosword.network.TaoluanPayload;
import com.amotassic.dabaosword.ui.QiceScreenHandler;
import com.amotassic.dabaosword.ui.TaoluanScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class SkillCards {
    //魏
    public static final Item DUANLIANG = register("duanliang", new DuanliangSkill(new Item.Settings().maxCount(1)));
    public static final Item FANGZHU = register("fangzhu", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item XINGSHANG = register("xingshang", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item GANGLIE = register("ganglie", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item GONGAO = register("gongao", new GongaoSkill(new Item.Settings().maxCount(1)));
    public static final Item JUEQING = register("jueqing", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item LUOSHEN = register("luoshen", new ActiveSkill(new Item.Settings().maxCount(1)));
    public static final Item QINGGUO = register("qingguo", new QingguoSkill(new Item.Settings().maxCount(1)));
    public static final Item LUOYI = register("luoyi", new LuoyiSkill(new Item.Settings().maxCount(1)));
    public static final Item QICE = register("qice", new ActiveSkill(new Item.Settings().maxCount(1)));
    public static final ScreenHandlerType<QiceScreenHandler> QICE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "qice", new ExtendedScreenHandlerType<>((syncId, inv, data) -> new QiceScreenHandler(syncId, inv), QicePayload.QICECODEC));
    public static final Item QUANJI = register("quanji", new QuanjiSkill(new Item.Settings().maxCount(1)));
    public static final Item YIJI = register("yiji",new SkillItem(new Item.Settings().maxCount(1)));
    //蜀
    public static final Item BENXI = register("benxi", new BenxiSkill(new Item.Settings().maxCount(1)));
    public static final Item HUOJI = register("huoji", new HuojiSkill(new Item.Settings().maxCount(1)));
    public static final Item KANPO = register("kanpo", new KanpoSkill(new Item.Settings().maxCount(1)));
    public static final Item JIZHI = register("jizhi", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item KUANGGU = register("kuanggu", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item LIEGONG = register("liegong", new LiegongSkill(new Item.Settings().maxCount(1)));
    public static final Item TIEJI = register("tieji", new SkillItem(new Item.Settings().maxCount(1)));
    //吴
    public static final Item GUOSE = register("guose", new GuoseSkill(new Item.Settings().maxCount(1)));
    public static final Item LIULI = register("liuli", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item KUROU = register("kurou", new ActiveSkill(new Item.Settings().maxCount(1)));
    public static final Item POJUN = register("pojun", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item QIXI = register("qixi", new QixiSkill(new Item.Settings().maxCount(1)));
    //群
    public static final Item LEIJI = register("leiji", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item LUANJI = register("luanji", new LuanjiSkill(new Item.Settings().maxCount(1)));
    public static final Item TAOLUAN = register("taoluan", new ActiveSkill(new Item.Settings().maxCount(1)));
    public static final ScreenHandlerType<TaoluanScreenHandler> TAOLUAN_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "taoluan", new ExtendedScreenHandlerType<>((syncId, inv, data) -> new TaoluanScreenHandler(syncId, inv), TaoluanPayload.TAOLUANCODEC));
    public static final Item MASHU = register("mashu", new SkillItem(new Item.Settings().maxCount(1)));

    public static final Item FEIYING = register("feiying", new SkillItem(new Item.Settings().maxCount(1)));

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM, Identifier.of("dabaosword", name), item);
    }

    public static void register() {}

}
