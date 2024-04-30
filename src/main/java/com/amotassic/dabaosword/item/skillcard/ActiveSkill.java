package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.ui.TaoluanScreenHandler;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static com.amotassic.dabaosword.item.skillcard.QiceSkill.openQiceScreen;

public class ActiveSkill extends SkillItem {
    public ActiveSkill(Settings settings) {super(settings);}

    public static void active(PlayerEntity user, ItemStack stack) {
        if (!user.getWorld().isClient) {

            if (stack.getItem() == SkillCards.KUROU) {
                if (user.getHealth() > 4.99) {
                    if (!user.isCreative()) user.setHealth(user.getHealth()-4.99f);
                    user.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 3));
                    if (new Random().nextFloat() < 0.5) {voice(Sounds.KUROU1, user);} else {voice(Sounds.KUROU2, user);}
                } else {user.sendMessage(Text.translatable("item.dabaosword.kurou.tip").formatted(Formatting.RED), true);}
            }

            if (stack.getItem() == SkillCards.QICE) {
                ItemStack offStack = user.getStackInHand(Hand.OFF_HAND);
                if (stack.getNbt() != null) {
                    int cd = stack.getNbt().getInt("cooldown");
                    if (!offStack.isEmpty() && offStack.isIn(Tags.Items.CARD) && offStack.getCount() > 1) {
                        if (cd == 0) openQiceScreen(user, stack);
                        else {user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);}
                    }
                    else {user.sendMessage(Text.translatable("item.dabaosword.qice.tip").formatted(Formatting.RED), true);}
                }
            }

            if (stack.getItem() == SkillCards.TAOLUAN) {
                if (user.getHealth() > 4.99) {openTaoluanScreen(user, stack);}
                else {user.sendMessage(Text.translatable("item.dabaosword.taoluan.tip").formatted(Formatting.RED), true);}
            }
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
                    return new TaoluanScreenHandler(syncId, stack);
                }
            });
        }
    }

    public static void voice(SoundEvent sound, PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld world) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
    }
}
