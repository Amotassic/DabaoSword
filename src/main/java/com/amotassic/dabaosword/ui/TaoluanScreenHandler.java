package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
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
        if (0 <= slotIndex && slotIndex <16) {
            if (slotIndex == 0) player.giveItemStack(new ItemStack(ModItems.SHAN));
            if (slotIndex == 1) player.giveItemStack(new ItemStack(ModItems.PEACH));
            if (slotIndex == 2) player.giveItemStack(new ItemStack(ModItems.JIU));
            if (slotIndex == 3) player.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));
            if (slotIndex == 4) player.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));
            if (slotIndex == 5) player.giveItemStack(new ItemStack(ModItems.DISCARD));
            if (slotIndex == 6) player.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));
            if (slotIndex == 7) player.giveItemStack(new ItemStack(ModItems.JIEDAO));
            if (slotIndex == 8) player.giveItemStack(new ItemStack(ModItems.JUEDOU));
            if (slotIndex == 9) player.giveItemStack(new ItemStack(ModItems.NANMAN));
            if (slotIndex == 10) player.giveItemStack(new ItemStack(ModItems.STEAL));
            if (slotIndex == 11) player.giveItemStack(new ItemStack(ModItems.TAOYUAN));
            if (slotIndex == 12) player.giveItemStack(new ItemStack(ModItems.TIESUO));
            if (slotIndex == 13) player.giveItemStack(new ItemStack(ModItems.WANJIAN));
            if (slotIndex == 14) player.giveItemStack(new ItemStack(ModItems.WUXIE));
            if (slotIndex == 15) player.giveItemStack(new ItemStack(ModItems.WUZHONG));
            if (!player.isCreative()) player.setHealth(player.getHealth()-4.99f);
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1));
            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.TAOLUAN1);} else {voice(player, Sounds.TAOLUAN2);}
        }
    }

    public ItemStack getStack() {return stack;}

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return !player.hasStatusEffect(ModItems.COOLDOWN2);
    }
}
