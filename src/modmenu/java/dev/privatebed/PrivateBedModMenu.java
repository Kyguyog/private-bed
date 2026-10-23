package dev.privatebed;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.io.IOException;
import java.nio.file.Path;

/** Loaded only through Mod Menu's optional client entrypoint. */
@Environment(EnvType.CLIENT)
public final class PrivateBedModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }

    private static final class ConfigScreen extends Screen {
        private final Screen parent;
        private final Path file = FabricLoader.getInstance().getConfigDir().resolve("private-bed.properties");
        private boolean bed, spawn, death;
        private String error;

        ConfigScreen(Screen parent) {
            super(Component.literal("Private Bed configuration"));
            this.parent = parent;
            try {
                CommandConfig config = CommandConfig.load(file);
                bed = config.bed(); spawn = config.spawn(); death = config.death();
            } catch (IOException failure) {
                error = "Cannot read config; check private-bed.properties";
            }
        }

        @Override
        protected void init() {
            int x = width / 2 - 150;
            int y = Math.max(10, height / 2 - 100);
            Button heading = addRenderableWidget(ConfigButtons.create(x, y, 300, title, b -> {}));
            heading.active = false;
            Button note = addRenderableWidget(ConfigButtons.create(x, y + 24, 300,
                    Component.literal("Local / singleplayer settings only"), b -> {}));
            note.active = false;
            addRenderableWidget(ConfigButtons.create(x, y + 52, 300, label("Bed", bed), b -> {
                bed = !bed; b.setMessage(label("Bed", bed));
            }));
            addRenderableWidget(ConfigButtons.create(x, y + 76, 300, label("World spawn", spawn), b -> {
                spawn = !spawn; b.setMessage(label("World spawn", spawn));
            }));
            addRenderableWidget(ConfigButtons.create(x, y + 100, 300, label("Last death", death), b -> {
                death = !death; b.setMessage(label("Last death", death));
            }));
            Button save = addRenderableWidget(ConfigButtons.create(x, y + 132, 148, Component.literal("Save"), b -> save()));
            save.active = error == null;
            addRenderableWidget(ConfigButtons.create(x + 152, y + 132, 148, Component.literal("Cancel"), b -> onClose()));
            if (error != null) {
                Button status = addRenderableWidget(ConfigButtons.create(x, y + 160, 300, Component.literal(error), b -> {}));
                status.active = false;
            }
        }

        private static Component label(String name, boolean enabled) {
            return Component.literal(name + ": " + (enabled ? "ON" : "OFF"));
        }

        private void save() {
            CommandConfig config = new CommandConfig(bed, spawn, death);
            try {
                config.save(file);
            } catch (IOException failure) {
                error = "Save failed; check config file access";
                clearWidgets();
                init();
                return;
            }
            var server = minecraft.getSingleplayerServer();
            if (server != null) FabricEntrypoint.applyLocalConfig(server, config);
            onClose();
        }

        @Override
        public void onClose() {
            ConfigNavigation.close(minecraft, parent);
        }
    }
}
