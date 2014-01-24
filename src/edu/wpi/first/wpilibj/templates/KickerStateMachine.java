/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Talon;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class KickerStateMachine extends SimpleRobot {

    public static final int JOYSTICK_LEFT = 1;
    public static final int JOYSTICK_RIGHT = 2;
    public static final int JOYSTICK_OPERATOR = 3;
    public static final int KICK_BUTTON = 1; //for kicker
    public static final int RETURN_BUTTON = 4; //for kicker
    public static final int INIT_BUTTON = 5; //for kicker
    public Joystick joyLeft;
    public Joystick joyRight;
    public Joystick joyOperator;
    public RobotDrive driver;

    public static final int RIGHT_MOTOR_PWM = 8; //for driving
    public static final int LEFT_MOTOR_PWM = 1; //for driving
    public static final int KICKER_MOTOR_PWM = 2; //for motor on kicker

    public static final int POT = 5; //for analog potentiometer

    public DriverStationLCD lcd;

    public static final double LOWER_LIMIT = 0.1; //potentiometer lower limit
    public static final double UPPER_LIMIT = 4.5; //potentiometer upper limit
    public AnalogPotentiometer pot; //potentiometer

    public static final int INIT = 0;
    public static final int SWING_KICK = 1;
    public static final int SWING_RETURN = 2;
    public int kickerState;

    public static final double KICK_SPEED = 0.2; //speed to kick ball
    public static final double RETURN_SPEED = -0.8; //speed to return back to rest
    public Talon kickerMotor; //motor controller for kicker motor

    public static final String VERSION = "0.0.0.0r0000";

    public KickerStateMachine() {
        joyLeft = new Joystick(JOYSTICK_LEFT);
        joyRight = new Joystick(JOYSTICK_RIGHT);
        joyOperator = new Joystick(JOYSTICK_OPERATOR);
        driver = new RobotDrive(LEFT_MOTOR_PWM, RIGHT_MOTOR_PWM);
        driver.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        driver.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        lcd = DriverStationLCD.getInstance();
        pot = new AnalogPotentiometer(POT);
        kickerState = INIT;

    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        lcd.updateLCD();
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        boolean useArcadeDrive = false;
        lcd.println(DriverStationLCD.Line.kUser1, 1, "OperatorControl v" + VERSION);
        lcd.updateLCD();

        boolean newValue, oldValue;
        oldValue = false; //shows older value of button to detect a valuebutton change

        while (isOperatorControl() && isEnabled()) {
            newValue = joyLeft.getRawButton(3);
            //if newValue of button is true and oldValue is false, ONLY then change mode
            if (newValue && !oldValue) //detects a change in value of button
            {
                useArcadeDrive = !useArcadeDrive; //toggles between tank drive and arcade drive
                if (useArcadeDrive) {
                    lcd.println(DriverStationLCD.Line.kUser2, 1, "Robot is in ArcadeDrive.");
                } else {
                    lcd.println(DriverStationLCD.Line.kUser2, 1, "Robot is in TankDrive.");
                }
                lcd.updateLCD();
            }
            oldValue = newValue;

            if (!useArcadeDrive) {
                driver.tankDrive(joyLeft, joyRight);
            } else {
                driver.arcadeDrive(joyLeft);
            }
        }
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {

    }

    public void kickerStateMachine() {
        double zAxis = joyOperator.getZ();
        lcd.println(DriverStationLCD.Line.kUser3, 1, "" + zAxis);
        lcd.updateLCD();
        switch (kickerState) {

            case INIT:
                kickerMotor.set(0.0);
                if (joyOperator.getRawButton(KICK_BUTTON)) {
                    kickerState = SWING_KICK;
                    lcd.println(DriverStationLCD.Line.kUser2, 1, "SWING_KICK     ");
                    lcd.updateLCD();
                }
                break;

            case SWING_KICK:
                kickerMotor.set(KICK_SPEED);
                //kickerMotor.set(zAxis);
                if (joyOperator.getRawButton(RETURN_BUTTON)) {
                    kickerState = SWING_RETURN;
                    lcd.println(DriverStationLCD.Line.kUser2, 1, "SWING_RETURN   ");
                    lcd.updateLCD();
                }
                break;

            case SWING_RETURN:
                kickerMotor.set(RETURN_SPEED);
                //kickerMotor.set(zAxis);
                if (joyOperator.getRawButton(INIT_BUTTON)) {
                    kickerState = INIT;
                    lcd.println(DriverStationLCD.Line.kUser2, 1, "INIT           ");
                    lcd.updateLCD();
                }
                break;
        }
    }
}
