package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.ui.QiceScreenHandler;
import com.amotassic.dabaosword.ui.TaoluanScreenHandler;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;
import static com.amotassic.dabaosword.util.ModTools.*;

public class ActiveSkill extends SkillItem {
    public ActiveSkill(Settings settings) {super(settings);}

    public static void active(PlayerEntity user, ItemStack stack, PlayerEntity target) {
        if (!user.getWorld().isClient && !user.hasStatusEffect(ModItems.TIEJI)) {

            if (stack.getItem() == SkillCards.YIJI) {
                int i = stack.getNbt() == null ? 0 : stack.getNbt().getInt("yiji");
                ItemStack stack1 = user.getStackInHand(Hand.MAIN_HAND);
                if (i > 0 && (stack1.isIn(Tags.Items.CARD) || stack1.getItem() == ModItems.GAIN_CARD)) {
                    give(target, stack1.copyWithCount(1));
                    target.sendMessage(Text.literal(user.getEntityName()).append(Text.translatable("give_card.tip", stack.getName(), target.getDisplayName())));
                    user.sendMessage(Text.literal(user.getEntityName()).append(Text.translatable("give_card.tip", stack.getName(), target.getDisplayName())));
                    stack1.decrement(1);
                    NbtCompound nbt = new NbtCompound(); nbt.putInt("yiji", i - 1); stack.setNbt(nbt);
                }
            }

            if (stack.getItem() == SkillCards.RENDE) {
                ItemStack stack1 = user.getStackInHand(Hand.MAIN_HAND);
                if (stack1.isIn(Tags.Items.CARD) || stack1.getItem() == ModItems.GAIN_CARD) {
                    if (new Random().nextFloat() < 0.5) {voice(user, Sounds.RENDE1);} else {voice(user, Sounds.RENDE2);}
                    give(target, stack1.copyWithCount(1));
                    target.sendMessage(Text.literal(user.getEntityName()).append(Text.translatable("give_card.tip", stack.getName(), target.getDisplayName())));
                    user.sendMessage(Text.literal(user.getEntityName()).append(Text.translatable("give_card.tip", stack.getName(), target.getDisplayName())));
                    stack1.decrement(1);
                    int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
                    if (cd == 0 && new Random().nextFloat() < 0.5) {
                        user.heal(5); voice(user, Sounds.RECOVER);
                        user.sendMessage(Text.translatable("recover.tip").formatted(Formatting.GREEN),true);
                        NbtCompound nbt = new NbtCompound();
                        nbt.putInt("cooldown", 30); stack.setNbt(nbt);
                    }
                }
            }
        }

    }

    public static void active(PlayerEntity user, ItemStack stack) {
        if (!user.getWorld().isClient && !user.hasStatusEffect(ModItems.TIEJI)) {

            if (stack.getItem() == SkillCards.ZHIHENG) {
                int z = stack.getNbt() == null ? 0 : stack.getNbt().getInt("zhi");
                ItemStack stack1 = user.getStackInHand(Hand.MAIN_HAND);
                if (stack1.isIn(Tags.Items.CARD)) {
                    if (z > 0) {
                        if (new Random().nextFloat() < 0.5) {voice(user, Sounds.ZHIHENG1);} else {voice(user, Sounds.ZHIHENG2);}
                        stack1.decrement(1);
                        if (new Random().nextFloat() < 0.1) {
                            give(user, new ItemStack(ModItems.GAIN_CARD, 2));
                            user.sendMessage(Text.translatable("zhiheng.extra").formatted(Formatting.GREEN), true);
                        } else give(user, new ItemStack(ModItems.GAIN_CARD, 1));
                        NbtCompound nbt = new NbtCompound(); nbt.putInt("zhi", z - 1); stack.setNbt(nbt);
                    } else user.sendMessage(Text.translatable("zhiheng.fail").formatted(Formatting.RED), true);
                }
            }

            if (stack.getItem() == SkillCards.LUOSHEN) {
                int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
                if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
                else {
                    if (new Random().nextFloat() < 0.5) {voice(user, Sounds.LUOSHEN1);} else {voice(user, Sounds.LUOSHEN2);}
                    if (new Random().nextFloat() < 0.5) {
                        draw(user,1);
                        user.sendMessage(Text.translatable("item.dabaosword.luoshen.win").formatted(Formatting.GREEN), true);
                    } else {
                        NbtCompound nbt = new NbtCompound();
                        nbt.putInt("cooldown", 30); stack.setNbt(nbt);
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
                int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
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
