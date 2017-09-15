package noppes.mpm;

import javafx.scene.paint.Material;
import net.minecraft.block.BlockContainer;

/**
 * Created by Alex Nava on 9/12/2017.
 */
public class CerealModel extends BlockContainer{

public CerealModel(Material material) {
        super(material);

        this.setBlockName("Cereal Box");
        }

@Override
public boolean renderAsNormalBlock(){
        return false;
        }

@Override
public int getRenderType(){
        return -1;
        }

@Override
public boolean isOpaqueCube(){
        return false;
        }

@Override
public TileEntity createNewTileEntity(World world, int par2) {
        return new TileEntityCerealModel();
        }
        }
