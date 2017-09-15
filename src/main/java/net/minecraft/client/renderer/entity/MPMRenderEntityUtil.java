package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class MPMRenderEntityUtil {
	public static ResourceLocation getResource(Entity entity){
		Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
		if(render == null)
			return null;
		return render.getEntityTexture(entity);
	}
}
