package gaskell.scene.objects.behaviours;

import gaskell.Constants;
import gaskell.Game;
import gaskell.scene.objects.primitives.Ball;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Enumeration;

/*
 * @author Connor Gaskell
 * @description Draws the 3D line representing the cue
 * References: http://www.cs.stir.ac.uk/courses/ITNP3B/Java3D/Tutorial/j3d_tutorial_ch4.pdf
 */

public class Cue extends Behavior {

    // The criteria which will activate the behavior
    private WakeupCriterion[] wakeupCriterion;

    // A WakeupOr which will hold all of the WakeupCriterion
    private WakeupOr wakeupOr;

    // The BranchGroup
    private BranchGroup branchGroup;

    // The white ball
    private Ball whiteBall;

    /*
     * Constructor which passes the branch group and the white ball
     */
    public Cue(BranchGroup branchGroup, Ball whiteBall) {
        this.branchGroup = branchGroup;
        this.whiteBall = whiteBall;

        // Sets the scheduling bounds for the behaviour
        setSchedulingBounds(Constants.BOUNDS);
    }

    // Create a BranchGroup for the cue
    private BranchGroup cueBranch = new BranchGroup();

    // Shape3D for storing the line
    Shape3D lineShape;

    // LineArray for creating the line
    LineArray line;

    /*
     * drawLine method passes the position of the white ball, creates a line based on the position of the white ball and the mouse position
     */
    public void drawLine(Vector3f whiteBallPosition) {
        // Create the LineArray
        line = new LineArray(2, LineArray.COORDINATES);

        // Set the coordinates of the LineArray to the white ball position and 0, 0, 0 to start.
        line.setCoordinate(0, new Point3f(whiteBallPosition.x, 0.0f, whiteBallPosition.z));
        line.setCoordinate(1, new Point3f(0.0f, 0.0f, 0.0f));

        // Allow for writing coordinate geometry
        line.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);

        // Set the bounds of the cueBranch
        cueBranch.setBounds(Constants.BOUNDS);

        // Store the LineArray as a Shape3D
        lineShape = new Shape3D(line);

        // Add the Shape3D to the branch
        cueBranch.addChild(lineShape);

        // Add the cue branch to the main branchGroup
        branchGroup.addChild(cueBranch);
    }

    /*
     * positionLine passes the white ball position, this method continuously position the line dependent on the ball and mouse positions.
     */
    public void positionLine(Vector3f whiteBallPosition) {
        Game.whiteBallPower = new Vector3f((float)(whiteBallPosition.x - Game.mouseX) / 20, 0.0f, (float)(whiteBallPosition.z - Game.mouseY) / 20);

        // Set the maximum power the white ball can be struck at
        float maxPower = 0.1f;

        // Limit the white ball power using a conditional operator
        Game.whiteBallPower.x = Game.whiteBallPower.x >= maxPower ? maxPower : Game.whiteBallPower.x <= -maxPower ? -maxPower : Game.whiteBallPower.x;
        Game.whiteBallPower.z = Game.whiteBallPower.z >= maxPower ? maxPower : Game.whiteBallPower.z <= -maxPower ? -maxPower : Game.whiteBallPower.z;

        // Set the line coordinates. The cue should only be drawn when the player can shoot and the game isn't over.
        line.setCoordinate(0, new Point3f(whiteBallPosition.x, 0.0f, whiteBallPosition.z));
        if(Game.canShoot && !Game.gameEnded) line.setCoordinate(1, new Point3f((float)Game.mouseX, 0.0f, (float)Game.mouseY)); else line.setCoordinate(1, new Point3f(whiteBallPosition.x, 0.0f, whiteBallPosition.z));
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

        // Call the drawLine method
        drawLine(whiteBall.getPosition());
    }

    /*
     * Behaviour processStimulus method
     */
    @Override
    public void processStimulus(Enumeration enumeration) {
        WakeupCriterion wakeupCriterion = (WakeupCriterion) enumeration.nextElement();

        // Wake up when the frame has elapsed
        if(wakeupCriterion instanceof WakeupOnElapsedFrames) {
            /*
             * Ensure all the balls have stopped moving, then allow the player to hit the ball again
             */
            for(int i = 0; i < Game.ballRack.size(); i++) {
                if(Game.ballRack.get(i).movement.velocity.x != 0 || Game.ballRack.get(i).movement.velocity.z != 0) {
                    break;
                }

                if(Game.ballRack.get(i).movement.velocity.x == 0 && Game.ballRack.get(i).movement.velocity.z == 0 && i == Game.ballRack.size() - 1) {
                    Game.canShoot = true;
                }
            }

            // Call the positionLine method to draw the cue
            positionLine(whiteBall.getPosition());
        }

        wakeupOn(wakeupOr);
    }


}
