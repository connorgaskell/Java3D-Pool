package gaskell.scene;

import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.universe.*;

import gaskell.Constants;

import javax.media.j3d.Canvas3D;
import javax.vecmath.Point3d;

/*
 * @author Connor Gaskell
 * @description Creates a Orbit camera which can be controlled by the player
 * References: https://download.java.net/media/java3d/javadoc/1.3.2/com/sun/j3d/utils/behaviors/vp/OrbitBehavior.html
 */

public class Camera {

    /*
     * Uses the OrbitBehavior included with Java3D, orbits the centre of the pool table.
     */
    public void createOrbitCamera(SimpleUniverse universe, Canvas3D canvas) {
        // Creates the OrbitBehaviour instance passing parameters for the 3D Canvas and the capabilities of the behavior
        OrbitBehavior orbitBehavior = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_TRANSLATE | OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.STOP_ZOOM | OrbitBehavior.DISABLE_TRANSLATE);

        // Minimum radius of the orbit
        orbitBehavior.setMinRadius(4.0f);

        // Rotation factors for the camera, X-Axis set to 0.0 to stop rotation on that axis.
        orbitBehavior.setRotFactors(0.0f, 0.35f);

        // Centre point of the rotation
        orbitBehavior.getRotationCenter(new Point3d(0.0f, 15.5f, 0.0f));

        // Bounds of the behavior, set to the default bounds defined in Constants class.
        orbitBehavior.setSchedulingBounds(Constants.BOUNDS);

        // Creates ViewingPlatform instance
        ViewingPlatform viewingPlatform = universe.getViewingPlatform();

        // Add the OrbitBehavior to the ViewingPlatform
        viewingPlatform.setViewPlatformBehavior(orbitBehavior);
    }

}
