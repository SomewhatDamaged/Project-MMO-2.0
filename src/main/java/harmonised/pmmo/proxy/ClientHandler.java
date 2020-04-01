package harmonised.pmmo.proxy;

import harmonised.pmmo.gui.XPOverlayGUI;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

//@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler
{
    public static final KeyBinding SHOW_GUI = new KeyBinding( "key.pmmo.showgui", GLFW.GLFW_KEY_TAB, "category.pmmo" );
    public static final KeyBinding CRAWL_KEY = new KeyBinding( "key.pmmo.crawl", GLFW.GLFW_KEY_C, "category.pmmo" );

//    public static void registerKeybinds()
//    {
//
//    }

    public static void init()
    {
        MinecraftForge.EVENT_BUS.register( new XPOverlayGUI() );
        ClientRegistry.registerKeyBinding( SHOW_GUI );
        ClientRegistry.registerKeyBinding( CRAWL_KEY );
    }
}
