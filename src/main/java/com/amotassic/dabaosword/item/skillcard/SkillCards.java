package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.ui.QiceScreenHandler;
import com.amotassic.dabaosword.ui.TaoluanScreenHandler;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class SkillCards {

    public static final Item YIJI = register("yiji",new YijiSkill(new FabricItemSettings().maxCount(1)));
    public static final Item TAOLUAN = register("taoluan", new TaoluanSkill(new FabricItemSettings().maxCount(1)));
    public static final ScreenHandlerType<TaoluanScreenHandler> TAOLUAN_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "taoluan", new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) -> new TaoluanScreenHandler(syncId, buf)));
    public static final Item LUOYI = register("luoyi", new LuoyiSkill(new FabricItemSettings().maxCount(1)));
    public static final Item HUOJI = register("huoji", new HuojiSkill(new FabricItemSettings().maxCount(1)));
    public static final Item QUANJI = register("quanji", new QuanjiSkill(new FabricItemSettings().maxCount(1)));
    public static final Item QICE = register("qice", new QiceSkill(new FabricItemSettings().maxCount(1)));
    public static final ScreenHandlerType<QiceScreenHandler> QICE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "qice", new ExtendedScreenHandlerType<>(QiceScreenHandler::new));

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM,new Identifier("dabaosword",name),item);
    }

    public static void register() {}
}
