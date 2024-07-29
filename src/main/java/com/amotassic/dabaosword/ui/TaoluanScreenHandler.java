package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.give;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class TaoluanScreenHandler extends ScreenHandler {
    private final ItemStack stack;

    public TaoluanScreenHandler(int syncId, Inventory inventory, PacketByteBuf buf) {
        this(syncId, buf.readItemStack(), new SimpleInventory(18));
    }

    public TaoluanScreenHandler(int syncId, ItemStack stack, Inventory inventory) {
        super(SkillCards.TAOLUAN_SCREEN_HANDLER, syncId);
        this.stack = stack;
        int i;
        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        Item[] items = {ModItems.THUNDER_SHA, ModItems.FIRE_SHA, ModItems.SHAN, ModItems.PEACH, ModItems.JIU, ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.JUEDOU, ModItems.NANMAN, ModItems.STEAL, ModItems.TAOYUAN, ModItems.TIESUO, ModItems.WANJIAN, ModItems.WUXIE, ModItems.WUZHONG};
        if (0 <= slotIndex && slotIndex <items.length) {
            give(player, new ItemStack(items[slotIndex]));
            if (!player.isCreative()) {
                player.timeUntilRegen = 0;
                player.damage(player.getDamageSources().genericKill(), 4.99f);
            }
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1,2,false,false,false));
            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.TAOLUAN1);} else {voice(player, Sounds.TAOLUAN2);}
        }
    }

    public ItemStack getStack() {return stack;}

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {return ItemStack.EMPTY;}

    @Override
    public boolean canUse(PlayerEntity player) {
        return !player.hasStatusEffect(ModItems.COOLDOWN2) || (player.hasStatusEffect(ModItems.COOLDOWN2) && Objects.requireNonNull(player.getStatusEffect(ModItems.COOLDOWN2)).getAmplifier() != 2);
    }
}
