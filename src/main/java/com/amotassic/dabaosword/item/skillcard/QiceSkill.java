package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.network.QicePayload;
import com.amotassic.dabaosword.ui.QiceScreenHandler;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QiceSkill extends ActiveSkill implements ModTools {
    public QiceSkill(Settings settings) {super(settings);}

    private int tick = 0;
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity) {
            if (stack.get(ModItems.CD) == null) stack.set(ModItems.CD, 0);
            else {
                int cd = Objects.requireNonNull(stack.get(ModItems.CD));
                if (++tick >= 20) {
                    tick = 0;
                    if (cd > 0) {cd--; stack.set(ModItems.CD, cd);}
                }
            }
        }
        super.tick(stack, slot, entity);
    }

    public static void openQiceScreen(PlayerEntity player) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public Object getScreenOpeningData(ServerPlayerEntity player) {return new QicePayload(player.getBlockX());}

                @Override
                public Text getDisplayName() {return Text.translatable("item.dabaosword.qice.screen");}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new QiceScreenHandler(syncId, new SimpleInventory(18));
                }
            });
        }
    }
}
