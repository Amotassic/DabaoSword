package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.ui.QiceScreenHandler;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Tags;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

public class QiceSkill extends SkillItem implements ModTools {
    public QiceSkill(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        int cd = 0;
        if (stack.getNbt() != null) {cd = stack.getNbt().getInt("cooldown");}
        tooltip.add(Text.literal(cd == 0 ? "CD: 20s" : "CD: 20s   left: "+cd/20+"s"));
        tooltip.add(Text.translatable("item.dabaosword.qice.tooltip").formatted(Formatting.BLUE));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(Hand.OFF_HAND);
            int cd = 0; ItemStack stack1 = user.getStackInHand(hand);
            if (stack1.getNbt() != null) {cd = stack1.getNbt().getInt("cooldown");}
            if (!stack.isEmpty() && stack.isIn(Tags.Items.CARD) && stack.getCount() > 1) {
                if (cd == 0) openScreen(user, user.getStackInHand(hand));
                else {user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);}
            }
            else {user.sendMessage(Text.translatable("item.dabaosword.qice.tip").formatted(Formatting.RED), true);}
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity) {
            int cd = 0; NbtCompound nbt = new NbtCompound();
            if (stack.getNbt() != null) {cd = stack.getNbt().getInt("cooldown");}
            if (cd > 0) {cd--; nbt.putInt("cooldown", cd); stack.setNbt(nbt);}
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public static void openScreen(PlayerEntity player, ItemStack stack) {
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
                    return new QiceScreenHandler(syncId, inv, stack);
                }
            });
        }
    }
}
