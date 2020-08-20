package gaskell;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;
import gaskell.scene.*;
import gaskell.scene.objects.behaviours.*;
import gaskell.scene.objects.loaders.LoadObject;
import gaskell.scene.objects.primitives.*;


import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.util.ArrayList;

/*
 * @author Connor Gaskell
 * @description This class handles most of the setup for the game, creates BranchGroups, objects, etc.
 */

public class Game extends JPanel {

    // BranchGroups which will contain other various objects within the SceneGraph
    private BranchGroup rootGroup, mapGroup, skyGroup, fogGroup, behaviourGroup;

    // ArrayList for all of the balls on the table
    public static ArrayList<Ball> ballRack = new ArrayList<>();

    // ArrayList for all of the pockets on the table
    public static ArrayList<Ball> tablePockets = new ArrayList<>();

    // ArrayList for all of the pocketed balls
    public static ArrayList<Ball> pocketedBalls = new ArrayList<>();

    // Boolean for whether the game has started
    public static boolean gameStarted = false;

    // Boolean for whether the game has ended
    public static boolean gameEnded = false;

    // Integer for the time the current game has been running
    public static int time = 0;

    // Doubles for the current X, Y coordinates of the mouse
    public static double mouseX = 0, mouseY = 0;

    // Boolean for whether the player can hit the white ball
    public static boolean canShoot = true;

    // Ball for the white ball
    public static Ball whiteBall;

    // Vector3f for the power the white ball will be hit at
    public static Vector3f whiteBallPower = new Vector3f();

    /*
     * Constructor, creates the Canvas3D, the UI and the 3D Universe.
     */
    public Game() {
        // Set the layout to BorderLayout
        setLayout(new BorderLayout());

        // Create a new GraphicsConfigTemplate3D
        GraphicsConfigTemplate3D configTemplate3D = new GraphicsConfigTemplate3D();

        // Set the scenes Antialiasing to preferred
        configTemplate3D.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);

        // Create a GraphicsConfiguration, set to the default best configuration
        GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(configTemplate3D);

        // Create a Canvas3D, this also contains the code for rendering the 2D UI
        Canvas3D canvas = new Canvas3D(config) {
            // Graphics2D Object
            Graphics2D g = this.getGraphics2D();

            /*
             * The postRender method is used for drawing the overlayed UI on the Canvas3D
             */
            public void postRender() {
                /*
                 * Get and store the screen width and height
                 */
                Dimension dimensions = this.getSize();
                double screenWidth = dimensions.getWidth();
                double screenHeight = dimensions.getHeight();

                // Set the drawing colour to white
                g.setColor(Color.WHITE);

                /*
                 * Calculations for the timer, creates a timer and using String formatting outputs in the format minutes : seconds
                 */
                int minutes = time / 60;
                int seconds = time - (minutes * 60);
                g.drawString("TIME: " + String.format("%d:%02d", minutes, seconds) + "   |   POTTED: " + pocketedBalls.size(),(int)(screenWidth / 2) - 65, 15);

                /*
                 * Draws information relevant to the player.
                 */
                g.drawString("-- INFORMATION --",(int)(screenWidth / 2) - 45, (int)(screenHeight) - 20);
                g.drawString("HIT BALL - RIGHT MOUSE   |   ROTATE CAMERA - LEFT MOUSE   |   ZOOM - SCROLL MIDDLE MOUSE   |   RESTART - 'R' KEY",(int)(screenWidth / 2) - 350, (int)(screenHeight) - 5);

                /*
                 * If the game has ended, draw the game over UI.
                 */
                if(gameEnded) {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, ((int) (screenHeight) / 2) - 27, (int) (screenWidth), 45);
                    g.setColor(Color.WHITE);
                    g.drawString("CONGRATULATIONS, ALL BALLS HAVE BEEN POTTED - PRESS 'R' TO PLAY AGAIN", (int) (screenWidth / 2) - 215, (int) (screenHeight) / 2);
                }

                // Disable Graphics2D flush
                this.getGraphics2D().flush(false);

                // Redraws the UI so it doesn't disappear after the first frame
                revalidate();
            }
        };

        /*
         * Set the canvas as focusable and request focus, this is used for the KeyListener which is then added to the canvas.
         */
        canvas.setFocusable(true);
        canvas.requestFocus();
        canvas.addKeyListener(new KeyInputHandler());

        // Create the SimpleUniverse, this is at the top of a SceneGraph
        SimpleUniverse universe = new SimpleUniverse(canvas);

        // Initalise the fogGroup
        fogGroup = new BranchGroup();

        // Initialise the behaviourGroup
        behaviourGroup = new BranchGroup();

        // Call the createFog() method
        createFog();

        // Call the createScene() method, passing the canvas
        createScene(canvas);

        // Add the canvas to the Frame
        add("Center", canvas);

        // Get the Viewer from the universe
        Viewer viewer = universe.getViewer();

        // Get the View from the Viewer
        View view = viewer.getView();

        // Set the rendering distance to 300
        view.setBackClipDistance(300.0f);

        // Enable Antialiasing for removing jagged edges
        view.setSceneAntialiasingEnable(true);

        // Enables Depth Buffer freezing
        view.setDepthBufferFreezeTransparent(true);

        // Set the Transparency Sorting Policy to perspective projection
        view.setTransparencySortingPolicy(View.PERSPECTIVE_PROJECTION);

        // Set the Screen Scale Policy
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);

        // Set the Projection Policy
        view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);

        // Set the Window Eyepoint Policy
        view.setWindowEyepointPolicy(View.RELATIVE_TO_FIELD_OF_VIEW);

        // Set the Field of View
        view.setFieldOfView(1.5f);

        /*
         * Create a new Camera object and call createOrbitCamera
         */
        Camera camera = new Camera();
        camera.createOrbitCamera(universe, canvas);

        // Set Nominal Viewing Transform
        universe.getViewingPlatform().setNominalViewingTransform();

        // Create a Transform for the position of the View
        Transform3D viewPosTransform = new Transform3D();

        // Set the Transform to the Vector
        viewPosTransform.set(new Vector3f(0.0f, 0.0f, 6.0f));

        // Create a Transform for the rotation of the View
        Transform3D viewRotTransform = new Transform3D();

        // Set the Transform to the Quaternion
        viewRotTransform.setRotation(new Quat4d(1.0f, 0.0f, 0.0f, -1.0f));

        // Combine the rotation Transform and position Transform
        viewRotTransform.mul(viewPosTransform);

        // Set the View Transform to the combined Transform
        universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewRotTransform);

        // Add the rootGroup to the universe
        universe.addBranchGraph(rootGroup);
    }

    /*
     * createScene method, creates
     */
    public void createScene(Canvas3D canvas) {
        // Create the rootGroup and the mapGroup
        rootGroup = new BranchGroup();
        mapGroup = new BranchGroup();

        /*
         * Set the capabilities for the rootGroup and the mapGroup
         */
        rootGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        rootGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
        rootGroup.setCapability(BranchGroup.ALLOW_DETACH);
        mapGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        mapGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
        mapGroup.setCapability(Group.ALLOW_CHILDREN_READ);
        mapGroup.setCapability(BranchGroup.ALLOW_DETACH);

        // Create the Lighting
        Lighting sceneLight = new Lighting();
        //mapGroup.addChild(sceneLight.createAmbientLight(new Color3f(0.3f, 0.3f, 0.3f)));

        // Add a directional light to the scene
        mapGroup.addChild(sceneLight.createDirectionalLight(new Vector3f(0.65f, -1.65f, -0.4f), new Color3f(1.5f, 1.5f, 1.5f)));

        // Add the Pick behaviour for mouse position to the mapGroup
        mapGroup.addChild(new Pick(canvas, mapGroup, Constants.BOUNDS));

        // Add the GameTimer behaviour to the mapGroup
        mapGroup.addChild(new GameTimer());

        // Call the createBackgroundSphere method
        createBackgroundSphere();

        // Create an Appearance for the table
        Appearance tableAppearance = new ObjectMaterial().createAppearance(new Color3f(0.3f, 0.3f, 0.3f), new Color3f(0.0f, 0.0f, 0.0f), new Color3f(1.0f, 1.0f, 1.0f), new Color3f(1.0f, 1.0f, 1.0f), 10.0f);

        // Create and appearance for the transparent pocket colliders
        Appearance transparentAppearance = new ObjectMaterial().createAppearance(new Color3f(0.3f, 0.3f, 0.3f), new Color3f(0.0f, 0.0f, 0.0f), new Color3f(1.0f, 1.0f, 1.0f), new Color3f(1.0f, 1.0f, 1.0f), 10.0f);

        /*
         * Creates a transparent texture
         * Reference: Java 3D API Jump-Start (Aaron E. Walsh and Doug Gehringer)
         */
        TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
        transparencyAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
        transparencyAttributes.setTransparency(1.0f);
        transparentAppearance.setTransparencyAttributes(transparencyAttributes);

        /*
         * Load the table .obj file from the directory specified
         * Reference: https://www.cgtrader.com/free-3d-models/sports/game/billiard-pool-table-4a97b4bb488c81810c3f044c2b44473a
         */
        new LoadObject(new Vector3f(0.0f, -1.16f, 0.0f), 3.2f, tableAppearance, mapGroup, "Table", "./res/obj/table/PoolTableLowPoly.obj");

        /*
         * Create the transparent sphere which will represent the table pockets
         */
        tablePockets.add(new Ball(new Vector3f(2.85f, -0.0f, 1.45f), new Vector3f(20.0f, 0.0f, 0.0f), 0.13f, transparentAppearance, mapGroup, 1, "Pocket", true));
        tablePockets.add(new Ball(new Vector3f(2.85f, -0.0f, -1.45f), new Vector3f(20.0f, 0.0f, 0.0f), 0.13f, transparentAppearance, mapGroup, 1, "Pocket", true));

        tablePockets.add(new Ball(new Vector3f(-2.85f, -0.0f, -1.45f), new Vector3f(20.0f, 0.0f, 0.0f), 0.13f, transparentAppearance, mapGroup, 1, "Pocket", true));
        tablePockets.add(new Ball(new Vector3f(-2.85f, -0.0f, 1.45f), new Vector3f(20.0f, 0.0f, 0.0f), 0.13f, transparentAppearance, mapGroup, 1, "Pocket", true));

        tablePockets.add(new Ball(new Vector3f(0, -0.0f, 1.55f), new Vector3f(20.0f, 0.0f, 0.0f), 0.13f, transparentAppearance, mapGroup, 1, "Pocket", true));
        tablePockets.add(new Ball(new Vector3f(0, -0.0f, -1.55f), new Vector3f(20.0f, 0.0f, 0.0f), 0.13f, transparentAppearance, mapGroup, 1, "Pocket", true));

        // Call the createPoolBalls method to add the pool balls to the table
        createPoolBalls();

        // Create a plane which is flipped to be transparent, and is at almost table level. This is used so the Pick behaviour intersects with this point.
        Plane planeA = new Plane(new Vector3f(0.0f, 0.125f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), 200.0f, 200.0f, new Color3f(0.2f, 0.2f, 0.2f), mapGroup, "FLOOR");
        planeA.setRotation(new Quat4d(180, 0, 0, 0));

        /*
         * Generate a randomly positioned and coloured floor underneath the pool table
         */
        int mapScaleX = 20;
        int mapScaleZ = 10;
        float planeSize = 0.5f;
        for(int x = 0; x <= mapScaleX; x++) {
            for(int z = 0; z <= mapScaleZ; z++) {
                if(((x == 0 || z == 0 || x == mapScaleX || z == mapScaleZ) && (x % 2 == 0 && z % 2 == 0))) continue;
                float randomVal = (float)(0.25f + Math.random() * (0.3f - 0.25f));
                new Plane(new Vector3f(x - (planeSize * mapScaleX), -2.28f, z - (planeSize * mapScaleZ)), new Vector3f(0.0f, 0.0f, 0.0f), planeSize, planeSize, new Color3f(randomVal, randomVal, randomVal), mapGroup, "FLOOR");
            }
        }
        float randomVal = (float)(0.25f + Math.random() * (0.3f - 0.25f));
        new Plane(new Vector3f(0.0f, -2.281f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), 200.0f, 200.0f, new Color3f(randomVal, randomVal, randomVal), mapGroup, "FLOOR");

        /*
         * Add all of the other BranchGroups to the rootGroup
         */
        rootGroup.addChild(behaviourGroup);
        rootGroup.addChild(fogGroup);
        rootGroup.addChild(mapGroup);

        // Compile the scene for performance purposes
        rootGroup.compile();
    }

    /*
     * Creates a SkySphere to display a texture representing the sky
     */
    public void createBackgroundSphere() {
        skyGroup = new BranchGroup();
        Background skyBackground = new Background();
        skyBackground.setApplicationBounds(Constants.BOUNDS);

        // Load the texture for the sky
        TextureLoader skyTexture = new TextureLoader("./res/DarkBackground.jpg", null);
        Appearance skyAppearance = new Appearance();
        skyAppearance.setTexture(skyTexture.getTexture());

        // Create a Sphere and generate the normals inward
        Sphere skySphere = new Sphere(1.0f, Sphere.GENERATE_NORMALS | Sphere.GENERATE_NORMALS_INWARD | Sphere.GENERATE_TEXTURE_COORDS, 40, skyAppearance);

        // Add the sphere to the skyGroup
        skyGroup.addChild(skySphere);
        skyBackground.setGeometry(skyGroup);

        // Add the skyBackground to the rootGroup
        rootGroup.addChild(skyBackground);
    }

    /*
     * Create ExponentialFog,
     */
    public void createFog() {
        ExponentialFog exponentialFog = new ExponentialFog(new Color3f(0.35f, 0.35f, 0.35f), 0.02f);
        exponentialFog.setInfluencingBounds(Constants.BOUNDS);
        fogGroup.addChild(exponentialFog);
    }

    /*
     * Method for creating and storing the pool balls
     */
    public void createPoolBalls() {
        // Size of the pool balls
        float ballScale = 0.1f;

        /*
         * Position values for the balls
         */
        float ballX = 1.0f;
        float ballY = 0.0f;
        float ballZ = 0.0f;

        // An ArrayList for storing the appearances of the balls
        ArrayList<Appearance> ballAppearances = new ArrayList<>();

        /*
         * Add the created appearances to the ballAppearance ArrayList, iterates through all of the images in the balls texture directory
         */
        for(int i = 0; i < 16; i++) {
            ballAppearances.add(new ObjectMaterial().createAppearance(new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f), 1.0f, "./res/textures/balls/" + i + ".png"));
        }

        /*
         * Row 1
         */
        ballX = ballX + (ballScale * 1.75f);
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(1), mapGroup, 1, "SOLID", false));

        /*
         * Row 2
         */
        ballX = ballX + (ballScale * 1.75f);
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ + (ballScale)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(2), mapGroup, 2, "SOLID", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ - (ballScale)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(3), mapGroup, 3, "SOLID", false));

        /*
         * Row 3
         */
        ballX = ballX + (ballScale * 1.75f);
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ + (ballScale * 2)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(4), mapGroup, 4, "SOLID", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(5), mapGroup, 5, "SOLID", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ - (ballScale * 2)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(6), mapGroup, 6, "SOLID", false));

        /*
         * Row 4
         */
        ballX = ballX + (ballScale * 1.75f);
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ + ballScale), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(7), mapGroup, 7, "SOLID", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ - ballScale), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(8), mapGroup, 8, "BLACK", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ + (ballScale * 3)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(9), mapGroup, 9, "STRIPE", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ - (ballScale * 3)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(10), mapGroup, 10, "STRIPE", false));

        /*
         * Row 5
         */
        ballX = ballX + (ballScale * 1.75f);
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ + (ballScale * 2)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(11), mapGroup, 11, "STRIPE", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ - (ballScale * 2)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(12), mapGroup, 12, "STRIPE", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(13), mapGroup, 13, "STRIPE", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ + (ballScale * 4)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(14), mapGroup, 14, "STRIPE", false));
        ballRack.add(new Ball(new Vector3f(ballX, ballY, ballZ - (ballScale * 4)), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(15), mapGroup, 15, "STRIPE", false));

        /*
         * White Ball
         */
        whiteBall = new Ball(new Vector3f(-2.0f, ballY, 0.0f), new Vector3f(20.0f, 0.0f, 0.0f), ballScale, ballAppearances.get(0), mapGroup, 0, "WHITE", false);
        ballRack.add(whiteBall);
        mapGroup.addChild(new Cue(mapGroup, whiteBall));
    }

}
