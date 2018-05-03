package edu.rosehulman.integratedimagerec;

import android.os.Handler;
import android.widget.Toast;

import edu.rosehulman.me435.NavUtils;
import edu.rosehulman.me435.RobotActivity;

/**
 * Created by wangy12 on 4/24/2018.
 */

public class Scripts {

    private Handler mCommandHandler = new Handler();

    private GolfBallDeliveryActivity mActivity;

    private int ARM_REMOVAL_TIME = 3000;

    public Scripts(GolfBallDeliveryActivity activity){
        mActivity = activity;
    }

    public void testStraightScript() {
        mActivity.sendWheelSpeed(mActivity.mLeftStraightPwmValue,mActivity.mRightStraightPwmValue);
        Toast.makeText(mActivity,"Begin Driving",Toast.LENGTH_SHORT).show();
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.sendWheelSpeed(0,0);
            }
        }, 8000);
    }

    public void nearBallScript() {
        double distanceToNearBall = NavUtils.getDistance(15,0,90,50);
        long driveTimeMs = (long) (distanceToNearBall / RobotActivity.DEFAULT_SPEED_FT_PER_SEC * 1000);

        //For test make it shorter!
        mActivity.sendWheelSpeed(mActivity.mLeftStraightPwmValue,mActivity.mRightStraightPwmValue);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.sendWheelSpeed(0,0);
                removeBallAtLocation(mActivity.mNearBallLocation);
            }
        }, driveTimeMs);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mActivity.mState == GolfBallDeliveryActivity.State.NEAR_BALL_SCRIPT){
                    mActivity.setState(GolfBallDeliveryActivity.State.DRIVE_TOWARDS_FAR_BALL);
                }
            }
        }, driveTimeMs + ARM_REMOVAL_TIME);
    }

    public void farBallScript() {
        mActivity.sendWheelSpeed(0,0);
        removeBallAtLocation(mActivity.mFarBallLocation);

        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.sendWheelSpeed(0,0);
                if(mActivity.mWhiteBallLocation != 0){
                    removeBallAtLocation(mActivity.mWhiteBallLocation);
                }
                if(mActivity.mState == GolfBallDeliveryActivity.State.FAR_BALL_SCRIPT){
                    mActivity.setState(GolfBallDeliveryActivity.State.DRIVE_TOWARDS_HOME);
                }
            }
        }, ARM_REMOVAL_TIME);
    }

    private void removeBallAtLocation(final int location) {
        mActivity.sendCommand("ATTACH 111111");
        //TODO: Really remove a ball with your arm!
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.sendCommand("POSITION 83 50 0 -90 90");
            }
        }, 10);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.sendCommand("POSITION 20 90 -50 3 120");
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.setLocationToColor(location,GolfBallDeliveryActivity.BallColor.NONE);
            }
        }, ARM_REMOVAL_TIME);
    }
}
