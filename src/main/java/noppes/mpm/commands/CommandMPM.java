package noppes.mpm.commands;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.mpm.ModelData;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class CommandMPM extends MpmCommandInterface {
	private HashMap<String,Class<? extends EntityLivingBase>> entities = new HashMap<String, Class<? extends EntityLivingBase>>();
	private List<String> sub = Arrays.asList("url", "name", "entity", "scale", "animation", "sendmodel");
		
	public CommandMPM(){
        Map<?,?> mapping = EntityList.NAME_TO_CLASS;
        for(Object name : mapping.keySet()){
        	Class<?> c = (Class<?>) mapping.get(name);
        	try {
        		if(EntityLiving.class.isAssignableFrom(c) && c.getConstructor(new Class[] {World.class}) != null && !Modifier.isAbstract(c.getModifiers())){
        			entities.put(name.toString().toLowerCase(), c.asSubclass(EntityLivingBase.class));
        		}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
			}
        } 
        entities.put("clear", null);
	}
	
	@Override
	public String getCommandName() {
		return "mpm";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] var2) throws CommandException {
		if(var2.length < 1){
			throw new CommandException("Not enough arguments given");
		}
		String type = var2[0].toLowerCase();
		if(!sub.contains(type)){
			throw new CommandException("Unknown subcommand");
		}		
		var2 = Arrays.copyOfRange(var2, 1, var2.length);
		
		EntityPlayer player = null;
		if(var2.length > 1 && isPlayerOp(icommandsender)){
			try {
				player = getPlayer(server, icommandsender, var2[0]);
				var2 = Arrays.copyOfRange(var2, 1, var2.length);
			} catch (PlayerNotFoundException e) {
				
			}
		}
		if(player == null && icommandsender instanceof EntityPlayer)
			player = (EntityPlayer) icommandsender;
		
		if(player == null)
			throw new PlayerNotFoundException();

		ModelData data = ModelData.get(player);
		
		if(type.equals("url")){
			url(player, var2, data);
		}
		else if(type.equals("scale")){
			scale(player, var2, data);
		}		
		else if(type.equals("name")){
			name(player, var2, data);
		}	
		else if(type.equals("entity")){
			entity(player, var2, data);
		}
		else if(type.equals("animation")){
			animation(player, var2, data);
		}
		else if(type.equals("sendmodel")){
			sendmodel(server, player, var2, data);
		}
	}
	
	private void animation(EntityPlayer player, String[] var2, ModelData data) throws WrongUsageException {
		if(var2.length <= 0)
			throw new WrongUsageException("/mpm animation [@p] <animation>");
		
		String type = var2[0];
		EnumAnimation animation = null;

		for(EnumAnimation ani : EnumAnimation.values()){
			if(ani.name().equalsIgnoreCase(type)){
				animation = ani;
				break;
			}
		}
		
		if(animation == null){
			throw new WrongUsageException("Unknown animation " + type);
		}
		
		if(data.animation == animation){
			animation = EnumAnimation.NONE;
		}
		
		Server.sendAssociatedData(player, EnumPackets.ANIMATION, player.getUniqueID(), animation);
		data.setAnimation(animation);
	}
	
	private void entity(EntityPlayer player, String[] var2, ModelData data) throws WrongUsageException {
		if(var2.length <= 0)
			throw new WrongUsageException("/mpm entity [@p] <entity> (to go back to default /mpm entity [@p] clear)");
		
		String arg = var2[0].toLowerCase();
		if(!entities.containsKey(arg)){
			throw new WrongUsageException("Unknown entity: " + var2[0]);
		}
		data.setEntityClass(entities.get(arg));
		int i = 1;
		if(var2.length > i){
			while(i < var2.length){
		    	EntityLivingBase entity = data.getEntity(player);
		    	String[] split = var2[i].split(":");
		    	if(split.length == 2)
		    		data.setExtra(entity, split[0], split[1]);
		    	i++;
			}
		}
		
		Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
	}
	
	private void name(EntityPlayer player, String[] var2, ModelData data) throws WrongUsageException {
		if(var2.length <= 0)
			throw new WrongUsageException("/mpm name [@p] <name>");
		if(var2.length > 1 && var2[0].startsWith("&")){
			data.displayFormat = var2[0].replace('&', Character.toChars(167)[0]);
			var2 = Arrays.copyOfRange(var2, 1, var2.length);
		}
		data.displayName = var2[0];
		for(int i = 1; i < var2.length; i++)
			data.displayName += " " + var2[i];

		data.displayName = data.displayName.replace('&', Character.toChars(167)[0]);
		if(data.displayName.equalsIgnoreCase("clear"))
			data.displayName = "";
		player.refreshDisplayName();
		Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
	}

	private void url(EntityPlayer player, String[] var2, ModelData data) throws WrongUsageException{
		if(var2.length <= 0)
			throw new WrongUsageException("/mpm url [@p] <url> (to go back to default /mpm url [@p] clear)");
		String url = var2[0];
		for(int i = 1; i < var2.length; i++){
			url += " " + var2[i];
		}
		if(url.equalsIgnoreCase("clear"))
			url = "";
		data.url = url;
		Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
	}
	
	private void sendmodel(MinecraftServer server, EntityPlayer fromPlayer, String[] var2, ModelData fromData) throws WrongUsageException{
		if(var2.length < 1){
			throw new WrongUsageException("/mpm sendmodel [@from_player] <@to_player> (to go back to default /mpm sendmodel [@p] clear)");
		}
		
		EntityPlayer toPlayer = null;
		ModelData toData = null;

		try {
			toPlayer = getPlayer(server, fromPlayer, var2[0]);
		} catch (PlayerNotFoundException e) {}
				
		if(toPlayer == null || toPlayer == fromPlayer){
			if(var2[0].equalsIgnoreCase("clear")){
				fromData = new ModelData();
			}
			else
				throw new WrongUsageException("/mpm sendmodel [@from_player] <@to_player> (to go back to default /mpm sendmodel [@p] clear)");
		}
		else
			toData = ModelData.get(toPlayer);
		
		NBTTagCompound compound = fromData.writeToNBT();
		toData.readFromNBT(compound);
		toData.save();
		Server.sendAssociatedData(toPlayer, EnumPackets.SEND_PLAYER_DATA, toPlayer.getUniqueID(), compound);
	}
	
	private void scale(EntityPlayer player, String[] var2, ModelData data) throws WrongUsageException{
		try{
			if(var2.length == 1){
				Scale scale = Scale.Parse(var2[0]);
				data.head.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.body.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.arm1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.arm2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.leg1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.leg2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
			}
			else if(var2.length == 4){
				Scale scale = Scale.Parse(var2[0]);
				data.head.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				scale = Scale.Parse(var2[1]);
				data.body.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);

				scale = Scale.Parse(var2[2]);
				data.arm1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.arm2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);

				scale = Scale.Parse(var2[3]);
				data.leg1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.leg2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
			}
			else{
				throw new WrongUsageException("/mpm scale [@p] [head x,y,z] [body x,y,z] [arms x,y,z] [legs x,y,z]. Examples: /mpm scale @p 1, /mpm scale @p 1 1 1 1, /mpm scale 1,1,1 1,1,1 1,1,1 1,1,1");
			}
		}
		catch(NumberFormatException ex){
			throw new WrongUsageException("None number given");
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/mpm <url/model/scale/name/animation> [@p]";
	}
	
	@Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
	public List getTabCompletionOptions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
    	if(args.length == 1)
    		return CommandBase.getListOfStringsMatchingLastWord(args, sub);
    	if(args.length >= 2){
    		String type = args[0].toLowerCase();
    		List<String> list = new ArrayList<String>();
        	if(args.length == 2)
        		list.addAll(Arrays.asList(server.getAllUsernames()));
	    	if(type.equals("model")){
	    		list.addAll(entities.keySet());
	    	}
	    	if(type.equals("animation")){
	    		for(EnumAnimation ani : EnumAnimation.values()){
	    			list.add(ani.name().toLowerCase());
	    		}
	    	}
	    	return CommandBase.getListOfStringsMatchingLastWord(args, list);
    	}
    	return super.getTabCompletionOptions(server, par1, args, pos);
    }

	static class Scale{
		float scaleX, scaleY, scaleZ;
		
		private static Scale Parse(String s) throws NumberFormatException{
			Scale scale = new Scale();
			if(s.contains(",")){
				String[] split = s.split(",");
				if(split.length != 3)
					throw new NumberFormatException("Not enough args given");
				scale.scaleX = Float.parseFloat(split[0]);
				scale.scaleY = Float.parseFloat(split[1]);
				scale.scaleZ = Float.parseFloat(split[2]);
			}
			else{
				scale.scaleZ = scale.scaleY = scale.scaleX = Float.parseFloat(s);
			}
			return scale;
		}
	}
}
