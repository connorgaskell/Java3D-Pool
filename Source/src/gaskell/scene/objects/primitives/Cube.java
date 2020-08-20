package gaskell.scene.objects.primitives;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.picking.PickTool;
import gaskell.scene.objects.GameObject;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;

/*
 * @author Connor Gaskell
 * @description Creates a cube object
 */

public class Cube extends GameObject {

    // Shape3D for the cube
    public Shape3D cubeObject;

    /*
     * The constructor passes the size on each axis, appearance, branch group and name.
     */
    public Cube(float sizeX, float sizeY, float sizeZ, Appearance material, BranchGroup branchGroup, String cubeName) {
        // Create a new Box (Cube)
        cubeObject = new Box(sizeX, sizeY, sizeZ, material).getShape(Box.TOP);

        // Add the Shape3D to the bounds
        bounds.addChild(cubeObject.getParent());

        // Set the name
        this.name = cubeName;

        // Set the user data
        cubeObject.setUserData(this.name);

        // Add the cube to the BranchGroup
        branchGroup.addChild(gameObject);
    }

}
