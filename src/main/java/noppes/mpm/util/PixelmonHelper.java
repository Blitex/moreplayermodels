package noppes.mpm.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class PixelmonHelper {

    public static boolean Enabled = false;

    private static Method getPixelmonModel = null;
	
	public static void load(){
		Enabled = Loader.isModLoaded("pixelmon");
        if(!Enabled)
        	return;

        try{
        	Class c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity2HasModel");
			getPixelmonModel = c.getMethod("getModel");
        }
        catch(Exception e){
        	
        }
        
	}
	
	
	public static List<String> getPixelmonList(){
		List<String> list = new ArrayList<String>();
		if(!Enabled)
			return list;
		try {
			Class c = Class.forName("com.pixelmonmod.pixelmon.enums.EnumPokemon");
			Object[] array = c.getEnumConstants();
			for(Object ob : array)
				list.add(ob.toString());
			
		} catch (Exception e) {
			LogManager.getLogger().error("getNameList", e);
		}
		return list;
	}

	public static boolean isPixelmon(Entity entity) {
		if(!Enabled)
			return false;
		return EntityList.getEntityString(entity).equals("pixelmon.Pixelmon");
	}

	public static void setName(EntityLivingBase entity, String name) {
		if(!Enabled || !isPixelmon(entity))
			return;
		try {
			Method m = entity.getClass().getMethod("init", String.class);
			m.invoke(entity, name);

			Class c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity2HasModel");
			m = c.getDeclaredMethod("loadModel");
			m.setAccessible(true);
			m.invoke(entity);
		} catch (Exception e) {
			LogManager.getLogger().error("setName", e);
		}
	}
	
	public static Object getModel(EntityLivingBase entity){
		try {
			return getPixelmonModel.invoke(entity);
		} catch (Exception e) {
			LogManager.getLogger().error("getModel", e);
		}
		return null;
	}
	
	public static String getName(EntityLivingBase entity) {
		if(!Enabled || !isPixelmon(entity))
			return "";
		try {
			Method m = entity.getClass().getMethod("getName");
			return m.invoke(entity).toString();
		} catch (Exception e) {
			LogManager.getLogger().error("getName", e);
		}
		return "";
	}


	public static void debug(EntityLivingBase entity) {
		if(!Enabled || !isPixelmon(entity))
			return;
		try {
			Method m = entity.getClass().getMethod("getModel");
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString((String) m.invoke(entity)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
