package gaskell.scene.objects.behaviours;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import gaskell.Constants;
import gaskell.Game;

import javax.media.j3d.*;
import javax.vecmath.Point3d;

/*
 * @author Connor Gaskell
 * @description Is an extension to PickMouseBehavior, finds the mouse position in the 3D space
 * References: Killer Game Programming in Java (Andrew Davison)
 */

public class Pick extends PickMouseBehavior {

    /*
     * Constructor, passes the Canvas3D, branch group and bounds
     */
    public Pick(Canvas3D canvas3D, BranchGroup branchGroup, Bounds bounds) {
        super(canvas3D, branchGroup, bounds);

        // Sets the scheduling bounds
        setSchedulingBounds(Constants.BOUNDS);

        // Set the mode of the PickCanvas
        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
    }


    /*
     * Inherits the updateScene method from PickMouseBehavior
     */
    @Override
    public void updateScene(int xPos, int yPos) {
        /*
         * Detects the point of intersection of the pickCanvas, if the PickResult is not null...
         * The mouseX and mouseY variables are set to the point of intersection
         */
        pickCanvas.setShapeLocation(xPos, yPos);

        Point3d eyePos = pickCanvas.getStartPosition();

        PickResult pickResult;
        pickResult = pickCanvas.pickClosest();

        if(pickResult != null) {
            PickIntersection pickIntersection = pickResult.getClosestIntersection(eyePos);
            Point3d intercept = pickIntersection.getPointCoordinatesVW();

            // Set the mouseX and mouseY variables
            Game.mouseX = intercept.x;
            Game.mouseY = intercept.z;
        }
    }

}
