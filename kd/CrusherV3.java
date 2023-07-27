package kd;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;


// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * CrusherV3 - a robot by (your name here)
 */
public class CrusherV3 extends AdvancedRobot 
{
	boolean movingForward;
    static double enemyBearing;
    double moveAmount;

    public void run() {
        // Set colors
        setBodyColor(Color.black);
        setGunColor(Color.red);
        setRadarColor(Color.yellow);

        // Initialize moveAmount to the maximum possible for this battlefield.
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());

        // Make the radar turn infinitely
        setTurnRadarRight(Double.POSITIVE_INFINITY);

        movingForward = true;

        // Start moving
        setAhead(moveAmount);

        // Set the gun to turn right 90 degrees
        setTurnGunRight(90);

        while(true) {
           if (getRadarTurnRemaining() == 0.0) {
                setTurnRadarRight(Double.POSITIVE_INFINITY);
            }
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
		// Basic radar locking mechanism
		double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
		setTurnRadarRightRadians(normalizeBearing(radarTurn));
	
		// Find the direction to move
		enemyBearing = e.getBearing();
		if (-90 < enemyBearing && enemyBearing <= 90) {
			movingForward = true;
		} else {
			movingForward = false;
		}
	
		// Move in the direction found
		setAhead((movingForward ? 1 : -1) * moveAmount);
	
		// Calculate the angle to the scanned robot
		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);
	
		// Calculate the coordinates of the robot
		double scannedX = (getX() + Math.sin(angle) * e.getDistance());
		double scannedY = (getY() + Math.cos(angle) * e.getDistance());
	
		// Turn gun toward scanned robot and fire
		double theta = normalRelativeAngleDegrees(Math.toDegrees(Math.atan2(scannedX - getX(), scannedY - getY())));
		setTurnGunRight(theta - getGunHeading());
	
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(Math.min(400 / e.getDistance(), 3));
		}
	}
	

    public void onHitWall(HitWallEvent e) {
        reverseDirection();
    }

    public void onHitRobot(HitRobotEvent e) {
        reverseDirection();
    }

    public void reverseDirection() {
        movingForward = !movingForward;
    }

    // normalizes a bearing to between +180 and -180
    double normalizeBearing(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }
}





public void wallSmoothing(ScannedRobotEvent e) {
    double absBearing = e.getBearingRadians() + getHeadingRadians();
    double goalDirection = absBearing - Math.PI / 2 * direction;

    Rectangle2D fieldRect = new Rectangle2D.Double(18, 18, getBattleFieldWidth() - 36, getBattleFieldHeight() - 36);
    while (!fieldRect.contains(getX() + Math.sin(goalDirection) * 120, getY() + Math.cos(goalDirection) * 120)) {
        goalDirection += direction * 0.1;  // turn a little toward enemy and try again
    }
    
    double turn = Utils.normalRelativeAngle(goalDirection - getHeadingRadians());
    
    if (Math.abs(turn) > Math.PI / 2) {
        turn = Utils.normalRelativeAngle(turn + Math.PI);
        setBack(100);
        movingForward = false;
    } else {
        setAhead(100);
        movingForward = true;
    }
    
    setTurnRightRadians(turn);
}

