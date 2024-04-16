package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.ui.TaoluanScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TaoluanSkill extends SkillItem {
    public TaoluanSkill(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.dabaosword.taoluan.tooltip"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (user.getHealth() > 4.99) {openScreen(user, user.getStackInHand(hand));}
            else {user.sendMessage(Text.translatable("item.dabaosword.taoluan.tip").formatted(Formatting.RED), true);}
        }
        return super.use(world, user, hand);
    }

    public static void openScreen(PlayerEntity player, ItemStack stack) {
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
}
