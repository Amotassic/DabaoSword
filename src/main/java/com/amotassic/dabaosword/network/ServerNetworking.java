package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.amotassic.dabaosword.ui.QiceScreenHandler;
import com.amotassic.dabaosword.ui.TaoluanScreenHandler;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;
import static com.amotassic.dabaosword.util.ModTools.*;

public class ServerNetworking {
    public static Identifier ACTIVE_SKILL = new Identifier("dabaosword:active_skill");
    public static Identifier ACTIVE_SKILL_TARGET = new Identifier("dabaosword:active_skill_target");
    public static Item[] active_skills_with_target = {SkillCards.RENDE, SkillCards.YIJI, SkillCards.GONGXIN, SkillCards.LUOYI};
    public static Item[] active_skills = {SkillCards.ZHIHENG, SkillCards.QICE, SkillCards.TAOLUAN, SkillCards.LUOSHEN, SkillCards.KUROU};

    public static void registerActiveSkillPacketHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ACTIVE_SKILL, ServerNetworking::receiveActiveSkillPacket);
        ServerPlayNetworking.registerGlobalReceiver(ACTIVE_SKILL_TARGET, ServerNetworking::receiveActiveSkillTargetPacket);
    }

    private static void receiveActiveSkillTargetPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID uuid = buf.readUuid(); PlayerEntity target = server.getPlayerManager().getPlayer(uuid);
        server.execute(() -> {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

            if(component.isPresent()) {
                List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                    ItemStack stack = entry.getRight();
                    if(Arrays.stream(active_skills_with_target).toList().contains(stack.getItem())) {
                        active(player, stack, target);
                        return;
                    }
                }
            }
        });
    }

    private static void receiveActiveSkillPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

            if(component.isPresent()) {
                List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                    if(Arrays.stream(active_skills).toList().contains(entry.getRight().getItem())) {
                        active(player, entry.getRight());
                        return;
                    }
                }
            }
        });
    }

    public static void active(PlayerEntity user, ItemStack stack, PlayerEntity target) {
        if (!user.getWorld().isClient && !user.hasStatusEffect(ModItems.TIEJI)) {

            if (stack.getItem() == SkillCards.LUOYI) {
                openInv(user, target, Text.literal("玩家背包"), stack, true, true, 3);
            }

            if (stack.getItem() == SkillCards.GONGXIN) {
                int cd = getCD(stack);
                if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
                else {
                    if (new Random().nextFloat() < 0.5) {voice(user, Sounds.GONGXIN1);} else {voice(user, Sounds.GONGXIN2);}
                    openInv(user, target, Text.translatable("gongxin.title"), stack, false, false, 2);
                    setCD(stack, 30);
                }
            }

            if (stack.getItem() == SkillCards.YIJI) {
                int i = getTag(stack);
                if (i > 0 ) openInv(user, target, true, Text.translatable("give_card.title", stack.getName()), stack, false, false, 2);
            }

            if (stack.getItem() == SkillCards.RENDE) {
                openInv(user, target, true, Text.translatable("give_card.title", stack.getName()), stack, false, false, 2);
            }
        }

    }

    public static void active(PlayerEntity user, ItemStack stack) {
        if (!user.getWorld().isClient && !user.hasStatusEffect(ModItems.TIEJI)) {

            if (stack.getItem() == SkillCards.ZHIHENG) {
                int z = getTag(stack);
                if (z > 0) openInv(user, user, Text.translatable("zhiheng.title"), stack, true, false, 2);
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
                ItemStack offStack = user.getStackInHand(Hand.OFF_HAND);
                int cd = getCD(stack);
                if (!offStack.isEmpty() && offStack.isIn(Tags.Items.CARD) && offStack.getCount() > 1) {
                    if (cd == 0) openQiceScreen(user, stack);
                    else {user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);}
                } else {user.sendMessage(Text.translatable("item.dabaosword.qice.tip").formatted(Formatting.RED), true);}
            }

            if (stack.getItem() == SkillCards.TAOLUAN) {
                if (user.getHealth() + 5 * count(user, Tags.Items.RECOVER) > 4.99) {openTaoluanScreen(user, stack);}
                else {user.sendMessage(Text.translatable("item.dabaosword.taoluan.tip").formatted(Formatting.RED), true);}
            }
        }
    }

    public static void openInv(PlayerEntity player, PlayerEntity target, Boolean openSelfInv, Text title, ItemStack stack, Boolean equip, Boolean armor, int cards) {
        PlayerEntity openInvTarget = openSelfInv ? player : target; //查看谁的物品栏？如果无需其他玩家作为目标，则无需使用openSelfInv
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
                    return new PlayerInvScreenHandler(syncId, targetInv(openInvTarget, equip, armor, cards), target, stack);
                }
            });
        }
    }

    public static void openInv(PlayerEntity player, PlayerEntity target, Text title, ItemStack stack, Boolean equip, Boolean armor, int cards) {openInv(player, target, false, title, stack, equip, armor, cards);}

    public static final ScreenHandlerType<PlayerInvScreenHandler> PLAYER_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "player_inv", new ExtendedScreenHandlerType<>(PlayerInvScreenHandler::new));

    public static Inventory targetInv(PlayerEntity target, Boolean equip, Boolean armor, int cards) {
        /*
        Boolean equip: 是否显示装备牌
        Boolean armor: 是否显示玩家的盔甲
        int cards: 是否显示手牌。0：完全不显示；1：显示随机选取手牌；2：显示所有手牌；3：显示所有物品
        int maxUse: 最大可操作次数，达到次数上限时关闭GUI
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

    public static void openQiceScreen(PlayerEntity player, ItemStack stack) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeItemStack(stack);
                }

                @Override
                public Text getDisplayName() {return Text.translatable("item.dabaosword.qice.screen");}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new QiceScreenHandler(syncId, new SimpleInventory(18), stack);
                }
            });
        }
    }

    public static void openTaoluanScreen(PlayerEntity player, ItemStack stack) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeItemStack(stack);
                }

                @Override
                public Text getDisplayName() {return Text.translatable("item.dabaosword.taoluan.screen");}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new TaoluanScreenHandler(syncId, stack, new SimpleInventory(18));
                }
            });
        }
    }
}
