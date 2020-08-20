package gaskell.scene.objects.primitives;

import gaskell.scene.objects.GameObject;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/*
 * @author Connor Gaskell
 * @description Create a plane, mostly used for representing the ground in the game.
 */

public class Plane extends GameObject {

    // String for the plane Name
    public String planeName;

    /*
     * Constructor passes the position, rotation, scale on the X and Z axis, colour, branch group and the name.
     */
    public Plane(Vector3f pos, Vector3f rot, float scaleX, float scaleZ, Color3f color, BranchGroup branchGroup, String planeName) {
        // Create the plane and store it in a Shape3D
        Shape3D plane = createPlane(pos.x, pos.y, pos.z, scaleX, scaleZ);

        /*
         * Create the planes appearance.
         */
        Appearance planeAppearance = new Appearance();
        planeAppearance.getRenderingAttributes();
        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);
        planeAppearance.setPolygonAttributes(polygonAttributes);
        ColoringAttributes planeCA = new ColoringAttributes(color, 1);
        planeAppearance.setColoringAttributes(planeCA);

        // Set the appearance
        plane.setAppearance(planeAppearance);

        // Set the name
        this.name = planeName;

        // Set the user data
        plane.setUserData(this.name);

        // Add the Shape3D to the bounds TransformGroup
        bounds.addChild(plane);

        // Add the gameObject to the BranchGroup
        branchGroup.addChild(gameObject);
    }

    /*
     * Creates a plane made up of a QuadArray
     * Passes the X, Y and Z position and scale on the X and Z axis
     */
    public Shape3D createPlane(float posX, float posY, float posZ, float scaleX, float scaleZ) {
        // Create a Quad with 4 vertices
        QuadArray quadArray = new QuadArray(4, QuadArray.COORDINATES);

        /*
         * Set the coordinates of each of the vertices for the Quad
         */
        quadArray.setCoordinate(0, new Point3f(-scaleX + posX, posY, scaleZ + posZ));
        quadArray.setCoordinate(1, new Point3f(scaleX + posX, posY, scaleZ + posZ));
        quadArray.setCoordinate(2, new Point3f(scaleX + posX, posY, -scaleZ + posZ));
        quadArray.setCoordinate(3, new Point3f(-scaleX + posX, posY, -scaleZ + posZ));

        // Return the Quad
        return new Shape3D(quadArray);
    }

}
