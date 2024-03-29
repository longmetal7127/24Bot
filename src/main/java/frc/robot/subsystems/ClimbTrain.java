package frc.robot.subsystems;

//import static edu.wpi.first.units.Units.*;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
//import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants.ClimbConstants;
import monologue.Annotations.Log;
import monologue.Logged;

public class ClimbTrain extends SubsystemBase implements Logged {

  public CANSparkMax left = new CANSparkMax(
    ClimbConstants.kLeftMotorCanId,
    com.revrobotics.CANSparkLowLevel.MotorType.kBrushless
  );
  public CANSparkMax right = new CANSparkMax(
    ClimbConstants.kRightMotorCanId,
    com.revrobotics.CANSparkLowLevel.MotorType.kBrushless
  );
  private SparkPIDController m_leftPID;
  private SparkPIDController m_rightPID;
  //private SysIdRoutine sysIdRoutine;
  private Servo lServo = new Servo(1);
  private Servo rServo = new Servo(2);
  @Log.NT
  private double pos = 0;

  public ClimbTrain() {
    left.setSmartCurrentLimit(40);
    right.setSmartCurrentLimit(40);
    m_leftPID = left.getPIDController();
    m_rightPID = right.getPIDController();

    m_leftPID.setP(ClimbConstants.kTurningP);
    m_leftPID.setI(ClimbConstants.kTurningI);
    m_leftPID.setD(ClimbConstants.kTurningD);
    m_leftPID.setFF(ClimbConstants.kTurningFF);
    m_leftPID.setOutputRange(-0.5, 1);

    m_rightPID.setP(ClimbConstants.kTurningP);
    m_rightPID.setI(ClimbConstants.kTurningI);
    m_rightPID.setD(ClimbConstants.kTurningD);
    m_rightPID.setFF(ClimbConstants.kTurningFF);
    m_rightPID.setOutputRange(-1, 1);

    left.setSoftLimit(CANSparkBase.SoftLimitDirection.kReverse, 0);
    right.setSoftLimit(CANSparkBase.SoftLimitDirection.kReverse, 0);
    lServo.setAngle(90);
    rServo.setAngle(90);
    /*sysIdRoutine =
      new SysIdRoutine(
        new SysIdRoutine.Config(),
        new SysIdRoutine.Mechanism(
          voltage -> left.setVoltage(voltage.in(Volts)),
          null, // No log consumer, since data is recorded by URCL
          this
        )
      );*/
  }

  public Command setPosition(double pos) {
    return this.runOnce(() -> {
        m_leftPID.setReference(pos, ControlType.kPosition);
        m_rightPID.setReference(pos, ControlType.kPosition);

        this.pos = pos;
      });
  }

  public void runVolts(double volts) {
    m_leftPID.setReference(volts, ControlType.kVoltage);
  }

  public Command incrementPosition() {
    return this.runOnce(() -> {
        m_leftPID.setReference(this.pos + 5, ControlType.kPosition);
        m_rightPID.setReference(-(this.pos + 5), ControlType.kPosition);

        this.pos += 5;
      });
  }

  public Command decrementPosition() {
    return this.runOnce(() -> {
        m_leftPID.setReference(this.pos - 5, ControlType.kPosition);
        m_rightPID.setReference(-(this.pos - 5), ControlType.kPosition);

        this.pos -= 5;
      });
  }
}
