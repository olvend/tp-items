package io.github.olvend.tpitems;

import io.github.olvend.tpitems.command.TPItemsCommand;
import io.github.olvend.tpitems.event.ItemInteractionEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

public class TPItems implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(new TPItemsCommand());

        ItemInteractionEvent itemInteractionEvent = new ItemInteractionEvent();
        UseItemCallback.EVENT.register(itemInteractionEvent);
        UseBlockCallback.EVENT.register(itemInteractionEvent);
    }
}
