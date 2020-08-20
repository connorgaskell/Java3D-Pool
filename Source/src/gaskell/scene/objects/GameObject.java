package gaskell.scene.objects;

import gaskell.scene.objects.behaviours.Movement;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3f;

/*
 * @author Connor Gaskell
 * @description Handles objects within the scene defined as GameObjects, contains methods for manipulating GameObjects
 * References: Killer Game Programming in Java (Andrew Davison)
 */

public abstract class GameObject extends TransformGroup {
    // TransformGroups for the gameObject, position, rotation, scale and bounds
    public TransformGroup gameObject, position, rotation, scale, bounds;

    // Strings for the name, tag and layer of the GameObject
    public String name, tag, layer;

    // For access to the Movement behaviour attached to each non-static GameObject
    public Movement movement;

    // The origin position of the GameObject
    public Vector3f originPosition;

    // Should the position be reset Boolean
    public boolean resetPosition = false;

    // Boolean for if the object is currently in the scene
    public boolean objectActive;

    /*
     * In the constructor the TransformGroups are created and their capabilities set
     * Each TransformGroup is parented to the previous:
     * gameObject -> position -> rotation -> scale -> bounds
     * This is done so each of these can be manipulated independently.
     */
    public GameObject() {
        bounds = new TransformGroup();
        setCapabilities(bounds, "bounds");

        scale = new TransformGroup();
        setCapabilities(scale, "scale");
        scale.addChild(bounds);

        rotation = new TransformGroup();
        setCapabilities(rotation, "rotation");
        rotation.addChild(scale);

        position = new TransformGroup();
        setCapabilities(position, "position");
        position.addChild(rotation);

        gameObject = new TransformGroup();
        setCapabilities(gameObject, "gameObject");
        gameObject.addChild(position);
    }

    /*
     * Translate method, for setting the translation of the transform
     */
    public void translate(Vector3f translation) {
        // Get the transform of the position group
        Transform3D transform3D = getTransform(position);

        // Get the position of this object
        Vector3f pos = getPosition();

        // Add to the position
        pos.add(translation);

        // Set the transforms translation to the position
        transform3D.setTranslation(pos);

        // Set the new transform
        setTransform(position, transform3D);
    }

    /*
     * moveTo method moves the object over time using an Interpolator.
     * Passes a start point, end point and time.
     * Currently only works on one axis, should be further developed to use a Vector3f
     */
    public void moveTo(float start, float end, int time) {
        moveTo(start, end, time, 1, false);
    }

    /*
     * moveTo method moves the object over time using an Interpolator.
     * Passes a start point, end point, time, loop count and a reverse boolean (move back and forth)
     * Currently only works on one axis, should be further developed to use a Vector3f
     */
    public void moveTo(float start, float end, int time, int loopCount, boolean reverse) {
        // Create an Alpha for the Interpolator, based on the loopCount, time and whether it should reverse
        Alpha alpha = new Alpha(loopCount, reverse ? (Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE) : (Alpha.INCREASING_ENABLE), 0, 0, time, time, 0, time, time, 0);

        // Create the Interpolator
        Interpolator interpolator = new PositionInterpolator(alpha, position, new Transform3D(), start, end);

        // Add the interpolator to the position group
        position.addChild(interpolator);

        // Set the scheduling bounds for the interpolator
        interpolator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 200.0f));
    }

    /*
     * rotateTo method rotates the object over time using an Interpolator.
     * Passes a start point, end point, and time.
     * Currently only works on one axis, should be further developed to use a Vector3f
     */
    public void rotateTo(float start, float end, int time) {
        rotateTo(start, end, time, 1, false);
    }

    /*
     * rotateTo method rotates the object over time using an Interpolator.
     * Passes a start point, end point, time, loop count and a reverse boolean (move back and forth)
     * Currently only works on one axis, should be further developed to use a Vector3f
     */
    public void rotateTo(float start, float end, int time, int loopCount, boolean reverse) {
        // Create an Alpha for the Interpolator, based on the loopCount, time and whether it should reverse
        Alpha alpha = new Alpha(loopCount, reverse ? (Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE) : (Alpha.INCREASING_ENABLE), 0, 0, time, time, 0, time, time, 0);

        // Determine which axis the object should be rotated on
        Transform3D axisTransform = new Transform3D();
        axisTransform.rotZ(3 * Math.PI / 2.0d);

        // Create the Interpolator
        Interpolator interpolator = new RotationInterpolator(alpha, rotation, axisTransform, start, end);

        // Add the interpolator to the rotation group
        rotation.addChild(interpolator);

        // Set scheduling bounds of the interpolator
        interpolator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 200.0f));
    }

    /*
     * scaleTo method scales the object over time using an Interpolator.
     * Passes a start point, end point and time.
     */
    public void scaleTo(float start, float end, int time) {
        scaleTo(start, end, time, 1, false);
    }

    /*
     * scaleTo method scales the object over time using an Interpolator.
     * Passes a start point, end point, time, loop count and a reverse boolean (move back and forth)
     */
    public void scaleTo(float start, float end, int time, int loopCount, boolean reverse) {
        // Create an Alpha for the Interpolator, based on the loopCount, time and whether it should reverse
        Alpha alpha = new Alpha(loopCount, reverse ? (Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE) : (Alpha.INCREASING_ENABLE), 0, 0, time, time, 0, time, time, 0);

        // Create the Interpolator
        Interpolator interpolator = new ScaleInterpolator(alpha, position, new Transform3D(), start, end);

        // Add the interpolator to the scale group
        scale.addChild(interpolator);

        // Set the scheduling bounds of the interpolator
        interpolator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 200.0f));
    }

    /*
     * setPosition method sets the position to the passed Vector
     */
    public void setPosition(Vector3f pos) {
        // Get the current position
        Transform3D transform3D = getTransform(position);

        // Set the position to the new position
        transform3D.set(pos);

        // Set the transform
        position.setTransform(transform3D);
    }

    /*
     * setRotation method sets the rotation to the passed Quaternion
     */
    public void setRotation(Quat4d rot) {
        /*
         * Creates two Transform3D's, one contains the current rotation the other is set to the new rotation
         * The values are then multiplied and the transform is set
         */
        Transform3D transform3D = getTransform(rotation);
        Transform3D transform3D1 = new Transform3D();

        transform3D1.setRotation(rot);
        transform3D.mul(transform3D1);

        rotation.setTransform(transform3D);
    }

    /*
     * getPosition gets the position of the gameObject
     */
    public Vector3f getPosition() {
        /*
         * Calls getTransform and returns as a Vector3f
         */
        Transform3D transform3D = getTransform(position);
        Vector3f pos = new Vector3f();
        transform3D.get(pos);
        return pos;
    }

    public Vector3f getRotation() {
        /*
         * Calls getTransform and returns as a Vector3f
         */
        Transform3D transform3D = getTransform(rotation);

        Vector3f rot = new Vector3f();
        transform3D.get(rot);
        return rot;
    }

    /*
     * setTransform sets the TransformGroup's transform to the passed Transform3D
     */
    private void setTransform(TransformGroup transformGroup, Transform3D transform3D) {
        transformGroup.setTransform(transform3D);
    }

    /*
     * getTransform gets the Transform3D from the passed TransformGroup
     */
    public Transform3D getTransform(TransformGroup transformGroup) {
        Transform3D transform3D = new Transform3D();

        // Get the Transform3D from the TransformGroup
        transformGroup.getTransform(transform3D);

        // Return the transform3D variable
        return transform3D;
    }

    /*
     * Sets capabilities for TransformGroup e.g, writing and reading.
     */
    private void setCapabilities(TransformGroup transformGroup, String groupName) {
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(Node.ALLOW_BOUNDS_READ);

        // Set the userData of the TransformGroup so they can be identified by a string
        transformGroup.setUserData(groupName);
    }

    /*
     * Checks for an intersection between this gameObject and another gameObject
     * References: Pool3D by Brian McCutchon (https://github.com/bmccutchon/Pool3D)
     */
    public boolean intersects(GameObject gameObject) {
        /*
         * Gets the position of this object using getPosition and subtracts the position of the other gameObject
         */
        double xDiff = getPosition().x - gameObject.getPosition().x;
        double yDiff = getPosition().y - gameObject.getPosition().y;
        double zDiff = getPosition().z - gameObject.getPosition().z;

        // Returns a Boolean dependant on the below calculation, determines if this distance between the two gameObject is less than the radius of the object
        return (xDiff * xDiff) + (yDiff * yDiff) + (zDiff * zDiff) < Math.pow(0.2f, 2);
    }
}
