package gaskell;

import javax.media.j3d.*;
import javax.vecmath.*;

/*
 * @author Connor Gaskell
 * @description Contains some final variables which may need to be called multiple times
 */

public class Constants {

    // Set the title of the application
    public static final String GAME_NAME = "Advanced Programming CW2 - 3D Pool Game - Connor Gaskell 23091622";

    // Default bounds for Java3D, set to a huge value.
    public static final BoundingSphere BOUNDS = new BoundingSphere(new Point3d(0, 0, 0), 1e100);

}
