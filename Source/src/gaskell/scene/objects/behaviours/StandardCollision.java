package gaskell.scene.objects.behaviours;

import gaskell.Constants;
import gaskell.scene.objects.primitives.Ball;

import javax.media.j3d.*;
import java.util.Enumeration;

/*
 * @author Connor Gaskell
 * @description Handles collisions using the Collision criteria in Java3D, this class is not used in the final product.
 * References: https://docs.oracle.com/cd/E17802_01/j2se/javase/technologies/desktop/java3d/forDevelopers/J3D_1_3_API/j3dapi/javax/media/j3d/WakeupOnCollisionEntry.html
 */

public class StandardCollision extends Behavior {

    private Ball gameObject;

    // The criteria which will activate the behavior
    private WakeupCriterion[] wakeupCriteria;

    // A WakeupOr which will hold all of the WakeupCriterion
    private WakeupOr orCriteria;

    // The Shape3D for the colliding object
    private Shape3D collidingShape;

    /*
     * Constructor, passes the current GameObject and the collidingShape
     */
    public StandardCollision(Ball gameObject, Shape3D collidingShape) {
        this.gameObject = gameObject;
        this.collidingShape = collidingShape;
        setSchedulingBounds(Constants.BOUNDS);
    }

    // When a collider enters...
    public void onCollisionEnter(Node node) {

    }

    // When a collider exits...
    public void onCollisionExit(Node node) {

    }

    // When a collider moves whilst colliding...
    public void onCollisionMovement(Node node) {

    }

    /*
     * Behaviour initialise method
     */
    @Override
    public void initialize() {
        // Collisions have criteria for entry, exit and movement
        wakeupCriteria = new WakeupCriterion[3];
        wakeupCriteria[0] = new WakeupOnCollisionEntry(collidingShape);
        wakeupCriteria[1] = new WakeupOnCollisionExit(collidingShape);
        wakeupCriteria[2] = new WakeupOnCollisionMovement(collidingShape);

        // Set the criteria
        orCriteria = new WakeupOr(wakeupCriteria);
        wakeupOn(orCriteria);
    }

    /*
     * Behaviour processStimulus method
     */
    @Override
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeupCriteria = (WakeupCriterion) criteria.nextElement();

        /*
         * If one of the criteria is met, get the node and pass it to the onCollisionEnter/Exit/Movement method.
         */
        if(wakeupCriteria instanceof WakeupOnCollisionEntry) {
            Node node = ((WakeupOnCollisionEntry) wakeupCriteria).getTriggeringPath().getObject();
            if(node.getUserData() != null) onCollisionEnter(node);
            else if(node.getParent().getUserData() != null) onCollisionEnter(node);
        } else if(wakeupCriteria instanceof WakeupOnCollisionExit) {
            Node node = ((WakeupOnCollisionExit) wakeupCriteria).getTriggeringPath().getObject();
            if(node.getUserData() != null) onCollisionExit(node);
            else if(node.getParent().getUserData() != null) onCollisionExit(node);
        } else if(wakeupCriteria instanceof WakeupOnCollisionMovement) {
            Node node = ((WakeupOnCollisionMovement) wakeupCriteria).getTriggeringPath().getObject();
            if(node.getUserData() != null) onCollisionMovement(node);
            else if(node.getParent().getUserData() != null) onCollisionMovement(node);
        }

        wakeupOn(orCriteria);
    }

}
