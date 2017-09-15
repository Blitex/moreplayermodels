package noppes.mpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.util.PixelmonHelper;


public class ModelData extends ModelDataShared implements ICapabilityProvider{	
	public static ExecutorService saveExecutor = Executors.newFixedThreadPool(1);
	
	@CapabilityInject(ModelData.class)
	public static Capability<ModelData> MODELDATA_CAPABILITY = null;
	
	public boolean resourceInit = false;
	public boolean resourceLoaded = false;

	public Object textureObject = null;
	
	public ItemStack backItem;
	
	public int inLove = 0;
	public int animationTime = -1;
	
	public EnumAnimation animation = EnumAnimation.NONE;
	public int animationStart = 0;
	
	public short soundType = 0;

	public double prevPosX, prevPosY, prevPosZ; 
	
	public EntityPlayer player = null;

	public NBTTagCompound writeToNBT(){
		NBTTagCompound compound = super.writeToNBT();				
		compound.setShort("SoundType", soundType);

		compound.setInteger("Animation", animation.ordinal());
				
		return compound;
	}
	
	public void readFromNBT(NBTTagCompound compound){
		String prevUrl = url;
		super.readFromNBT(compound);		
		soundType = compound.getShort("SoundType");


		if(player != null){
			player.refreshDisplayName();
			if(entityClass == null)
				player.getEntityData().removeTag("MPMModel");
			else
				player.getEntityData().setString("MPMModel", entityClass.getCanonicalName());
		}
		setAnimation(compound.getInteger("Animation"));

		if(resourceInit && !prevUrl.equals(url))
			resourceInit = false;
	}

	public void setAnimation(int i) {
		if(i < EnumAnimation.values().length)
			animation = EnumAnimation.values()[i];
		else
			animation = EnumAnimation.NONE;
		setAnimation(animation);
	}

	public void setAnimation(EnumAnimation ani) {
		animationTime = -1;
		animation = ani;
		
		if(animation == EnumAnimation.WAVING)
			animationTime = 80;
		
		if(animation == EnumAnimation.YES || animation == EnumAnimation.NO)
			animationTime = 60;
		
		if(player == null || ani == EnumAnimation.NONE)
			animationStart = -1;
		else
			animationStart = player.ticksExisted;
	}

	public EntityLivingBase getEntity(EntityPlayer player){
		if(entityClass == null)
			return null;
		if(entity == null){
			try {
				entity = entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {player.worldObj});

				entity.readEntityFromNBT(extra);
				if(entity instanceof EntityLiving){
					EntityLiving living = (EntityLiving)entity;
					living.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, player.getHeldItemMainhand());
					living.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, player.getHeldItemOffhand());
					living.setItemStackToSlot(EntityEquipmentSlot.HEAD, player.inventory.armorItemInSlot(3));
					living.setItemStackToSlot(EntityEquipmentSlot.CHEST, player.inventory.armorItemInSlot(2));
					living.setItemStackToSlot(EntityEquipmentSlot.LEGS, player.inventory.armorItemInSlot(1));
					living.setItemStackToSlot(EntityEquipmentSlot.FEET, player.inventory.armorItemInSlot(0));
				}

				if(PixelmonHelper.isPixelmon(entity) && player.worldObj.isRemote){
					if(extra.hasKey("Name"))
						PixelmonHelper.setName(entity, extra.getString("Name"));
					else
						PixelmonHelper.setName(entity, "Abra");
				}
			} catch (Exception e) {
			} 
		}
		return entity;
	}

	public ModelData copy(){
		ModelData data = new ModelData();
		data.readFromNBT(this.writeToNBT());
		data.resourceLoaded = resourceLoaded;
		data.player = player;
		return data;
	}

	public boolean isSleeping() {
		return isSleeping(animation);
	}
	private boolean isSleeping(EnumAnimation animation) {
		return animation == EnumAnimation.SLEEPING_EAST || animation == EnumAnimation.SLEEPING_NORTH ||
				animation == EnumAnimation.SLEEPING_SOUTH || animation == EnumAnimation.SLEEPING_WEST;
	}

	public boolean animationEquals(EnumAnimation animation2) {
		return animation2 == animation || isSleeping() && isSleeping(animation2);
	}

	public float getOffsetCamera(EntityPlayer player){
		if(!MorePlayerModels.EnablePOV)
			return 0;
		float offset = -offsetY();
		if(animation == EnumAnimation.SITTING){
			offset += 0.5f - getLegsY();
		}
		if(isSleeping())
			offset = 1.18f;
		if(animation == EnumAnimation.CRAWLING)
			offset = 0.8f;
		if(offset < -0.2f && isBlocked(player))
			offset = -0.2f;
		return offset;
	}

	private boolean isBlocked(EntityPlayer player) {
		return !player.worldObj.isAirBlock(new BlockPos(player).up(2));
	}

	
	public void setExtra(EntityLivingBase entity, String key, String value){
		key = key.toLowerCase();

		if(key.equals("breed") && EntityList.getEntityString(entity).equals("tgvstyle.Dog")){
			try {
				Method method = entity.getClass().getMethod("getBreedID");
				Enum breed = (Enum) method.invoke(entity);				
				method = entity.getClass().getMethod("setBreedID", breed.getClass());
				method.invoke(entity, breed.getClass().getEnumConstants()[Integer.parseInt(value)]);
				NBTTagCompound comp = new NBTTagCompound();
				entity.writeEntityToNBT(comp);
				extra.setString("EntityData21", comp.getString("EntityData21"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	if(key.equalsIgnoreCase("name") && PixelmonHelper.isPixelmon(entity)){
    		extra.setString("Name", value);
    	}
		clearEntity();
	}
	public void save(){
		if(player == null)
			return;
		final EntityPlayer player = this.player;
		saveExecutor.submit(new Runnable(){			
			public void run(){
				try {
					String filename = player.getUniqueID().toString().toLowerCase();
					if(filename.isEmpty())
						filename = "noplayername";
					filename += ".dat";
					File file = new File(MorePlayerModels.dir, filename+"_new");
					File file1 = new File(MorePlayerModels.dir, filename+"_old");
					File file2 = new File(MorePlayerModels.dir, filename);
					CompressedStreamTools.writeCompressed(writeToNBT(), new FileOutputStream(file));
					if(file1.exists())
					{
							file1.delete();
					}
					file2.renameTo(file1);
					if(file2.exists())
					{
							file2.delete();
					}
					file.renameTo(file2);
					if(file.exists())
					{
							file.delete();
					}
				} catch (Exception e) {
					LogWriter.except(e);
				}
			}
		});
	}
	
	public static ModelData get(EntityPlayer player){
		ModelData data = player.getCapability(MODELDATA_CAPABILITY, null);
		if(data.player == null){
			data.player = player;
			NBTTagCompound compound = loadPlayerData(player.getUniqueID());
			if(compound != null){
				data.readFromNBT(compound);
			}
		}
		return data;
	}

	private static NBTTagCompound loadPlayerData(UUID id){
		String filename = id.toString();
		if(filename.isEmpty())
			filename = "noplayername";
		filename += ".dat";
		try {
	        File file = new File(MorePlayerModels.dir, filename);
	        if(!file.exists()){
				return null;
	        }
	        return CompressedStreamTools.readCompressed(new FileInputStream(file));
		} catch (Exception e) {
			LogWriter.except(e);
		}
		try {
	        File file = new File(MorePlayerModels.dir, filename+"_old");
	        if(!file.exists()){
				return null;
	        }
	        return CompressedStreamTools.readCompressed(new FileInputStream(file));
	        
		} catch (Exception e) {
			LogWriter.except(e);
		}
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == MODELDATA_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(hasCapability(capability, facing))
			return (T) this;
		return null;
	}
}
