package noppes.mpm.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.item.ModItems;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabMPM;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabVanilla;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.mpm.CommonProxy;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.commands.CommandBow;
import noppes.mpm.client.commands.CommandCrawl;
import noppes.mpm.client.commands.CommandCry;
import noppes.mpm.client.commands.CommandDance;
import noppes.mpm.client.commands.CommandHug;
import noppes.mpm.client.commands.CommandNo;
import noppes.mpm.client.commands.CommandPoint;
import noppes.mpm.client.commands.CommandSit;
import noppes.mpm.client.commands.CommandSleep;
import noppes.mpm.client.commands.CommandWag;
import noppes.mpm.client.commands.CommandWave;
import noppes.mpm.client.commands.CommandYes;
import noppes.mpm.client.layer.LayerArms;
import noppes.mpm.client.layer.LayerBackItem;
import noppes.mpm.client.layer.LayerBody;
import noppes.mpm.client.layer.LayerCapeMPM;
import noppes.mpm.client.layer.LayerChatbubble;
import noppes.mpm.client.layer.LayerElytraAlt;
import noppes.mpm.client.layer.LayerEyes;
import noppes.mpm.client.layer.LayerHead;
import noppes.mpm.client.layer.LayerHeadwear;
import noppes.mpm.client.layer.LayerInterface;
import noppes.mpm.client.layer.LayerLegs;
import noppes.mpm.client.model.ModelBipedAlt;
import noppes.mpm.client.model.ModelPlayerAlt;

public class ClientProxy extends CommonProxy{
	public void preInit(FMLPreInitializationEvent event) {
		OBJLoader.INSTANCE.addDomain(MorePlayerModels.MODID);
		registerModel(ModItems.CerealModel);
	}

	public void registerModel(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MorePlayerModels.MODID + ";" + item.getUnlocalizedName().substring(5), "inventory"));
	}

	public static KeyBinding Screen;
	public static KeyBinding Sleep;
	public static KeyBinding Sit;
	public static KeyBinding Dance;
	public static KeyBinding Hug;
	public static KeyBinding Crawl;
	public static KeyBinding Camera;
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(FMLInitializationEvent event) {
		ModItems.registerRenders();
	}

	@Override
	public void load() {
		MorePlayerModels.Channel.register(new PacketHandlerClient());
		new PresetController(MorePlayerModels.instance.dir);

		ClientRegistry.registerKeyBinding(Screen = new KeyBinding("CharacterScreen", Keyboard.KEY_F12, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Sleep = new KeyBinding("MPM 1",Keyboard.KEY_Z, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Sit = new KeyBinding("MPM 2",Keyboard.KEY_X, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Dance = new KeyBinding("MPM 3",Keyboard.KEY_C, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Hug = new KeyBinding("MPM 4",Keyboard.KEY_V, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Crawl = new KeyBinding("MPM 5",Keyboard.KEY_B, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Camera = new KeyBinding("MPM Camera",Keyboard.KEY_LMENU, "key.categories.gameplay"));

		FMLCommonHandler.instance().bus().register(new ClientEventHandler());
		
		if(MorePlayerModels.EnableUpdateChecker){
			VersionChecker checker = new VersionChecker();
			checker.start();
		}
		MinecraftForge.EVENT_BUS.register(new RenderEvent());
		ClientCommandHandler.instance.registerCommand(new CommandBow());
		ClientCommandHandler.instance.registerCommand(new CommandCrawl());
		ClientCommandHandler.instance.registerCommand(new CommandCry());
		ClientCommandHandler.instance.registerCommand(new CommandDance());
		ClientCommandHandler.instance.registerCommand(new CommandHug());
		ClientCommandHandler.instance.registerCommand(new CommandNo());
		ClientCommandHandler.instance.registerCommand(new CommandPoint());
		ClientCommandHandler.instance.registerCommand(new CommandSit());
		ClientCommandHandler.instance.registerCommand(new CommandSleep());
		ClientCommandHandler.instance.registerCommand(new CommandWag());
		ClientCommandHandler.instance.registerCommand(new CommandWave());
		ClientCommandHandler.instance.registerCommand(new CommandYes());
	}

	@Override
	public void postload() {
		FixModels(true);
		
        if(MorePlayerModels.InventoryGuiEnabled){
	        MinecraftForge.EVENT_BUS.register(new TabRegistry());
	        
	        if (TabRegistry.getTabList().isEmpty()){
	        	TabRegistry.registerTab(new InventoryTabVanilla());
	        }
        	TabRegistry.registerTab(new InventoryTabMPM());
        }
	}
	public static void FixModels(boolean init){
		
		Map<String,RenderPlayer> map = ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, Minecraft.getMinecraft().getRenderManager(), 1);
		
		for(String type : map.keySet()){
			RenderPlayer render = map.get(type);
			FixModels(render, type.equals("slim"), !init);
			boolean hasMPMLayers = false;
			List<? extends LayerRenderer> list = render.layerRenderers;
			for(LayerRenderer layer : list){
				if(layer instanceof LayerInterface){
					((LayerInterface)layer).setModel((ModelBiped) render.getMainModel());
					hasMPMLayers = true;
				}
			}
			if(!hasMPMLayers)
				addLayers(render);				
		}
	}
	
	private static void FixModels(RenderPlayer render, boolean slim, boolean fix){
		if(!MorePlayerModels.Compatibility)
			render.mainModel = new ModelPlayerAlt(0, slim);
		else if(fix)
			render.mainModel = new ModelPlayer(0, slim);

		Iterator<? extends LayerRenderer> ita = render.layerRenderers.iterator();
		while(ita.hasNext()){
			LayerRenderer layer = ita.next();
			if(layer instanceof LayerArmorBase){
				LayerArmorBase l = (LayerArmorBase) layer;
				if(!MorePlayerModels.Compatibility){
					ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, l, new ModelBipedAlt(0.5f), 1);
					ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, l, new ModelBipedAlt(1), 2);
				}
				else if(fix){
					ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, l, new ModelBiped(0.5f), 1);
					ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, l, new ModelBiped(1), 2);
				}
			}
			if(layer instanceof LayerCustomHead){
				ObfuscationReflectionHelper.setPrivateValue(LayerCustomHead.class, (LayerCustomHead)layer, render.getMainModel().bipedHead, 0);
			}
			if(layer instanceof LayerElytra){
				ita.remove();				
			}
		}
		render.layerRenderers.add(new LayerElytraAlt(render));
	}
	
	private static void addLayers(RenderPlayer render){
		List<LayerRenderer<AbstractClientPlayer>> list = render.layerRenderers;
		Iterator<? extends LayerRenderer> it = list.iterator();
		while(it.hasNext()){
			LayerRenderer layer = it.next();
			if(layer instanceof LayerCape)
				it.remove();
		}
		list.add(new LayerEyes(render));
		list.add(new LayerHead(render));
		list.add(new LayerBody(render));
		list.add(new LayerArms(render));
		list.add(new LayerLegs(render));
		list.add(new LayerHeadwear(render));
		list.add(new LayerCapeMPM(render));
		list.add(new LayerChatbubble(render));
		list.add(new LayerBackItem(render));
	}
	
	public static void bindTexture(ResourceLocation location){
		if(location == null)
			return;
        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
    	ITextureObject ob = manager.getTexture(location);
    	if(ob == null){
    		ob = new SimpleTexture(location);
    		manager.loadTexture(location, ob);
    	}
        GlStateManager.bindTexture(ob.getGlTextureId());
        
	}
}
