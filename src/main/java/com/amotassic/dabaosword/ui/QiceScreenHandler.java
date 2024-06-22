package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.Random;

public class QiceScreenHandler extends ScreenHandler implements ModTools {

    public QiceScreenHandler(int syncId, Inventory inventory) {
        super(SkillCards.QICE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 18);
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        Item[] items = {ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.JUEDOU, ModItems.NANMAN, ModItems.STEAL, ModItems.TAOYUAN, ModItems.TIESUO, ModItems.WANJIAN, ModItems.WUXIE, ModItems.WUZHONG};
        if (0 <= slotIndex && slotIndex <items.length) {
            player.giveItemStack(new ItemStack(items[slotIndex]));
            if (!player.isCreative()) player.getStackInHand(Hand.OFF_HAND).decrement(2);
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1,2,false,false,false));
            ItemStack stack1 = trinketItem(SkillCards.QICE, player);
            stack1.set(ModItems.CD, 20);
            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.QICE1);} else {voice(player, Sounds.QICE2);}
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {return null;}

    @Override
    public boolean canUse(PlayerEntity player) {return !player.hasStatusEffect(ModItems.COOLDOWN2);}

}
