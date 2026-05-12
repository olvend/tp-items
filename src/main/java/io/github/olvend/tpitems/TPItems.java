package io.github.olvend.tpitems;

import io.github.olvend.tpitems.command.TPItemsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class TPItems implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(new TPItemsCommand());
    }
}
