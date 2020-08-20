package gaskell.scene.objects.primitives;

import com.sun.j3d.utils.geometry.*;
import gaskell.scene.objects.GameObject;
import gaskell.scene.objects.behaviours.BallCollision;
import gaskell.scene.objects.behaviours.Movement;

import javax.media.j3d.*;
import javax.vecmath.Vector3f;

/*
 * @author Connor Gaskell
 * @description Creates a pool ball object
 */

public class Ball extends GameObject {

    // Shape3D for the ball
    public Shape3D ballObject;

    /*
     * The constructor passes the position, rotation, scale, material, branch group, the number (in relation to the pool ball), type of pool ball and whether it is static
     */
    public Ball(Vector3f pos, Vector3f rot, float scale, Appearance material, BranchGroup branchGroup, int number, String type, boolean isStatic) {
        // Create a Sphere and store it into the ballObject variable
        ballObject = new Sphere(scale, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, 50).getShape();

        // Set the appearance of the ball
        ballObject.setAppearance(material);

        // Add the Shape3D to the bounds TransformGroup
        bounds.addChild(ballObject.getParent());

        // Name of the ball
        this.name = type + " " + number;

        // Set the user data of the object
        ballObject.setUserData(this.name);

        // Starting position of the ball
        originPosition = pos;

        // Set the user data of the gameObject
        setUserData(this.name);

        // Set pickable to false
        setPickable(false);

        // Set the position
        setPosition(pos);

        // Set the ball as active
        objectActive = true;

        /*
         * If a ball is static then the Movement behaviour is not added to the ball, neither is the BallCollision behaviour
         */
        if (!isStatic) {
            // Add the Movement behaviour to the ball
            movement = new Movement(this, new Vector3f(0.0f, 0.0f, 0.0f));
            branchGroup.addChild(movement);

            // Add the BallCollision behaviour to the ball
            BallCollision physics = new BallCollision(this);
            branchGroup.addChild(physics);
        }

        // Add the gameObject to the BranchGroup
        branchGroup.addChild(gameObject);
    }

}
