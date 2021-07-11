package myrrhion.identityorigins;

import io.github.apace100.origins.Origins;
import myrrhion.identityorigins.powers.IdentityPowerFactory;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IdentityOrigins implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(IdentityOrigins.class);
    public static final String MODID = "identity-origins";
    @Override
    public void onInitialize() {
        IdentityPowerFactory.register();
    }
    public static Identifier identifier(String path) {
        return new Identifier(IdentityOrigins.MODID, path);
    }
}
