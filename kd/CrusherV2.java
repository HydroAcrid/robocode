package kd;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.*;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Crusher - a robot by (your name here)
 */
public class CrusherV2 extends AdvancedRobot
{
	// Keep track of the direction we are moving in
    private boolean movingForward;

    public void run() {
        // Set colors
        setBodyColor(Color.red);
        setGunColor(Color.black);
        setRadarColor(Color.yellow);
        setBulletColor(Color.green);
        setScanColor(Color.green);

        // Radar spinning indefinitely
        setTurnRadarRight(Double.POSITIVE_INFINITY);

        // Moving strategy
        movingForward = true;

        while (true) {
            // Change moving direction randomly
            if (Math.random() > 0.5) {
                setAhead(10000);
                movingForward = true;
            } else {
                setBack(10000);
                movingForward = false;
            }

            // Change turning direction randomly
            if (Math.random() > 0.5) {
                setTurnRight(45);
            } else {
                setTurnLeft(45);
            }

            if (getRadarTurnRemaining() == 0.0) {
                setTurnRadarRight(Double.POSITIVE_INFINITY);
            }
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing=e.getBearingRadians()+getHeadingRadians();

        // Adjust gun and radar independently
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
        setTurnGunRightRadians(normalizeBearing(absBearing - getGunHeadingRadians() + (e.getVelocity() * Math.sin(e.getHeadingRadians() - absBearing) / 13.0)));

        // Distance management, if we're too close, back up.
        if(e.getDistance() < 150) {
            if(movingForward){
                setBack(40000);
                movingForward = false;
            } else {
                setAhead(40000);
                movingForward = true;
            }
        }

        // When a bot is spotted, fire!
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
            setFire(Math.min(400 / e.getDistance(), 3));
        }
    }

    public void onHitWall(HitWallEvent e) {
        // Reverse direction upon hitting a wall
        // reverseDirection();
		// setTurnRight(90);
		// Get the bearing of the wall
		double bearing = e.getBearing();

		// If the wall is to our right, turn left; if it's to our left, turn right
		if (bearing > 0) {
			setTurnLeft(180);
		} else {
			setTurnRight(180);
		}
	
		// Move a bit away from the wall
		ahead(20);
    }

    public void onHitRobot(HitRobotEvent e) {
        // If we're moving forward and we hit a robot, reverse direction
        // if (movingForward) {
        //     reverseDirection();
		// 	setTurnRight(90);
        // }
		// Get the bearing of the wall
		double bearing = e.getBearing();

		// If the wall is to our right, turn left; if it's to our left, turn right
		if (bearing > 0) {
			setTurnLeft(180);
		} else {
			setTurnRight(180);
		}
	
		// Move a bit away from the wall
		ahead(20);
    }
    
    public void reverseDirection() {
        if (movingForward) {
            setBack(40000);
            movingForward = false;
        } else {
            setAhead(40000);
            movingForward = true;
        }
    }

    // Normalize a bearing to between +180 and -180
    double normalizeBearing(double angle) {
        while (angle >  Math.PI) angle -= 2*Math.PI;
        while (angle < -Math.PI) angle += 2*Math.PI;
        return angle;
    }
}






