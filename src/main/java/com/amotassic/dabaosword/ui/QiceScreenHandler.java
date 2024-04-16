package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.Random;

public class QiceScreenHandler extends ScreenHandler implements ModTools {
    private final ItemStack stack;

    public QiceScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv, buf.readItemStack());
    }

    public QiceScreenHandler(int syncId, PlayerInventory inv, ItemStack stack) {
        super(SkillCards.QICE_SCREEN_HANDLER, syncId);
        this.stack = stack;
        SimpleInventory inventory = new SimpleInventory(18);
        checkSize(inventory, 18);
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (0 <= slotIndex && slotIndex <11) {
            if (slotIndex == 0) player.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));
            if (slotIndex == 1) player.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));
            if (slotIndex == 2) player.giveItemStack(new ItemStack(ModItems.DISCARD));
            if (slotIndex == 3) player.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));
            if (slotIndex == 4) player.giveItemStack(new ItemStack(ModItems.JIEDAO));
            if (slotIndex == 5) player.giveItemStack(new ItemStack(ModItems.JUEDOU));
            if (slotIndex == 6) player.giveItemStack(new ItemStack(ModItems.NANMAN));
            if (slotIndex == 7) player.giveItemStack(new ItemStack(ModItems.STEAL));
            if (slotIndex == 8) player.giveItemStack(new ItemStack(ModItems.TIESUO));
            if (slotIndex == 9) player.giveItemStack(new ItemStack(ModItems.WUXIE));
            if (slotIndex == 10) player.giveItemStack(new ItemStack(ModItems.WUZHONG));
            if (!player.isCreative()) player.getStackInHand(Hand.OFF_HAND).decrement(3);
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1));
            ItemStack stack1 = player.getStackInHand(Hand.MAIN_HAND);
            NbtCompound nbt = new NbtCompound();nbt.putInt("cooldown", 20 * 30);stack1.setNbt(nbt);
            if (new Random().nextFloat() < 0.5) {
                voice(player, Sounds.QICE1);
            } else {
                voice(player, Sounds.QICE2);
            }
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
