package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class SimpleMenuHandler extends ScreenHandler {
    private final ItemStack stack;
    private final Inventory inventory;

    public SimpleMenuHandler(int syncId, PacketByteBuf buf) {
        this(syncId, new SimpleInventory(20), buf.readItemStack());
    }

    public SimpleMenuHandler(int syncId, Inventory inventory, ItemStack stack) {
        super(ModItems.SIMPLE_MENU_HANDLER, syncId);
        this.stack = stack;
        this.inventory = inventory;
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        ItemStack itemStack = inventory.getStack(slotIndex);
        if (!itemStack.isEmpty()) {

            if (stack.getItem() == SkillCards.QICE) {
                give(player, itemStack);
                if (!player.isCreative()) player.getOffHandStack().decrement(2);
                setCD(stack, 20);
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.QICE1);} else {voice(player, Sounds.QICE2);}
                closeGUI(player);
            }

            if (stack.getItem() == SkillCards.TAOLUAN) {
                give(player, itemStack);
                if (!player.isCreative()) {
                    player.timeUntilRegen = 0;
                    player.damage(player.getDamageSources().genericKill(), 4.99f);
                }
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.TAOLUAN1);} else {voice(player, Sounds.TAOLUAN2);}
                closeGUI(player);
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {return ItemStack.EMPTY;}

    @Override
    public boolean canUse(PlayerEntity player) {
        return !player.hasStatusEffect(ModItems.COOLDOWN2) || (player.hasStatusEffect(ModItems.COOLDOWN2) && Objects.requireNonNull(player.getStatusEffect(ModItems.COOLDOWN2)).getAmplifier() != 2);
    }

    private void closeGUI(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1,2,false,false,false));
    }
}
