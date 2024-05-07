package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Random;

public class TaoluanScreenHandler extends ScreenHandler implements ModTools {
    private final ItemStack stack;

    public TaoluanScreenHandler(int syncId, PacketByteBuf buf) {
        this(syncId, buf.readItemStack());
    }

    public TaoluanScreenHandler(int syncId, ItemStack stack) {
        super(SkillCards.TAOLUAN_SCREEN_HANDLER, syncId);
        this.stack = stack;
        SimpleInventory inventory = new SimpleInventory(18);
        checkSize(inventory, 18);
        int i;
        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        Item[] items = {ModItems.THUNDER_SHA, ModItems.FIRE_SHA, ModItems.SHAN, ModItems.PEACH, ModItems.JIU, ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.JUEDOU, ModItems.NANMAN, ModItems.STEAL, ModItems.TAOYUAN, ModItems.TIESUO, ModItems.WANJIAN, ModItems.WUXIE, ModItems.WUZHONG};
        if (0 <= slotIndex && slotIndex <items.length) {
            player.giveItemStack(new ItemStack(items[slotIndex]));
            if (!player.isCreative()) {
                player.timeUntilRegen = 0;
                player.damage(player.getDamageSources().genericKill(), 4.99f);
            }
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1,2));
            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.TAOLUAN1);} else {voice(player, Sounds.TAOLUAN2);}
        }
    }

    public ItemStack getStack() {return stack;}

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {return Items.AIR.getDefaultStack();}

    @Override
    public boolean canUse(PlayerEntity player) {return !player.hasStatusEffect(ModItems.COOLDOWN2);}
}
