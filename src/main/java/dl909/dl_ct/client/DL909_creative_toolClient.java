package dl909.dl_ct.client;

import dl909.dl_ct.dl909_creative_tool;
import dl909.dl_ct.client.gui.screen.ingame.CrafterScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class DL909_creative_toolClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(dl909_creative_tool.CRAFTER_3X3, CrafterScreen::new);
        ScreenRegistry.register(dl909_creative_tool.BOX_SCREEN_HANDLER, dl909.dl_ct.client.gui.screen.ingame.BoxScreen::new);
    }
}
