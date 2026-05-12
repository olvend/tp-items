package io.github.olvend.tpitems.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;
import java.util.Set;

public class ItemInteractionEvent implements UseItemCallback, UseBlockCallback {
    @Override
    public InteractionResult interact(Player player, Level level, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);

        CompoundTag tpData = customData != null ? customData.copyTag().getCompound("tp-items").orElse(null) : null;

        if (level.isClientSide() || tpData == null || player.isSpectator()) {
            return InteractionResult.PASS;
        }

        Double x = parseCoordinate(tpData, "x", player.getX());
        Double y = parseCoordinate(tpData, "y", player.getY());
        Double z = parseCoordinate(tpData, "z", player.getZ());
        Float yaw = parseFacing(tpData, "yaw", player.getYRot());
        Float pitch = parseFacing(tpData, "pitch", player.getXRot());

        if ((x == null || y == null || z == null) || (yaw == null ^ pitch == null)) {
            return InteractionResult.FAIL;
        }

        player.teleportTo((ServerLevel) level, x, y, z, Set.of(), yaw == null ? player.getYRot() : yaw, pitch == null ? player.getXRot() : pitch, false);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interact(Player player, Level level, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return player.getItemInHand(interactionHand).getItem() instanceof BlockItem
                ? interact(player, level, interactionHand) : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private Double parseCoordinate(CompoundTag tag, String key, double defaultValue) {
        Optional<String> optional = tag.getString(key);
        if (optional.isEmpty()) {
            return null;
        }

        String value = optional.get();

        try {
            if (value.startsWith("~")) {
                return defaultValue + Double.parseDouble(value.substring(1));
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Float parseFacing(CompoundTag tag, String key, float defaultValue) {
        Optional<String> optional = tag.getString(key);
        if (optional.isEmpty()) {
            return null;
        }

        String value = optional.get();

        try {
            if (value.startsWith("~")) {
                return defaultValue + Float.parseFloat(value.substring(1));
            }
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
