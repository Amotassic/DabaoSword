package com.amotassic.dabaosword.command;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.network.OpenInvPayload;
import com.amotassic.dabaosword.ui.FullInvScreenHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("info")
                .then(argument("target", EntityArgumentType.entity())
                        .executes(c -> run(EntityArgumentType.getEntity(c, "target"), c, false))
                        .then(argument("editable", BoolArgumentType.bool())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(c -> run(EntityArgumentType.getEntity(c, "target"), c, BoolArgumentType.getBool(c, "editable")))
                        )
                )
        );
    }

    private static int run(Entity entity, CommandContext<ServerCommandSource> context, boolean editable) {

        var player = context.getSource().getPlayer();
        if (player != null) {
            if (entity instanceof LivingEntity target) openFullInv(player, target, editable);
            else player.sendMessage(Text.translatable("info.fail").formatted(Formatting.RED));
        }
        return 1;
    }

    public static void openFullInv(PlayerEntity player, LivingEntity target, boolean editable) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public Object getScreenOpeningData(ServerPlayerEntity player) {
                    return new OpenInvPayload(target.getId());
                }

                @Override
                public Text getDisplayName() {return target.getDisplayName();}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new FullInvScreenHandler(syncId, inv, fullInv(target, editable), target);
                }
            });
        }
    }

    public static Inventory fullInv(LivingEntity target, boolean editable) {
        Inventory inv = new SimpleInventory(64);
        return updateInv(inv, target, editable);
    }

    public static Inventory updateInv(Inventory inventory, LivingEntity target, boolean editable) {
        //物品栏
        if (editable) {
            inventory.setStack(61, new ItemStack(ModItems.BBJI));
            if (target instanceof PlayerEntity player) {
                DefaultedList<ItemStack> inv = player.getInventory().main;
                for (var stack : inv) {
                    inventory.setStack(inv.indexOf(stack), stack);
                }
            } else if (target instanceof VillagerEntity villager) {
                DefaultedList<ItemStack> inv = villager.getInventory().heldStacks;
                for (var stack : inv) {
                    inventory.setStack(inv.indexOf(stack), stack);
                }
            } else inventory.setStack(0, target.getMainHandStack());
        }

        List<ItemStack> armors = List.of(target.getEquippedStack(EquipmentSlot.HEAD), target.getEquippedStack(EquipmentSlot.CHEST), target.getEquippedStack(EquipmentSlot.LEGS), target.getEquippedStack(EquipmentSlot.FEET));
        for (ItemStack stack : armors) {
            inventory.setStack(armors.indexOf(stack) + 36, stack);
        } //盔甲栏

        inventory.setStack(40, target.getOffHandStack()); //副手

        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(target);
        if (component.isPresent()) { //饰品栏
            List<Pair<SlotReference, ItemStack>> trinkets = component.get().getEquipped(stack -> true);
            List<ItemStack> stacks = new ArrayList<>();
            for (var trinket : trinkets) {
                stacks.add(trinket.getRight());
            }
            for (var stack : stacks) {
                inventory.setStack(stacks.indexOf(stack) + 41, stack);
            }
        }
        return inventory;
    }
}
