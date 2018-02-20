/* GraspStatus - hdbt.shadow.grasps.GraspStatus
 * 
 * random collection of things that can go wrong. Will be updated, cleaned-up, 
 * and polished later.
 * 
 * 19.09.10 - initial stuff
 * 
 * (C) 2010 fnh
 */

package hdbt.shadow.grasps;

public enum GraspStatus {
  
  OK,
  STABLE_GRASP,
  ERROR,
  INVALID_ARGUMENT, 
  INVALID_JOINT_ANGLE, 
  CANT_REACH, ARM_CANT_REACH, 
  FORCES_TOO_HIGH, 
  TOO_SLOW,
  NO_PROGESS_DURING_LAST_EPOCH,
  WATCHDOG_TIMEOUT,
  PRESSURE_LOSS,
  CONTROLLERS_NOT_RUNNING,
  SLIPPAGE_DETECTED,
  MISSING_OBJECT,
}

