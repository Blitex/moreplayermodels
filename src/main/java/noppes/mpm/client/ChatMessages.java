package noppes.mpm.client;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import noppes.mpm.MorePlayerModels;

public class ChatMessages {
	private static Map<String,ChatMessages> users = new Hashtable<String,ChatMessages>();

	private Map<Long,TextBlockClient> messages = new TreeMap<Long,TextBlockClient>();

	private int boxLength = 46;
	private float scale = 0.5f;
	
	private String lastMessage = "";
	private long lastMessageTime = 0;
	
	public void addMessage(String message){
		if(!MorePlayerModels.EnableChatBubbles)
			return;
		long time = System.currentTimeMillis();
		if(message.equals(lastMessage) && lastMessageTime + 1000 > time){
			return;
		}
		Map<Long,TextBlockClient> messages = new TreeMap<Long,TextBlockClient>(this.messages);
		messages.put(time, new TextBlockClient(message, (int) (boxLength * 4)));

		if(messages.size() > 3){
			messages.remove(messages.keySet().iterator().next());
		}
		this.messages = messages;
		lastMessage = message;
		lastMessageTime = time;
	}    
	
	public void renderMessages(double par3, double par5, double par7, boolean inRange){
		Map<Long,TextBlockClient> messages = getMessages();
		if(messages.isEmpty())
			return;
		if(inRange)
			render(par3, par5, par7, false);
		render(par3, par5, par7, true);
    }    

	public void render(double par3, double par5, double par7, boolean depth){
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        float var13 = 1.6F;
        float var14 = 0.016666668F * var13;
        GlStateManager.pushMatrix();
        int size = 0;
        for(TextBlockClient block : messages.values())
        	size += block.lines.size();
        Minecraft mc = Minecraft.getMinecraft();
        int textYSize = (int) (size * font.FONT_HEIGHT * scale);
        GlStateManager.translate((float)par3 + 0.0F, (float)par5 + textYSize * var14, (float)par7);
        //GlStateManager.scale(textscale, textscale, textscale);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1F, 0.0F, 0.0F);
        GlStateManager.scale(-var14, -var14, var14);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.depthMask(true);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        if(depth)
        	GlStateManager.enableDepth();
        else
        	GlStateManager.disableDepth();
        int black = depth?0xFF000000:0x55000000;
        int white = depth?0xBBFFFFFF:0x44FFFFFF;
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableTexture2D();

        drawRect(-boxLength - 2, -2, boxLength + 2, textYSize + 1, white, 0.11);
        
        drawRect(-boxLength - 1, -3, boxLength + 1, -2, black, 0.1); //top
        drawRect(-boxLength - 1, textYSize + 2, -1, textYSize + 1, black, 0.1); //bottom1
        drawRect(3, textYSize + 2, boxLength + 1, textYSize + 1, black, 0.1); //bottom2
        drawRect(-boxLength - 3, -1, -boxLength - 2, textYSize, black, 0.1); //left
        drawRect(boxLength + 3, -1, boxLength + 2, textYSize, black, 0.1); //right

        drawRect(-boxLength - 2, -2, -boxLength - 1, -1, black, 0.1);
        drawRect(boxLength + 2, -2, boxLength + 1, -1, black, 0.1);
        drawRect(-boxLength - 2, textYSize + 1, -boxLength - 1, textYSize, black, 0.1);
        drawRect(boxLength + 2, textYSize + 1, boxLength + 1, textYSize, black, 0.1);

        drawRect(0, textYSize + 1, 3, textYSize + 4, white, 0.11);
        drawRect(-1, textYSize + 4, 1, textYSize + 5, white, 0.11);
        
        drawRect(-1, textYSize + 1, 0, textYSize + 4, black, 0.1);
        drawRect(3, textYSize + 1, 4, textYSize + 3, black, 0.1);
        drawRect(2, textYSize + 3, 3, textYSize + 4, black, 0.1);
        drawRect(1, textYSize + 4, 2, textYSize + 5, black, 0.1);
        drawRect(-2, textYSize + 4, -1, textYSize + 5, black, 0.1);
        
        drawRect(-2, textYSize + 5, 1, textYSize + 6, black, 0.1);
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        
        GlStateManager.scale(scale, scale, scale);
        int index = 0;
        for(TextBlockClient block : messages.values()){
        	for(ITextComponent chat : block.lines){
	        	String message = chat.getFormattedText();
	        	font.drawString(message, -font.getStringWidth(message) / 2, index * font.FONT_HEIGHT, black);
	        	index++;
        	}
        }
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
	}
	
	public static void drawRect(double x, double y, double x2, double y2, int color, double z)
    {
		double j1;

        if (x < x2)
        {
            j1 = x;
            x = x2;
            x2 = j1;
        }

        if (y < y2)
        {
            j1 = y;
            y = y2;
            y2 = j1;
        }

        float f = (float)(color >> 24 & 255) / 255.0F;
        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;
        VertexBuffer tessellator = Tessellator.getInstance().getBuffer();
        GlStateManager.color(1, 1, 1, 1);
        tessellator.begin(7, DefaultVertexFormats.POSITION_COLOR);
        tessellator.pos((double)x, (double)y, z).color(f1, f2, f3, f).endVertex();
        tessellator.pos((double)x, (double)y2, z).color(f1, f2, f3, f).endVertex();
        tessellator.pos((double)x2, (double)y2, z).color(f1, f2, f3, f).endVertex();
        tessellator.pos((double)x2, (double)y, z).color(f1, f2, f3, f).endVertex();
        Tessellator.getInstance().draw();
    }
	
	public static ChatMessages getChatMessages(String username){
		if(users.containsKey(username))
			return users.get(username);
		
		ChatMessages chat = new ChatMessages();
		users.put(username, chat);
		return chat;
	}
	
	private static Pattern[] patterns = new Pattern[]{
		Pattern.compile("^<+([a-zA-z0-9_]{2,16})>[:]? (.*)"), 
		Pattern.compile("^\\[.*[\\]]{1,16}[^a-zA-z0-9]?([a-zA-z0-9_]{2,16})[:]? (.*)"),
		Pattern.compile("^[a-zA-z0-9_]{2,10}[^a-zA-z0-9]([a-zA-z0-9_]{2,16})[:]? (.*)"),
	};
	
	public static void parseMessage(String toParse){
		toParse = toParse.replaceAll("\247.", "");
		for(Pattern pattern : patterns){
			Matcher m = pattern.matcher(toParse);
			if(m.find()){
				String username = m.group(1);
				if(!validPlayer(username))
					continue;
				String message = m.group(2);
				getChatMessages(username).addMessage(message);
				return;
			}
		}
	}
	
	public static void test() {
		test("<Sirnoppes01> :)","Sirnoppes01: :)");
		test("<Sirnoppes01> hey","Sirnoppes01: hey");
		test("<Sir_noppes> hey","Sir_noppes: hey");
		test("<Sirnoppes>: hey","Sirnoppes: hey");
		test("[member]Sirnoppes: hey","Sirnoppes: hey");
		test("[member]Sirnoppes01: hey","Sirnoppes01: hey");
		test("[member]Sir_noppes: hey","Sir_noppes: hey");
		test("[member] Sirnoppes: hey","Sirnoppes: hey");
		test("[g][member]Sirnoppes: hey","Sirnoppes: hey");
		test("[g] [member]Sirnoppes: hey","Sirnoppes: hey");
		test("[g] [member]-Sirnoppes: hey","Sirnoppes: hey");
		test("[Player755: Teleported Player755 to Player885]","");
		test("member Sirnoppes: hey","Sirnoppes: hey");
		test("member-Sirnoppes: hey","Sirnoppes: hey");
		test("member: Sirnoppes: hey","");
	}
	private static void test(String toParse, String result) {
		for(Pattern pattern : patterns){
			Matcher m = pattern.matcher(toParse);
			if(m.find()){
				String username = m.group(1);
				String message = m.group(2);
				if(message == null || username == null)
					continue;
				else if(result.isEmpty()){
					System.err.println("failed: " + toParse + " - " + username + ": " + message);
					return;
				}
				if((username +": " + message).equals(result)){
					System.out.println("succes: " + toParse);
					return;
				}
			}
		}
		if(result.isEmpty())
			System.out.println("succes: " + toParse);
		else
			System.err.println("failed: " + toParse);
	}

	private static boolean validPlayer(String username){
		return Minecraft.getMinecraft().theWorld.getPlayerEntityByName(username) != null;
	}

	private Map<Long,TextBlockClient> getMessages(){
		Map<Long, TextBlockClient> messages = new TreeMap<Long, TextBlockClient>();
		long time = System.currentTimeMillis();
		for(Entry<Long, TextBlockClient> entry : this.messages.entrySet()){
			if(time > entry.getKey() + 10000)
				continue;
			messages.put(entry.getKey(), entry.getValue());
		}
		return this.messages = messages;
	}

	public boolean hasMessage() {
		return !messages.isEmpty();
	}
	
}
