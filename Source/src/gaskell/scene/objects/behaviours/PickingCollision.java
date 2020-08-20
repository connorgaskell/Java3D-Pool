package gaskell.scene.objects.behaviours;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import gaskell.Constants;
import gaskell.scene.objects.GameObject;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import java.util.Enumeration;

/*
 * @author Connor Gaskell
 * @description Collision detection using a PickTool to detect intersection, this class is not used in the final product.
 * References: Killer Game Programming in Java (Andrew Davison)
 */

public class PickingCollision extends Behavior {

    // The criteria which will activate the behavior
    private WakeupCriterion[] wakeupCriterion;

    // A WakeupOr which will hold all of the WakeupCriterion
    private WakeupOr wakeupOr;

    // GameObject for the colliding object
    private GameObject collisionObject;

    // Objects BranchGroup
    private BranchGroup objectBranchGroup;

    // The TransformGroup of the object
    private TransformGroup objectTransformGroup;

    // PickBounds
    private PickBounds pickBounds = null;

    /*
     * Constructor, passes the branch group, collision game object and transform group
     */
    public PickingCollision(BranchGroup objectBranchGroup, GameObject collisionObject, TransformGroup objectTransformGroup) {
        this.objectBranchGroup = objectBranchGroup;
        this.collisionObject = collisionObject;
        this.objectTransformGroup = objectTransformGroup;

        // Set the scheduling bounds
        setSchedulingBounds(Constants.BOUNDS);
    }

    /*
     * Behaviour initialise method
     */
    @Override
    public void initialize() {
        wakeupCriterion = new WakeupCriterion[1];
        wakeupCriterion[0] = new WakeupOnElapsedFrames(0);

        wakeupOr = new WakeupOr(wakeupCriterion);
        wakeupOn(wakeupOr);
    }

    // When a collider enters...
    private void onCollisionEnter() {

    }

    // When a collider exits...
    private void onCollisionExit() {

    }

    /*
     * Checks for collision from the PickResults array
     */
    public boolean isCollision(PickResult[] pickResults) {
        // Ensure PickResults is not null
        if(pickResults == null || pickResults.length == 0) return false;

        /*
         * Get the PickResults elements, if there is a colliding object which isn't this object, return true.
         */
        for(int n = 0; n < pickResults.length; n++) {
            Object userData = pickResults[n].getObject().getUserData();

            if(pickResults[n].getObject().getUserData() != null) {
                if(!userData.equals(collisionObject.name)) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Behaviour processStimulus method
     */
    @Override
    public void processStimulus(Enumeration criteria) {
        while(criteria.hasMoreElements()) {
            WakeupCriterion wakeupCriterion = (WakeupCriterion) criteria.nextElement();

            // Runs on every frame, checks if a collision has occurred..
            if (wakeupCriterion instanceof WakeupOnElapsedFrames) {
                // Create a PickTool
                PickTool pickTool = new PickTool(objectBranchGroup);
                pickTool.setMode(PickTool.BOUNDS);

                // Create a BoundingSphere for the object
                BoundingSphere bounds = (BoundingSphere) collisionObject.bounds.getBounds();

                // Create a PickBounds from the BoundingSphere
                pickBounds = new PickBounds(new BoundingSphere(new Point3d(collisionObject.movement.positionVector), bounds.getRadius() / 1.5f));

                // Set the PickTool shape to the PickBounds
                pickTool.setShape(pickBounds, new Point3d(0, 0, 0));

                /*
                 * Check for a collision using pickAll()
                 */
                PickResult[] pickResults = pickTool.pickAll();
                // If there is a collision call onCollisionEnter...
                if (isCollision(pickResults)) {
                    onCollisionEnter();
                } else if(!isCollision(pickResults)) {
                    onCollisionExit();
                }

            }
        }
        wakeupOn(wakeupOr);
    }

}
