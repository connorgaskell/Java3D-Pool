package gaskell.scene.objects.behaviours;

import gaskell.Constants;
import gaskell.Game;
import gaskell.scene.objects.GameObject;

import javax.media.j3d.*;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3f;
import java.util.Enumeration;

/*
 * @author Connor Gaskell
 * @description Controls the movement of all non-static GameObjects
 * References: http://www.cs.stir.ac.uk/courses/ITNP3B/Java3D/Tutorial/j3d_tutorial_ch4.pdf
 */

public class Movement extends Behavior {

    // The criteria which will activate the behavior
    private WakeupCriterion[] wakeupCriterion;

    // A WakeupOr which will hold all of the WakeupCriterion
    private WakeupOr wakeupOr;

    // The GameObject
    public GameObject gameObject;

    // Vector for the position
    public Vector3f positionVector;

    // Vector for the velocity
    public Vector3f velocity;

    // Vector for the spin velocity
    public Vector3f spinVelocity = new Vector3f();

    /*
     * Constructor passes the GameObject and the current velocity
     */
    public Movement(GameObject gameObject, Vector3f velocity) {
        this.gameObject = gameObject;
        this.positionVector = gameObject.getPosition();
        this.velocity = velocity;

        // Set the scheduling bounds
        setSchedulingBounds(Constants.BOUNDS);
    }

    /*
     * moveObject method, handles all of the movement of the object.
     */
    private void moveObject() {
        // Get the GameObjects transform
        Transform3D transform3D = gameObject.getTransform(gameObject.position);

        // Add the positionVector and the velocity vector
        positionVector.add(velocity);

        /*
         * If the objects position should be reset, then reset the velocity, the position to the origin and set the object active.
         */
        if(gameObject.resetPosition) {
            spinVelocity = new Vector3f(0.0f, 0.0f, 0.0f);
            velocity = new Vector3f(0.0f, 0.0f, 0.0f);
            positionVector.set(new Vector3f(gameObject.originPosition.x, 0.0f, gameObject.originPosition.z));
            gameObject.objectActive = true;
            gameObject.resetPosition = false;
        }

        // If the white ball has fallen off the table, reset it.
        if(gameObject.name.equals("WHITE 0") && positionVector.y < -0.35f) {
            gameObject.resetPosition = true;
            Game.time += 10;
        }

        //If any ball except the white ball has fallen off the table, set the position to -10f to hide it.
        if(!gameObject.name.equals("WHITE 0") && positionVector.y < -0.35f) {
            positionVector.y = -10f;
        }

        // Quick hack to fix a potential error if the positionVectors Y-Axis is greater than 0.02f
        if(positionVector.y > 0.02f || (positionVector.y < 0 && !gameObject.name.equals("WHITE 0") && !Game.pocketedBalls.contains(gameObject))) {
            positionVector.y = 0;
        }

        // Set the transform to the positionVector
        transform3D.setTranslation(positionVector);

        // Finally set the GameObjects position to the transform created
        gameObject.position.setTransform(transform3D);

        // Set the rotation based on the spinVelocity variable using a Quaternion.
        gameObject.setRotation(new Quat4d(spinVelocity.z, spinVelocity.y, -spinVelocity.x, 0.3f));
    }

    /*
     * Applies linear friction to the moving GameObject.
     */
    private void applyFriction() {
        /*
         * Use the signum function to determine whether the value is postive/negative, as next the max function is used to determine the larger of the two integers (0 and the velocity during the next frame).
         * Using the abs function to use the absolute value of the velocity, which is the reason this value is multiplied by the returned value of the signum function.
         */
        velocity.x = Math.signum(velocity.x) * Math.max(0, Math.abs(velocity.x) - 0.0005f);
        velocity.z = Math.signum(velocity.z) * Math.max(0, Math.abs(velocity.z) - 0.0005f);

        /*
         * Same as above except for the spinVelocity.
         */
        spinVelocity.x = Math.signum(spinVelocity.x) * Math.max(0, Math.abs(spinVelocity.x) - 0.0001f);
        spinVelocity.y = Math.signum(spinVelocity.y) * Math.max(0, Math.abs(spinVelocity.y) - 0.0001f);
        spinVelocity.z = Math.signum(spinVelocity.z) * Math.max(0, Math.abs(spinVelocity.z) - 0.0001f);
    }

    /*
     * Behaviour initialise method
     */
    @Override
    public void initialize() {
        wakeupCriterion = new WakeupCriterion[2];
        wakeupCriterion[0] = new WakeupOnElapsedFrames(0);
        wakeupCriterion[1] = new WakeupOnElapsedTime(1);

        wakeupOr = new WakeupOr(wakeupCriterion);
        wakeupOn(wakeupOr);
    }

    /*
     * Behaviour processStimulus method
     */
    @Override
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeupCriterion = (WakeupCriterion) criteria.nextElement();

        // If the criteria is met, call the methods
        if(wakeupCriterion instanceof WakeupOnElapsedFrames) {
            applyFriction();
            moveObject();
        } else if(wakeupCriterion instanceof WakeupOnElapsedTime) {
            applyFriction();
        }

        wakeupOn(wakeupOr);
    }

}
