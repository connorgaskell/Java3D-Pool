package gaskell.scene.objects.behaviours;

import gaskell.Constants;
import gaskell.Game;
import gaskell.scene.objects.primitives.Ball;

import javax.media.j3d.*;
import javax.vecmath.Vector3f;
import java.util.Enumeration;

/*
 * @author Connor Gaskell
 * @description This class handles the 3D collisions between the balls and walls in the scene, this is a far more accurate and reliable collision system
 *              than the ones provided by the Java3D API. It adapts Brian McCutchon's implementation (https://github.com/bmccutchon/Pool3D/blob/master/Pool3D/src/com/brianmccutchon/pool3d/Physics.java) and instead of using a static class
 *              it is implemented as a behavior, therefore is instantiated and added to each ball. It check for intersection with the parent ball and other balls within the scene.
 *              In this implementation it is assumed that all balls will be of the same mass.
 * @reference Credit to Brian McCutchon (http://brianmccutchon.com/) for his collision implementation which this class was derived from. (https://github.com/bmccutchon/Pool3D/blob/master/Pool3D/src/com/brianmccutchon/pool3d/Physics.java)
 */

public class BallCollision extends Behavior {

    // The Ball which this behavior is attached to
    private Ball gameObject;

    // The criteria which will activate the behavior
    private WakeupCriterion[] wakeupCriterion;

    // A WakeupOr which will hold all of the WakeupCriterion
    private WakeupOr wakeupOr;

    /*
     * Class constructor this passes the Ball.
     */
    public BallCollision(Ball gameObject) {
        // Set the global Ball variable to the passed Ball variable
        this.gameObject = gameObject;

        // Set the scheduling bounds of the behavior
        setSchedulingBounds(Constants.BOUNDS);
    }

    /*
     * Checks for collisions between the gameObject and all other balls within the scene from 'Game.ballRack'
     * Calls the 'intersects' method found within the GameObject class, if balls are intersecting then ballCollision() is called.
     */
    public void checkForCollision() {
        /*
         * Check for collisions between balls
         */
        for(int i = 0; i < Game.ballRack.size(); i++) {
            if(Game.ballRack.get(i) != gameObject && Game.ballRack.get(i).intersects(gameObject)) {
                ballCollision(gameObject, Game.ballRack.get(i));
            }
        }

        /*
         * Checks for collisions between balls and pockets
         */
        for(int i = 0; i < Game.tablePockets.size(); i++) {
            if(Game.tablePockets.get(i).intersects(gameObject)) {
                /*
                 * Move the ball towards the pockets and set the Y movement to -0.02f (representing gravity)
                 */
                gameObject.movement.velocity = new Vector3f(
                        Math.signum(Game.tablePockets.get(i).getPosition().x - gameObject.getPosition().x) * (Math.abs(Game.tablePockets.get(i).getPosition().x - gameObject.getPosition().x) * 0.5f),
                        -0.02f,
                        Math.signum(Game.tablePockets.get(i).getPosition().z - gameObject.getPosition().z) * (Math.abs(Game.tablePockets.get(i).getPosition().z - gameObject.getPosition().z) * 0.5f)
                );

                // The gameObject is no longer active in the scene
                gameObject.objectActive = false;

                // Add the ball to the pocketedBalls Array, unless it is the white ball
                if(!gameObject.name.equals("WHITE 0") && !Game.pocketedBalls.contains(gameObject)) {
                    Game.pocketedBalls.add(gameObject);
                }
            }
        }

        /*
         * If all of the balls have been pocketed, then the gameEnded bool is set to true, this will trigger a game over sequence.
         */
        if(Game.pocketedBalls.size() == Game.ballRack.size() - 1) {
            Game.gameEnded = true;
            System.out.println("All balls have been pocketed!");
        }

        // Call the wallCollision method unless the ball is falling and providing the object is active
        if(gameObject.movement.velocity.y <= 0.01f && gameObject.objectActive) wallCollision();
    }

    /*
     * Handles the ball collisions between two intersecting balls.
     * Reference: https://github.com/bmccutchon/Pool3D/blob/master/Pool3D/src/com/brianmccutchon/pool3d/Physics.java
     */
    public void ballCollision(Ball ballA, Ball ballB) {
        // Create a rotation matrix based on the centre of both colliding balls
        float[][] rotMatrix = createRotationMatrix(ballA.getPosition(), ballB.getPosition());

        /*
         * Rotates the vectors using the generated rotation matrix.
         * This ensures that only the X value of the velocity matters, so they can be compared and switched.
         */
        rotateVector(new Vector3f[] { ballA.movement.velocity, ballB.movement.velocity }, rotMatrix);

        /*
         * Set the spinVelocity equal to the movement velocity.
         * This is not an accurate way of creating spin velocity however, it gives a somewhat believable simulation
         */
        ballA.movement.spinVelocity = ballA.movement.velocity;
        ballB.movement.spinVelocity = ballB.movement.velocity;


        // Compare the X values of the velocity for both balls, if they are colliding ballA's should be greater than ballB's.
        if(ballA.movement.velocity.x > ballB.movement.velocity.x) {
            // Store the X velocity of ballA in a temporary variable
            float tempVelocity = ballA.movement.velocity.x;

            // Set ballA's X velocity to ballB's
            ballA.movement.velocity.x = ballB.movement.velocity.x;

            // Set ballB's X velocity to ballA's
            ballB.movement.velocity.x = tempVelocity;
        }

        // Create a rotation matrix from the previously created rotation matrix, essentially reversing the rotation matrix.
        reverseRotationMatrix(rotMatrix);

        // Using the reversed rotation matrix the velocity vectors can be rotated back
        rotateVector(new Vector3f[] { ballA.movement.velocity, ballB.movement.velocity }, rotMatrix);
    }

    /*
     * Using a Vector to define the size of the table, the objects velocity on the X or Z axis is inverted dependant on which side of the table was hit.
     */
    public Vector3f tableSize = new Vector3f(2.8f, 0.0f, 1.4f);
    public void wallCollision() {
        /*
         * Comparing the returned values of the signum function prevents the ball sticking to the wall in certain cases.
         */
        if (Math.abs(gameObject.getPosition().x) > tableSize.x && Math.signum(gameObject.getPosition().x) == Math.signum(gameObject.movement.velocity.x)) {
            // Invert velocity
            gameObject.movement.velocity.x = -gameObject.movement.velocity.x;
        } else if (Math.abs(gameObject.getPosition().z) > tableSize.z && Math.signum(gameObject.getPosition().z) == Math.signum(gameObject.movement.velocity.z)) {
            gameObject.movement.velocity.z = -gameObject.movement.velocity.z;
        }
    }

    /*
     * Creates a rotation matrix based on the positions of the colliding objects.
     * Reference: https://github.com/bmccutchon/Pool3D/blob/master/Pool3D/src/com/brianmccutchon/pool3d/Physics.java
     *            https://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
     */
    private float[][] createRotationMatrix(Vector3f centre, Vector3f centre2) {
        Vector3f ballBLocation = new Vector3f();

        // Subtract the two vectors and store it into ballBLocation
        ballBLocation.sub(centre2, centre);

        // Normalise the vector
        ballBLocation.normalize();

        /*
         * Create a Vector3f to represent the axis of rotation.
         * The cross-product of the ballBLocation variable, returning a vector that is perpendicular to both input vectors.
         */
        Vector3f u = new Vector3f();
        u.cross(ballBLocation, new Vector3f(1, 0, 0));

        /*
         * Calculates a single value from two Vectors, known as the dot-product.
         */
        float cos = ballBLocation.dot(new Vector3f(1, 0, 0));

        /*
         * The length of the vector.
         */
        float sin = u.length();

        // Ensures the axis is a unit vector
        if(Math.abs(sin - 0) < Math.pow(10.0f, -15)) {
            u = new Vector3f(0, 1, 0);
        } else {
            u.normalize();
        }

        /*
         * Creates the rotation matrix given an axis and an angle
         * https://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
         */
        float[][] rotationMatrix = {
                { cos + u.x * u.x * (1 - cos), u.x * u.y * (1 - cos) - u.z * sin, u.x * u.z * (1-cos) + u.y * sin },
                { u.y * u.x * (1 - cos) + u.z * sin, cos + u.y * u.y * (1 - cos), u.y * u.z * (1 - cos) - u.x * sin },
                { u.z * u.x * (1 - cos) - u.y * sin, u.z * u.y * (1 - cos) + u.x * sin, cos + u.z * u.z * (1 - cos) }
        };

        // Return the rotation matrix
        return rotationMatrix;
    }

    /*
     * Rotates a Vector using a Rotation Matrix (https://en.wikipedia.org/wiki/Rotation_matrix)
     * Reference: https://github.com/bmccutchon/Pool3D/blob/master/Pool3D/src/com/brianmccutchon/pool3d/Physics.java
     *            https://stackoverflow.com/questions/14607640/rotating-a-vector-in-3d-space
     */
    private void rotateVector(Vector3f[] velocity, float[][] mat) {
        for(int i = 0; i < velocity.length; i++) {
            velocity[i].set(velocity[i].x * mat[0][0] + velocity[i].y * mat[0][1] + velocity[i].z * mat[0][2], velocity[i].x * mat[1][0] + velocity[i].y * mat[1][1] + velocity[i].z * mat[1][2], velocity[i].x * mat[2][0] + velocity[i].y * mat[2][1] + velocity[i].z * mat[2][2]);
        }
    }

    /*
     * Transposes a provided rotation matrix.
     * Reference: https://github.com/bmccutchon/Pool3D/blob/master/Pool3D/src/com/brianmccutchon/pool3d/Physics.java
     */
    private void reverseRotationMatrix(float[][] mat) {
        /*
         * Using two nested for loops to iterate through the float[][]
         * The values of i and j are switched
         */
        for (int i = 0; i < mat.length; i++) {
            for (int j = i + 1; j < mat.length; j++) {
                float tmp = mat[i][j];
                mat[i][j] = mat[j][i];
                mat[j][i] = tmp;
            }
        }
    }

    /*
     * Behaviour initialise method
     */
    @Override
    public void initialize() {
        // Wakes up on the next elapsed frame
        wakeupCriterion = new WakeupCriterion[1];
        wakeupCriterion[0] = new WakeupOnElapsedFrames(0);

        // Create the wakeupOr from the wakeup criteria
        wakeupOr = new WakeupOr(wakeupCriterion);
        wakeupOn(wakeupOr);
    }

    /*
     * Behaviour processStimulus method
     */
    @Override
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeupCriterion = (WakeupCriterion) criteria.nextElement();

        // For the next elapsed frame, call the checkForCollision method
        if(wakeupCriterion instanceof WakeupOnElapsedFrames) {
            checkForCollision();
        }

        // Set the wakeupOn
        wakeupOn(wakeupOr);
    }
}
