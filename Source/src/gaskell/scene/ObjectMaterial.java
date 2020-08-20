package gaskell.scene;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.*;

/*
 * @author Connor Gaskell
 * @description Creates an Appearance for an object based on a colour or texture
 * References: Java 3D Programming (Daniel Selman)
 *             https://download.java.net/media/java3d/javadoc/1.3.2/javax/media/j3d/Appearance.html
 */

public class ObjectMaterial {

    /*
     * Creates an appearance with an applied texture.
     * Passes various Color3f parameters for ambient colour, emission colour, diffuse colour, specular colour, a float for light intensity and a String for the texture path.
     */
    public Appearance createAppearance(Color3f ambientColour, Color3f emissionColour, Color3f diffuseColor, Color3f specularColor, float lightIntensity, String texture) {
        // Create the Material using the passed parameters
        Material material = new Material(ambientColour, emissionColour, diffuseColor, specularColor, lightIntensity);

        // Enable lighting for the material
        material.setLightingEnable(true);

        // Create the Appearance
        Appearance appearance = new Appearance();

        // Set the material of the Appearance to the previously created material
        appearance.setMaterial(material);

        /*
         * Use a TextureLoader to load the texture from the specified path
         * Set the texture of the Appearance to the texture
         */
        try {
            TextureLoader objectTexture = new TextureLoader(texture, null);
            appearance.setTexture(objectTexture.getTexture());
        } catch(NullPointerException e) { }

        // Return the created Appearance
        return appearance;
    }

    /*
     * Creates an appearance without an applied texture, calls the other createAppearance method which requires the texture parameter however 'null' is passed for the texture.
     * Passes various Color3f parameters for ambient colour, emission colour, diffuse colour, specular colour and a float for light intensity.
     */
    public Appearance createAppearance(Color3f ambientColour, Color3f emissionColour, Color3f diffuseColor, Color3f specularColor, float lightIntensity) {
        // Create the Appearance, passes the ambient colour, emission colour, diffuse colour, specular colour and light intensity.
        Appearance appearance = createAppearance(ambientColour, emissionColour, diffuseColor, specularColor, lightIntensity, null);

        // Return the created Appearance
        return appearance;
    }
}
