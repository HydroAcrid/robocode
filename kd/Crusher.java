package kd;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.*;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Crusher - a robot by (your name here)
 */
public class Crusher extends AdvancedRobot
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
        ahead(10000);
        movingForward = true;
        
        while (true) {
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
        reverseDirection();
    }

    public void onHitRobot(HitRobotEvent e) {
        // If we're moving forward and we hit a robot, reverse direction
        if (movingForward) {
            reverseDirection();
        }
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






