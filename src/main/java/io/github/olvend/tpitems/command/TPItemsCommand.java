package io.github.olvend.tpitems.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TPItemsCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        LiteralCommandNode<CommandSourceStack> tpitems = dispatcher.register(
                Commands.literal("tp-items")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(
                                Commands.literal("cp")
                                        .executes(
                                                context -> execute(
                                                        context,
                                                        context.getSource().getPosition(),
                                                        context.getSource().getRotation(),
                                                        false
                                                )
                                        )
                                        .then(
                                                Commands.argument("location", Vec3Argument.vec3())
                                                        .executes(
                                                                context -> execute(
                                                                        context,
                                                                        Vec3Argument.getVec3(context, "location"),
                                                                        null,
                                                                        false
                                                                )
                                                        )
                                                        .then(
                                                                Commands.argument("rotation", RotationArgument.rotation())
                                                                        .executes(
                                                                                context -> execute(
                                                                                        context,
                                                                                        Vec3Argument.getVec3(context, "location"),
                                                                                        RotationArgument.getRotation(context, "rotation").getRotation(context.getSource()),
                                                                                        false
                                                                                )
                                                                        )
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("cpgen")
                                        .executes(
                                                context -> execute(
                                                        context,
                                                        context.getSource().getPosition(),
                                                        context.getSource().getRotation(),
                                                        true
                                                )
                                        )
                                        .then(
                                                Commands.argument("location", Vec3Argument.vec3())
                                                        .executes(
                                                                context -> execute(
                                                                        context,
                                                                        Vec3Argument.getVec3(context, "location"),
                                                                        null,
                                                                        true
                                                                )
                                                        )
                                                        .then(
                                                                Commands.argument("rotation", RotationArgument.rotation())
                                                                        .executes(
                                                                                context -> execute(
                                                                                        context,
                                                                                        Vec3Argument.getVec3(context, "location"),
                                                                                        RotationArgument.getRotation(context, "rotation").getRotation(context.getSource()),
                                                                                        true
                                                                                )
                                                                        )
                                                        )
                                        )
                        )
        );
        dispatcher.register(Commands.literal("tpi").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)).redirect(tpitems));
    }

    private int execute(CommandContext<CommandSourceStack> context, Vec3 vec3, Vec2 vec2, boolean gen) {
        ServerPlayer player;

        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            return 1;
        }

        ItemStack itemStack;

        if (!gen) {

            itemStack = player.getMainHandItem().isEmpty()
                    ? new ItemStack(Items.RED_DYE)
                    : player.getMainHandItem();

            CompoundTag rootTag = new CompoundTag();
            CompoundTag tpData = new CompoundTag();

            tpData.putString("x", String.valueOf(vec3.x));
            tpData.putString("y", String.valueOf(vec3.y));
            tpData.putString("z", String.valueOf(vec3.z));
            if (vec2 != null) {
                tpData.putString("yaw", String.valueOf(vec2.y));
                tpData.putString("pitch", String.valueOf(vec2.x));
            }

            rootTag.put("tp-items", tpData);
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(rootTag));
        } else {
            itemStack = new ItemStack(Items.COMMAND_BLOCK);

            CompoundTag tag = new CompoundTag();

            tag.putByte("auto", (byte) 0);
            tag.putString("Command", "item replace entity @p weapon.mainhand with minecraft:red_dye[minecraft:custom_data={tp-items:{x:\""
                            + vec3.x + "\",y:\"" + vec3.y + "\",z:\"" + vec3.z + "\""
                            + (vec2 == null ? "" : ",yaw:\"" + vec2.y + "\",pitch:\"" + vec2.x + "\"") + "}}]");
            tag.putString("id", "minecraft:command_block");
            tag.putInt("SuccessCount", 0);
            tag.putByte("TrackOutput", (byte) 1);
            tag.putByte("UpdateLastExecution", (byte) 1);

            itemStack.set(
                    DataComponents.BLOCK_ENTITY_DATA,
                    TypedEntityData.of(BlockEntityType.COMMAND_BLOCK, tag)
            );
        }
        player.getInventory().setItem(player.getInventory().getSelectedSlot(), itemStack);
        return 1;
    }
}
