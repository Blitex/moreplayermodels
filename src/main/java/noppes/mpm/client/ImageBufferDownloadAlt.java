package noppes.mpm.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

import net.minecraft.client.renderer.ImageBufferDownload;

public class ImageBufferDownloadAlt extends ImageBufferDownload
{
    private int imageData[];
    private int imageWidth;
    private int imageHeight;
    
    private boolean fix64;
    
    public ImageBufferDownloadAlt(boolean fix64){
    	this.fix64 = fix64;
    }

    @Override
    public BufferedImage parseUserSkin(BufferedImage bufferedimage){    
		imageWidth = bufferedimage.getWidth(null);
        imageHeight = bufferedimage.getHeight(null);
        BufferedImage bufferedimage1 = new BufferedImage(imageWidth, fix64?imageWidth:imageHeight, 2);
        Graphics graphics = bufferedimage1.getGraphics();
        graphics.drawImage(bufferedimage, 0, 0, null);
        float scale = imageWidth / 64f;
        
        boolean bo = fix64 && imageWidth/2 >= imageHeight;
        if (bo){
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 32, 64, 32);
        	drawImage(graphics, bufferedimage1, 24, 48, 20, 52, 4, 16, 8, 20, scale);
        	drawImage(graphics, bufferedimage1, 28, 48, 24, 52, 8, 16, 12, 20, scale);
        	drawImage(graphics, bufferedimage1, 20, 52, 16, 64, 8, 20, 12, 32, scale);
            drawImage(graphics, bufferedimage1, 24, 52, 20, 64, 4, 20, 8, 32, scale);
            drawImage(graphics, bufferedimage1, 28, 52, 24, 64, 0, 20, 4, 32, scale);
            drawImage(graphics, bufferedimage1, 32, 52, 28, 64, 12, 20, 16, 32, scale);
            drawImage(graphics, bufferedimage1, 40, 48, 36, 52, 44, 16, 48, 20, scale);
            drawImage(graphics, bufferedimage1, 44, 48, 40, 52, 48, 16, 52, 20, scale);
            drawImage(graphics, bufferedimage1, 36, 52, 32, 64, 48, 20, 52, 32, scale);
            drawImage(graphics, bufferedimage1, 40, 52, 36, 64, 44, 20, 48, 32, scale);
            drawImage(graphics, bufferedimage1, 44, 52, 40, 64, 40, 20, 44, 32, scale);
            drawImage(graphics, bufferedimage1, 48, 52, 44, 64, 52, 20, 56, 32, scale);
        }
        
        graphics.dispose();
        imageData = ((DataBufferInt)bufferedimage1.getRaster().getDataBuffer()).getData();
        if(bo){
            setAreaTransparent(imageWidth / 2, 0, imageWidth, imageHeight / 2);
        }
        return bufferedimage1;
    }
    
    private void drawImage(Graphics graphics, BufferedImage bufferedimage, int x, int y, int x2, int y2, int xx, int yy, int xx2, int yy2, float scale){
        graphics.drawImage(bufferedimage, (int)(x * scale), (int)(y * scale), (int)(x2 * scale), (int)(y2 * scale), 
        		(int)(xx * scale), (int)(yy * scale), (int)(xx2 * scale), (int)(yy2 * scale), (ImageObserver)null);
    }
    
    /**
     * Makes the given area of the image transparent if it was previously completely opaque (used to remove the outer
     * layer of a skin around the head if it was saved all opaque; this would be redundant so it's assumed that the skin
     * maker is just using an image editor without an alpha channel)
     */
    private void setAreaTransparent(int par1, int par2, int par3, int par4){
        if (!this.hasTransparency(par1, par2, par3, par4)){
            for (int i1 = par1; i1 < par3; ++i1){
                for (int j1 = par2; j1 < par4; ++j1){
                    this.imageData[i1 + j1 * this.imageWidth] &= 16777215;
                }
            }
        }
    }

    /**
     * Returns true if the given area of the image contains transparent pixels
     */
    private boolean hasTransparency(int par1, int par2, int par3, int par4){
        for (int i1 = par1; i1 < par3; ++i1){
            for (int j1 = par2; j1 < par4; ++j1){
                int k1 = this.imageData[i1 + j1 * this.imageWidth];

                if ((k1 >> 24 & 255) < 128){
                    return true;
                }
            }
        }

        return false;
    }
//        
//	 private boolean loadPlayerData(BufferedImage bufferedimage,EntityPlayer player) {
//		if(player == null)
//			return false;
//		PlayerData data = RenderMorePlayer.getPlayerData(player);
//		data.setPlayerModel(checkSkin(bufferedimage,player.username));
//		data.isChanged = true;
//		return data.model != null;
//	}
//	public static ModelData checkSkin(BufferedImage bufferedimage,String player){
//		if(bufferedimage == null)
//			return null;
//		
//		if (!new Color(bufferedimage.getRGB(0, 0), true).equals(new Color(128, 128, 128, 255))) {
//			return null;
//		}
//		ModelData data = new ModelData();
//		Color type = new Color(bufferedimage.getRGB(1, 0), true);
//		if (type.equals(new Color(255, 0, 255, 255))) {
//			data.type = EnumPlayerModelType.HUMANFEMALE;
//			data.mainModel = new ModelHumanFemale(data,0);
//			data.modelArmor = new ModelHumanFemale(data,0.3f);
//			data.modelArmorChestplate = new ModelHumanFemale(data,0.8f);
//			data.scaleX = 0.9075f;
//			data.scaleY = 0.9075f;
//			data.scaleZ = 0.9075f;
//		} else if (type.equals(new Color(128, 0, 0, 255))) {
//			data.type = EnumPlayerModelType.DWARFMALE;
//			data.mainModel = new ModelDwarfMale(data,0);
//			data.modelArmor = new ModelDwarfMale(data,0.3f);
//			data.modelArmorChestplate = new ModelDwarfMale(data,0.8f);
//			data.scaleX = 0.85f;
//			data.scaleY = 0.6875f;
//			data.scaleZ = 0.85f;
//		} else if (type.equals(new Color(255, 0, 0, 255))) {
//			data.type = EnumPlayerModelType.DWARFFEMALE;
//			data.mainModel = new ModelDwarfFemale(data,0);
//			data.modelArmor = new ModelDwarfFemale(data,0.3f);
//			data.modelArmorChestplate = new ModelDwarfFemale(data,0.8f);
//			data.scaleX = 0.75f;
//			data.scaleY = 0.6275f;
//			data.scaleZ = 0.75f;
//		} else if (type.equals(new Color(0, 128, 0, 255))) {
//			data.type = EnumPlayerModelType.ORCMALE;
//			data.mainModel = new ModelOrcMale(data,0);
//			data.modelArmor = new ModelOrcMale(data,0.3f);
//			data.modelArmorChestplate = new ModelOrcMale(data,0.8f);
//			data.scaleX = 1.2f;
//			data.scaleY = 1f;
//			data.scaleZ = 1.2f;
//		} else if (type.equals(new Color(0, 255, 0, 255))) {
//			data.type = EnumPlayerModelType.ORCFEMALE;
//			data.mainModel = new ModelOrcFemale(data,0);
//			data.modelArmor = new ModelOrcFemale(data,0.3f);
//			data.modelArmorChestplate = new ModelOrcFemale(data,0.8f);
//		} else if (type.equals(new Color(0, 0, 128, 255))) {
//			data.type = EnumPlayerModelType.FURRYMALE;
//			data.mainModel = new ModelFurryMale(data,0);
//			data.modelArmor = new ModelFurryMale(data,0.3f);
//			data.modelArmorChestplate = new ModelFurryMale(data,0.8f);
//		} else if (type.equals(new Color(0, 0, 255, 255))) {
//			data.type = EnumPlayerModelType.FURRYFEMALE;
//			data.mainModel = new ModelFurryFemale(data,0);
//			data.modelArmor = new ModelFurryFemale(data,0.3f);
//			data.modelArmorChestplate = new ModelFurryFemale(data,0.8f);
//			data.scaleX = 0.9075f;
//			data.scaleY = 0.9075f;
//			data.scaleZ = 0.9075f;
//		}else if (type.equals(new Color(255, 128, 0, 255))) {
//			data.type = EnumPlayerModelType.MONSTERMALE;
//			data.mainModel = new ModelMonsterMale(data,0);
//			data.modelArmor = new ModelMonsterMale(data,0.3f);
//			data.modelArmorChestplate = new ModelMonsterMale(data,0.8f);
//		} else if (type.equals(new Color(255, 255, 0, 255))) {
//			data.type = EnumPlayerModelType.MONSTERFEMALE;
//			data.mainModel = new ModelMonsterFemale(data,0);
//			data.modelArmor = new ModelMonsterFemale(data,0.3f);
//			data.modelArmorChestplate = new ModelMonsterFemale(data,0.8f);
//			data.scaleX = 0.9075f;
//			data.scaleY = 0.9075f;
//			data.scaleZ = 0.9075f;
//		}else if (type.equals(new Color(0,255, 128, 255))) {
//			data.type = EnumPlayerModelType.ELFMALE;
//			data.mainModel = new ModelElfMale(data,0);
//			data.modelArmor = new ModelElfMale(data,0.3f);
//			data.modelArmorChestplate = new ModelElfMale(data,0.8f);
//			data.scaleX = 0.85f;
//			data.scaleY = 1.07f;
//			data.scaleZ = 0.85f;
//		} else if (type.equals(new Color(0, 255, 255, 255))) {
//			data.type = EnumPlayerModelType.ELFFEMALE;
//			data.mainModel = new ModelElfFemale(data,0);
//			data.modelArmor = new ModelElfFemale(data,0.3f);
//			data.modelArmorChestplate = new ModelElfFemale(data,0.8f);
//			data.scaleX = 0.8f;
//			data.scaleY = 1f;
//			data.scaleZ = 0.8f;
//		} else if (type.equals(new Color(32, 32, 32, 255))) {
//			data.type = EnumPlayerModelType.ENDER;
//			data.mainModel = new ModelEnderChibi(data,0);
//			data.modelArmor = new ModelEnderChibi(data,0.3f);
//			data.modelArmorChestplate = new ModelEnderChibi(data,0.8f);
//		} 
//		else if (type.equals(new Color(128, 255, 0, 255))) {
//			data.type = EnumPlayerModelType.NAGAMALE;
//			data.mainModel = new ModelNagaMale(data,0);
//			data.modelArmor = new ModelNagaMale(data,0.3f);
//			data.modelArmorChestplate = new ModelNagaMale(data,0.8f);
//		} 
//		else if (type.equals(new Color(128, 128, 0, 255))) {
//			data.type = EnumPlayerModelType.NAGAFEMALE;
//			data.mainModel = new ModelNagaFemale(data,0);
//			data.modelArmor = new ModelNagaFemale(data,0.3f);
//			data.modelArmorChestplate = new ModelNagaFemale(data,0.8f);
//		} 
//		else if(RenderMorePlayer.superUsers.contains(player)){
//			if (type.equals(new Color(66, 66, 66, 255))){
//				data.mainModel = new ModelCrystal(data,0);
//				data.modelArmor = new ModelCrystal(data,0.3f);
//				data.modelArmorChestplate = new ModelCrystal(data,0.8f);
//			}
//			else if (type.equals(new Color(13, 13, 13, 255))){
//				data.mainModel = new ModelFail(data);
//			}
//		}
//		
//		Color sizeColor = new Color(bufferedimage.getRGB(2, 0), true);
//		int size = 3;
//		if (sizeColor.equals(new Color(255, 0, 0, 255)))
//			size = 4;
//		else if (sizeColor.equals(new Color(0, 255, 0, 255)))
//			size = 2;
//		else if (sizeColor.equals(new Color(0, 0, 255, 255)))
//			size = 1;
//		else if (sizeColor.equals(new Color(255, 0, 255, 255)))
//			size = 0;
//		data.size = size;
//		
//		Color headVissible = new Color(bufferedimage.getRGB(3, 0), true);
//		if (headVissible.equals(new Color(255, 0, 0, 255)))
//			data.showHelmet = false;
//		else if (headVissible.equals(new Color(0, 255, 0, 255)))
//			data.showHeadwear = false;
//		else if (headVissible.equals(new Color(255, 255, 0, 255))){
//			data.showHeadwear = false;
//			data.showHelmet = false;
//		}
//		
//		
//		int extraOption1 = getValue(new Color(bufferedimage.getRGB(7, 0), true));
//		data.mainModel.setExtraOption1(extraOption1);
//		data.modelArmorChestplate.setExtraOption1(extraOption1);
//		data.modelArmor.setExtraOption1(extraOption1);
//		
//		int extraOption2 = getValue(new Color(bufferedimage.getRGB(7, 1), true));
//		data.mainModel.setExtraOption2(extraOption2);
//		data.modelArmorChestplate.setExtraOption2(extraOption2);
//		data.modelArmor.setExtraOption2(extraOption2);
//		
//		int extraOption3 = getValue(new Color(bufferedimage.getRGB(7, 2), true));
//		data.mainModel.setExtraOption3(extraOption3);
//		data.modelArmorChestplate.setExtraOption3(extraOption3);
//		data.modelArmor.setExtraOption3(extraOption3);
//
//		Color color = new Color(bufferedimage.getRGB(7, 3), true);
//		data.mainModel.setExtraColor(color);
//
//		Color claws = new Color(bufferedimage.getRGB(0, 1), true);
//		if (claws.equals(new Color(255, 0, 0, 255)))
//			((ModelInterface)data.mainModel).addClaws();
//
//		data.headSize = getValue(new Color(bufferedimage.getRGB(1, 1), true));
//		
//		data.bellySize = getValue(new Color(bufferedimage.getRGB(2, 1), true));
//		return data;
//	}
//	private static int getValue(Color color){
//		if (color.equals(new Color(255, 0, 0, 255)))
//			return 1;
//		else if (color.equals(new Color(0, 255, 0, 255)))
//			return 2;
//		else if (color.equals(new Color(0, 0, 255, 255)))
//			return 3;
//		else if (color.equals(new Color(255, 0, 255, 255)))
//			return 4;
//		return 0;
//	}
}
