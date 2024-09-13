package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Pair;

import java.util.*;

import static com.amotassic.dabaosword.util.ModTools.*;

public class SimpleMenuHandler extends ScreenHandler {
    private final ItemStack eventStack;
    private final Inventory inventory;
    private final PlayerEntity target;

    public SimpleMenuHandler(int syncId, Inventory inventory, PlayerEntity target) {
        super(ModItems.SIMPLE_MENU_HANDLER, syncId);
        this.inventory = inventory;
        this.eventStack = inventory.getStack(18);
        this.target = target;
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        var stack = getStack(player, eventStack);
        ItemStack itemStack = inventory.getStack(slotIndex);
        if (!itemStack.isEmpty()) {

            if (stack.getItem() == SkillCards.QICE) {
                give(player, itemStack);
                if (!player.isCreative()) player.getOffHandStack().decrement(2);
                setCD(stack, 20);
                voice(player, Sounds.QICE);
                closeGUI(player);
            }

            if (stack.getItem() == SkillCards.TAOLUAN) {
                give(player, itemStack);
                if (!player.isCreative()) {
                    player.timeUntilRegen = 0;
                    player.damage(player.getDamageSources().genericKill(), 4.99f);
                }
                voice(player, Sounds.TAOLUAN);
                closeGUI(player);
            }
        }
    }

    private ItemStack getStack(PlayerEntity player, ItemStack eventStack) {
        List<ItemStack> candidate = new ArrayList<>();
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        if(component.isPresent()) {
            List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
            for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                candidate.add(entry.getRight());
            }
        }
        candidate.addAll(player.getInventory().main);
        for (ItemStack stack : candidate) {
            if (stack.equals(eventStack)) return stack;
        }
        return ItemStack.EMPTY;
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
