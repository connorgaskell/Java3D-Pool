package gaskell.scene.objects.behaviours;

import gaskell.Constants;
import gaskell.Game;

import javax.media.j3d.*;
import java.util.Enumeration;

/*
 * @author Connor Gaskell
 * @description Timer for the game, increments every 1000ms
 */

public class GameTimer extends Behavior {

    private WakeupCriterion[] wakeupCriterion;
    private WakeupOr wakeupOr;

    /*
     * Set the scheduling bounds of the behaviour in the constructor
     */
    public GameTimer() {
        setSchedulingBounds(Constants.BOUNDS);
    }

    /*
     * Initialise method, runs on application load.
     * Defines the wakeup criteria for the behaviour.
     */
    @Override
    public void initialize() {
        // Create 1 wakeup criteria
        wakeupCriterion = new WakeupCriterion[1];

        // Runs every 1000ms (1 second)
        wakeupCriterion[0] = new WakeupOnElapsedTime(1000);

        // WakeupOr to store all of the wakeup criteria
        wakeupOr = new WakeupOr(wakeupCriterion);

        // Set the wakeupOn to wakeupOr
        wakeupOn(wakeupOr);
    }

    @Override
    public void processStimulus(Enumeration enumeration) {
        WakeupCriterion wakeupCriterion = (WakeupCriterion) enumeration.nextElement();

        /*
         * If the wakeup criteria for elapsed frames is met, and the game has started and not ended, increment the time variable.
         */
        if(wakeupCriterion instanceof WakeupOnElapsedTime) {
            if(Game.gameStarted && !Game.gameEnded) Game.time++;
        }

        // Set the wakeupOn again, otherwise it will not run again.
        wakeupOn(wakeupOr);
    }

}
