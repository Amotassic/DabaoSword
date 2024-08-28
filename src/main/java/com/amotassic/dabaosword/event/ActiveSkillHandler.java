package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.event.callback.ActiveSkillCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.equipment.EquipmentItem;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.amotassic.dabaosword.ui.SimpleMenuHandler;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;
import static com.amotassic.dabaosword.util.ModTools.*;

public class ActiveSkillHandler implements ActiveSkillCallback {
    @Override
    public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
        if (!user.getWorld().isClient && !user.hasStatusEffect(ModItems.TIEJI)) {

            if (stack.getItem() == SkillCards.ZHIHENG) {
                int z = getTag(stack);
                if (z > 0) openInv(user, user, Text.translatable("zhiheng.title"), stack, targetInv(user, true, false, 2));
                else user.sendMessage(Text.translatable("zhiheng.fail").formatted(Formatting.RED), true);
            }

            if (stack.getItem() == SkillCards.LUOSHEN) {
                int cd = getCD(stack);
                if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
                else {
                    if (new Random().nextFloat() < 0.5) {voice(user, Sounds.LUOSHEN1);} else {voice(user, Sounds.LUOSHEN2);}
                    if (new Random().nextFloat() < 0.5) {
                        draw(user,1);
                        user.sendMessage(Text.translatable("item.dabaosword.luoshen.win").formatted(Formatting.GREEN), true);
                    } else {
                        setCD(stack, 30);
                        user.sendMessage(Text.translatable("item.dabaosword.luoshen.lose").formatted(Formatting.RED), true);
                    }
                }
            }

            if (stack.getItem() == SkillCards.KUROU) {
                if (user.getHealth() + 5 * count(user, Tags.Items.RECOVER) > 4.99) {
                    give(user, new ItemStack(ModItems.GAIN_CARD, 2));
                    if (!user.isCreative()) {
                        user.timeUntilRegen = 0;
                        user.damage(user.getDamageSources().genericKill(), 4.99f);
                    }
                    if (new Random().nextFloat() < 0.5) {voice(user, Sounds.KUROU1);} else {voice(user, Sounds.KUROU2);}
                } else {user.sendMessage(Text.translatable("item.dabaosword.kurou.tip").formatted(Formatting.RED), true);}
            }

            if (stack.getItem() == SkillCards.QICE) {
                ItemStack offStack = user.getOffHandStack();
                int cd = getCD(stack);
                if (!offStack.isEmpty() && offStack.isIn(Tags.Items.CARD) && offStack.getCount() > 1) {
                    if (cd == 0) {

                        ItemStack[] stacks = {new ItemStack(ModItems.BINGLIANG_ITEM), new ItemStack(ModItems.TOO_HAPPY_ITEM), new ItemStack(ModItems.DISCARD), new ItemStack(ModItems.FIRE_ATTACK), new ItemStack(ModItems.JIEDAO), new ItemStack(ModItems.JUEDOU), new ItemStack(ModItems.NANMAN), new ItemStack(ModItems.STEAL), new ItemStack(ModItems.TAOYUAN), new ItemStack(ModItems.TIESUO), new ItemStack(ModItems.WANJIAN), new ItemStack(ModItems.WUXIE), new ItemStack(ModItems.WUZHONG)};
                        Inventory inventory = new SimpleInventory(20);
                        for (var stack1 : stacks) inventory.setStack(Arrays.stream(stacks).toList().indexOf(stack1), stack1);

                        openSimpleMenu(user, stack, inventory, Text.translatable("item.dabaosword.qice.screen"));
                    }
                    else {user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);}
                } else {user.sendMessage(Text.translatable("item.dabaosword.qice.tip").formatted(Formatting.RED), true);}
            }

            if (stack.getItem() == SkillCards.TAOLUAN) {
                if (user.getHealth() + 5 * count(user, Tags.Items.RECOVER) > 4.99) {

                    ItemStack[] stacks = {new ItemStack(ModItems.THUNDER_SHA), new ItemStack(ModItems.FIRE_SHA), new ItemStack(ModItems.SHAN), new ItemStack(ModItems.PEACH), new ItemStack(ModItems.JIU), new ItemStack(ModItems.BINGLIANG_ITEM), new ItemStack(ModItems.TOO_HAPPY_ITEM), new ItemStack(ModItems.DISCARD), new ItemStack(ModItems.FIRE_ATTACK), new ItemStack(ModItems.JIEDAO), new ItemStack(ModItems.JUEDOU), new ItemStack(ModItems.NANMAN), new ItemStack(ModItems.STEAL), new ItemStack(ModItems.TAOYUAN), new ItemStack(ModItems.TIESUO), new ItemStack(ModItems.WANJIAN), new ItemStack(ModItems.WUXIE), new ItemStack(ModItems.WUZHONG)};
                    Inventory inventory = new SimpleInventory(20);
                    for (var stack1 : stacks) inventory.setStack(Arrays.stream(stacks).toList().indexOf(stack1), stack1);

                    openSimpleMenu(user, stack, inventory, Text.translatable("item.dabaosword.taoluan.screen"));
                }
                else {user.sendMessage(Text.translatable("item.dabaosword.taoluan.tip").formatted(Formatting.RED), true);}
            }

            //==============================以下技能需要target==============================

            if (stack.getItem() == SkillCards.ZHIJIAN) {
                ItemStack itemStack = user.getMainHandStack();
                if (itemStack.getItem() instanceof EquipmentItem && itemStack.getItem() != ModItems.CARD_PILE) {
                    if (EquipmentItem.useEquip(target, itemStack)) {
                        if (new Random().nextFloat() < 0.5) {voice(user, Sounds.ZHIJIAN1);} else {voice(user, Sounds.ZHIJIAN2);}
                        draw(user, 1);
                    } else if (EquipmentItem.replaceEquip(target, itemStack)) {
                        if (new Random().nextFloat() < 0.5) {voice(user, Sounds.ZHIJIAN1);} else {voice(user, Sounds.ZHIJIAN2);}
                        draw(user, 1);
                    }
                } else user.sendMessage(Text.translatable("zhijian.fail").formatted(Formatting.RED), true);
            }

            if (stack.getItem() == SkillCards.GONGXIN) {
                int cd = getCD(stack);
                if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
                else {
                    if (new Random().nextFloat() < 0.5) {voice(user, Sounds.GONGXIN1);} else {voice(user, Sounds.GONGXIN2);}
                    openInv(user, target, Text.translatable("gongxin.title"), stack, targetInv(target, false, false, 2));
                    setCD(stack, 30);
                }
            }

            if (stack.getItem() == SkillCards.YIJI) {
                int i = getTag(stack);
                if (i > 0 ) openInv(user, target, Text.translatable("give_card.title", stack.getName()), stack, targetInv(user, false, false, 2));
            }

            if (stack.getItem() == SkillCards.RENDE) {
                openInv(user, target, Text.translatable("give_card.title", stack.getName()), stack, targetInv(user, false, false, 2));
            }
        }
    }

    public static void openInv(PlayerEntity player, PlayerEntity target, Text title, ItemStack stack, Inventory targetInv) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeUuid(target.getUuid());
                    buf.writeItemStack(stack);
                }

                @Override
                public Text getDisplayName() {return title;}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new PlayerInvScreenHandler(syncId, targetInv, target, stack);
                }
            });
        }
    }

    public static Inventory targetInv(PlayerEntity target, Boolean equip, Boolean armor, int cards) {
        /*
        Boolean equip: 是否显示装备牌
        Boolean armor: 是否显示玩家的盔甲
        int cards: 是否显示手牌。0：完全不显示；1：显示随机选取手牌；2：显示所有手牌；3：显示所有物品
        */
        Inventory targetInv = new SimpleInventory(60);
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(target);
        if(component.isPresent() && equip) {
            List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
            for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                ItemStack stack = entry.getRight();
                if (stack.streamTags().toList().equals(ModItems.GUDING_WEAPON.getDefaultStack().streamTags().toList())) targetInv.setStack(0, stack);
                if (stack.streamTags().toList().equals(ModItems.BAGUA.getDefaultStack().streamTags().toList())) targetInv.setStack(1, stack);
                if (stack.getItem() == ModItems.DILU) targetInv.setStack(2, stack);
                if (stack.getItem() == ModItems.CHITU) targetInv.setStack(3, stack);
            }//四件装备占1~4格
        }

        List<ItemStack> armors = List.of(target.getEquippedStack(EquipmentSlot.HEAD), target.getEquippedStack(EquipmentSlot.CHEST), target.getEquippedStack(EquipmentSlot.LEGS), target.getEquippedStack(EquipmentSlot.FEET));
        for (ItemStack stack : armors) {
            if (armor && !stack.isEmpty()) targetInv.setStack(armors.indexOf(stack) + 4, stack);
        }//4件盔甲占5~8格

        DefaultedList<ItemStack> targetInventory = target.getInventory().main;
        List<Integer> cardSlots = IntStream.range(0, targetInventory.size()).filter(
                        i -> targetInventory.get(i).isIn(Tags.Items.CARD) || targetInventory.get(i).getItem() == ModItems.GAIN_CARD)
                .boxed().toList();
        if (cards == 2 && !cardSlots.isEmpty()) {
            for(Integer i : cardSlots) {
                targetInv.setStack(i + 9, targetInventory.get(i));
            }
        }//副手物品在第9格，其他背包中的物品依次排列
        ItemStack off = target.getOffHandStack();
        if (cards == 2 && (off.isIn(Tags.Items.CARD) || off.getItem() == ModItems.GAIN_CARD)) targetInv.setStack(8, off);
        if (cards == 3) {
            for (ItemStack stack : targetInventory) {
                if (!stack.isEmpty()) targetInv.setStack(targetInventory.indexOf(stack) + 9, stack);
            }
            targetInv.setStack(8, off);
        }
        targetInv.setStack(54, new ItemStack(ModItems.GAIN_CARD, cards));
        return targetInv;
    }

    public static void openSimpleMenu(PlayerEntity player, ItemStack stack, Inventory inventory, Text title) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeItemStack(stack);
                }

                @Override
                public Text getDisplayName() {return title;}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new SimpleMenuHandler(syncId, inventory, stack);
                }
            });
        }
    }
}
