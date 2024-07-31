package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ServerNetworking;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;
import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerInvScreenHandler extends ScreenHandler {
    private final PlayerEntity target;
    private final ItemStack stack;
    private final int cards;

    public PlayerInvScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, new SimpleInventory(60), inv.player.getWorld().getPlayerByUuid(buf.readUuid()), buf.readItemStack());
    }

    public PlayerInvScreenHandler(int syncId, Inventory inventory, PlayerEntity target, ItemStack stack) {
        super(ServerNetworking.PLAYER_INV_SCREEN_HANDLER, syncId);
        this.target = target;
        this.stack = stack;
        this.cards = inventory.getStack(54).getCount();
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < 54) {
            var targetStack = selected(target, slotIndex); //openSelfInv 为 false 时
            var selfStack = selected(player, slotIndex); //openSelfInv 为 true 时
            if (selfStack != ItemStack.EMPTY) {

                if (stack.getItem() == SkillCards.RENDE) {
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.RENDE1);} else {voice(player, Sounds.RENDE2);}
                    give(target, selfStack.copyWithCount(1));
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("give_card.tip", stack.getName(), target.getDisplayName())));
                    player.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("give_card.tip", stack.getName(), target.getDisplayName())));
                    selfStack.decrement(1);
                    int cd = getCD(stack);
                    if (player.getHealth() < player.getMaxHealth() && cd == 0 && new Random().nextFloat() < 0.5) {
                        player.heal(5); voice(player, Sounds.RECOVER);
                        player.sendMessage(Text.translatable("recover.tip").formatted(Formatting.GREEN), true);
                        setCD(stack, 30);
                    }
                }

                if (stack.getItem() == SkillCards.YIJI) {
                    int i = getTag(stack);
                    give(target, selfStack.copyWithCount(1));
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("give_card.tip", stack.getName(), target.getDisplayName())));
                    player.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("give_card.tip", stack.getName(), target.getDisplayName())));
                    selfStack.decrement(1);
                    setTag(stack, i - 1);
                    if (i - 1 == 0) closeGUI(player);
                }
            }

            if (targetStack != ItemStack.EMPTY) {

                if (stack.getItem() == SkillCards.GONGXIN) {
                    targetStack.decrement(1);
                    closeGUI(player);
                }

                if (stack.getItem() == SkillCards.ZHIHENG) {
                    int z = getTag(stack);
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.ZHIHENG1);} else {voice(player, Sounds.ZHIHENG2);}
                    targetStack.decrement(1);
                    if (new Random().nextFloat() < 0.1) {
                        draw(player, 2);
                        player.sendMessage(Text.translatable("zhiheng.extra").formatted(Formatting.GREEN), true);
                    } else draw(player, 1);
                    setTag(stack, z - 1);
                    if (z - 1 == 0) closeGUI(player);
                }

                if (stack.getItem() == ModItems.STEAL) {
                    voice(player, Sounds.SHUNSHOU);
                    if (!player.isCreative()) {stack.decrement(1);}
                    jizhi(player); benxi(player);
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("dabaosword.steal")).append(targetStack.getName()));
                    give(player, targetStack.copyWithCount(1)); /*顺手：复制一个物品*/ targetStack.decrement(1);
                    closeGUI(player);
                }

                if (stack.getItem() == ModItems.DISCARD) {
                    voice(player, Sounds.GUOHE);
                    if (!player.isCreative()) {stack.decrement(1);}
                    jizhi(player); benxi(player);
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("dabaosword.discard")).append(targetStack.getName()));
                    targetStack.decrement(1);
                    closeGUI(player);
                }

                if (stack.getItem() == SkillCards.LUOYI) {
                    give(player, targetStack.copyWithCount(1));
                    targetStack.decrement(1);
                }
            }
        }
    }

    private ItemStack selected(PlayerEntity player, int slotIndex) {
        var itemStack = getSlot(slotIndex).getStack();
        if (!itemStack.isEmpty()) {
            if (slotIndex < 4) {
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
                if(component.isPresent()) {
                    List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                    for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                        ItemStack stack = entry.getRight();
                        if (itemStack.equals(stack)) return stack;
                    }
                }
            }
            if (slotIndex > 3 && slotIndex < 8) {
                for (ItemStack stack : player.getArmorItems()) {
                    if (itemStack.equals(stack)) return stack;
                }
            }
            if (slotIndex >= 8) {
                DefaultedList<ItemStack> inventory = player.getInventory().main;
                for (ItemStack stack : inventory) {
                    if (itemStack.equals(stack)) return stack;
                }
                if (player.getOffHandStack().equals(itemStack)) return player.getOffHandStack();
            }
        } else if (cards == 1) {
            List<ItemStack> candidate = new ArrayList<>();
            DefaultedList<ItemStack> inventory = player.getInventory().main;
            List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(
                            i -> inventory.get(i).isIn(Tags.Items.CARD) || inventory.get(i).getItem() == ModItems.GAIN_CARD)
                    .boxed().toList();
            for (Integer slot : cardSlots) {candidate.add(inventory.get(slot));}
            ItemStack off = player.getOffHandStack();
            if (off.isIn(Tags.Items.CARD) || off.getItem() == ModItems.GAIN_CARD) candidate.add(off);
            if(!candidate.isEmpty()) {
                Random r = new Random();
                int index = r.nextInt(candidate.size());
                return candidate.get(index);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {return ItemStack.EMPTY;}

    @Override
    public boolean canUse(PlayerEntity player) {
        return !player.hasStatusEffect(ModItems.COOLDOWN2) || (player.hasStatusEffect(ModItems.COOLDOWN2) && Objects.requireNonNull(player.getStatusEffect(ModItems.COOLDOWN2)).getAmplifier() != 2);
    }

    private void closeGUI(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1,2,false,false,false));
    }

    private void addPlayerInventorySlots(PlayerInventory playerInventory) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
