package gaskell;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * @author Connor Gaskell
 * @description Handles input from the keyboard
 */

public class KeyInputHandler implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        /*
         * Triggered when the 'R' key is pressed, resets the game.
         */
        if(e.getKeyCode() == KeyEvent.VK_R) {
            /*
             * Resets the position of all of the balls in the ball rack
             */
            for(int i = 0; i < Game.ballRack.size(); i++) {
                if(!Game.ballRack.get(i).resetPosition) {
                    Game.ballRack.get(i).resetPosition = true;
                }
            }

            // Clear the pocketedBalls ArrayList
            Game.pocketedBalls.clear();

            // gameStarted is set to false
            Game.gameStarted = false;

            // gameEnded is also set to false
            Game.gameEnded = false;

            // Reset the timer
            Game.time = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
