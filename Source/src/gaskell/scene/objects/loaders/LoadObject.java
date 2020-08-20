package gaskell.scene.objects.loaders;

import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import gaskell.scene.objects.GameObject;

import javax.media.j3d.*;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3f;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

/*
 * @author Connor Gaskell
 * @description Loads an .obj file using the inbuilt object loader in Java3D
 * References: Killer Game Programming in Java (Andrew Davison)
 *             Java 3D Programming (Daniel Selman)
 *             Java 3D API Jump-Start (Aaron E. Walsh and Doug Gehringer)
 */

public class LoadObject extends GameObject {

    // Shape3D for storing the loaded object as a Shape3D
    public Shape3D shape;

    // String for the name of the shape
    public String shapeName;

    /*
     * Constructor which passes a position vector, scale, appearance, the BranchGroup to add it to, name of the object and the directory of the object
     */
    public LoadObject(Vector3f pos, float scale, Appearance material, BranchGroup branchGroup, String name, String objFile) {
        // Create the Loader passing the parameter to resize the object
        ObjectFile objLoader = new ObjectFile(ObjectFile.RESIZE);

        Scene scene = null;

        /*
         * Load the object using the objLoader passing the directory, store the object into the Scene.
         */
        try {
            File file = new File(objFile);
            scene = objLoader.load(file.toURI().toURL());
        } catch (ParsingErrorException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Create a temporary BranchGroup equal to the contents of the Scene
        BranchGroup tempGroup = scene.getSceneGroup();

        /*
         * Set some capabilities for this group
         */
        tempGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        tempGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        tempGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        tempGroup.setCapability(BranchGroup.ALLOW_DETACH);

        // Get the only child of the temporary BranchGroup and cast Shape3D, storing it into the shape variable
        shape = (Shape3D)tempGroup.getChild(0);

        /*
         * Set the scale using a Transform3D
         */
        Transform3D scaleTransform = new Transform3D();
        scaleTransform.setScale(scale);
        bounds.setTransform(scaleTransform);

        // Add the Shape3D to the bounds TransformGroup
        bounds.addChild(shape.getParent());

        // Name the shape
        shapeName = name;

        // Set the name as the UserData of the object
        shape.setUserData(shapeName);

        /*
         * Set the rotation and position of the object
         */
        setRotation(new Quat4d(0, 90, 0, 90));
        setPosition(pos);

        // Add the object to the permanent BranchGroup
        branchGroup.addChild(gameObject);
    }

}
