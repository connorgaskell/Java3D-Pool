package gaskell.scene;

import gaskell.Constants;

import javax.media.j3d.*;
import javax.vecmath.*;

/*
 * @author Connor Gaskell
 * @description Creates lighting for the scene, Ambient or Directional
 * References: Killer Game Programming in Java (Andrew Davison)
 *             The Java 3D API Specification (Henry Sowizral, Kevin Rushforth and Michael Deering)
 *             Java 3D API Jump-Start (Aaron E. Walsh and Doug Gehringer)
 *             Java 3D Programming (Daniel Selman)
 */

public class Lighting {

    /*
     * Ambient lighting, lights the entire scene with the specified colour.
     */
    public AmbientLight createAmbientLight(Color3f lightColour) {
        // Creates AmbientLight instance, passing a Color3f parameter.
        AmbientLight ambientLight = new AmbientLight(lightColour);

        // Sets the influencing bounds of the behavior, set to default bounds defined in Constants class.
        ambientLight.setInfluencingBounds(Constants.BOUNDS);

        // Sets the capabilities of the behavior
        ambientLight.setCapability(Light.ALLOW_STATE_WRITE);

        // Returns the created AmbientLight
        return ambientLight;
    }

    /*
     * Directional Lighting, lights the scene from a specific direction with a specific colour, acts like a light source from a star/sun.
     */
    public DirectionalLight createDirectionalLight(Vector3f lightDirection, Color3f lightColour) {
        // Creates the DirectionalLight instance, passing a colour as Color3f and direction as Vector3d.
        DirectionalLight directionalLight = new DirectionalLight(lightColour, lightDirection);

        // Sets the influencing bounds of the behavior, set to default bounds defined in Constants class.
        directionalLight.setInfluencingBounds(Constants.BOUNDS);

        // Sets the capabilities of the behavior
        directionalLight.setCapability(Light.ALLOW_STATE_WRITE);

        // Returns the created DirectionalLight
        return directionalLight;
    }

}
