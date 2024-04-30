package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.ui.QiceScreenHandler;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QiceSkill extends ActiveSkill implements ModTools {
    public QiceSkill(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (stack.getNbt() != null) {
            int cd = stack.getNbt().getInt("cooldown");
            tooltip.add(Text.literal(cd == 0 ? "CD: 20s" : "CD: 20s   left: "+cd/20+"s"));
        }
        tooltip.add(Text.translatable("item.dabaosword.qice.tooltip").formatted(Formatting.BLUE));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity) {
            NbtCompound nbt = new NbtCompound();
            if (stack.getNbt() == null) {nbt.putInt("cooldown", 0); stack.setNbt(nbt);}
            else {
                int cd = stack.getNbt().getInt("cooldown");
                if (cd > 0) {cd--; nbt.putInt("cooldown", cd); stack.setNbt(nbt);}
            }
        }
        super.tick(stack, slot, entity);
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
                    return new QiceScreenHandler(syncId, inv, stack);
                }
            });
        }
    }
}
