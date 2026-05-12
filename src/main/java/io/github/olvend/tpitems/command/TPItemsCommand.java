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
        return 1;
    }
}
