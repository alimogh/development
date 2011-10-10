/*
 * KJK_TALK APIDEMOS: App-> Service-> Foreground Service Controller
 * ����� interaction�� �ִ� UI�� ���� foreground service�� ����� interaction�� ���� background service�� ����� ������
 * foreground service�� ����� ������ service priority ������ service�� ���� ������� ���ƾ��� ����(notification ���) 
 * ������ ���ؼ� �̴�. priority�� foreground>visible>service>background>empty �� ������ �Ǵµ�, �⺻������ service��
 * service ����� ������ �ǳ�, ��������� memory reclaim�� �ش� service�� kill���� �ʵ��� ��������� priority�� foreground��
 * �������ֱ� ���� API�̴�. �̶� home�� ���� api demos�� �������ʰ� �Ǹ�, visible�� ����ǰ� ���̸� foreground�� �ǰ� �ȴ�.
 * 
 * 
 * 
 * �߰������� ���⼭ �ľ��ؾ� �Ұ��� ������ method�� ȣ���ϴ� �������, getClass().getMethod("method �̸�", parameter type)
 * �������� ȣ���� �����ϴ�. �̸� reflection(���÷���) ����̶�� �Ѵ�.

 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.app;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.example.android.apis.R;

/**
 * This is an example of implementing an application service that can
 * run in the "foreground".  It shows how to code this to work well by using
 * the improved Android 2.0 APIs when available and otherwise falling back
 * to the original APIs.  Yes: you can take this exact code, compile it
 * against the Android 2.0 SDK, and it will against everything down to
 * Android 1.0.
 */
public class ForegroundService extends Service {
    static final String ACTION_FOREGROUND = "com.example.android.apis.FOREGROUND";
    static final String ACTION_BACKGROUND = "com.example.android.apis.BACKGROUND";
    
 // BEGIN_INCLUDE(foreground_compatibility)
    private static final Class<?>[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[] {
        boolean.class};
    
    private NotificationManager mNM;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
    
    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        // If we have the new startForeground API, then use it.
        //KJK_TALK: onCreate���� startForeground�� �����ϸ� �ش� method�� �����ϰ�, �������� ������ null�� �����Ѵ�.
        //���� �������� ���� ��� 1.6������ ��� setForeground method�� ����ǰ� �ȴ�.
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            try {
                //forground�� ����� API
                mStartForeground.invoke(this, mStartForegroundArgs);
            } catch (InvocationTargetException e) {
                // Should not happen.
                Log.w("ApiDemos", "Unable to invoke startForeground", e);
            } catch (IllegalAccessException e) {
                // Should not happen.
                Log.w("ApiDemos", "Unable to invoke startForeground", e);
            }
            return;
        }
        
        // Fall back on the old API.
        setForeground(true);
        mNM.notify(id, notification);
    }
    
    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            try {
                //ForeGround Service�� stop ��Ű�� API
                mStopForeground.invoke(this, mStopForegroundArgs);
            } catch (InvocationTargetException e) {
                // Should not happen.
                Log.w("ApiDemos", "Unable to invoke stopForeground", e);
            } catch (IllegalAccessException e) {
                // Should not happen.
                Log.w("ApiDemos", "Unable to invoke stopForeground", e);
            }
            return;
        }
        
        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        //mNM.cancel(id);
        setForeground(false);
    }
    
    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        try {
            //KJK_TALK: foreground method�� startForeground �Լ��� mapping�Ѵ�.
            //startForeground�Լ��� ������ �ش� service�� forground�� ������ִ� �Լ�
            //v2.0�������� �̰��� setForeground �Լ�����.
            //Signature�� �����ų�� ���� parameter�� ���Ѵ�. 
            
            mStartForeground = getClass().getMethod("startForeground",
                    mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground",
                    mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            // Running on an older platform.
            mStartForeground = mStopForeground = null;
        }
    }

    @Override
    public void onDestroy() {
        // Make sure our notification is gone.
        stopForegroundCompat(R.string.foreground_service_started);
    }
// END_INCLUDE(foreground_compatibility)

// BEGIN_INCLUDE(start_compatibility)
    // This is the old onStart method that will be called on the pre-2.0
    // platform.  On 2.0 or later we override onStartCommand() so this
    // method will not be called.
    // KJK_TALK: version 2.0 ���Ŀ��� onStart ��� onStartCommand�� ȣ��ȴ�.���� onStartCommand�� onCreate���Ŀ� ȣ��ȴ�.
    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        // KJK_TALK onStartCommand�� flag���� ���ؼ� ��ȯ����� �Ѵ�. 
        return START_STICKY;
    }
// END_INCLUDE(start_compatibility)

    void handleCommand(Intent intent) {
        //KJK_TALK: intent.getAction���� ���� service�� � intent action���� �����ߴ��� �˾Ƴ���.
        if (ACTION_FOREGROUND.equals(intent.getAction())) { // ForeGround�� ���
            // In this sample, we'll use the same text for the ticker and the expanded notification
            CharSequence text = getText(R.string.foreground_service_started);

            // Set the icon, scrolling text and timestamp
            Notification notification = new Notification(R.drawable.stat_sample, text,
                    System.currentTimeMillis());

            // The PendingIntent to launch our activity if the user selects this notification
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, Controller.class), 0);

            // Set the info for the views that show in the notification panel.
            notification.setLatestEventInfo(this, getText(R.string.local_service_label),
                           text, contentIntent);
            
            startForegroundCompat(R.string.foreground_service_started, notification);
            
        } else if (ACTION_BACKGROUND.equals(intent.getAction())) { //BackGround�ϰ�� 
            stopForegroundCompat(R.string.foreground_service_started);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    // ----------------------------------------------------------------------

    /**
     * <p>Example of explicitly starting and stopping the {@link ForegroundService}.
     * 
     * <p>Note that this is implemented as an inner class only keep the sample
     * all together; typically this code would appear in some separate class.
     */
    public static class Controller extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.foreground_service_controller);

            // Watch for button clicks.
            Button button = (Button)findViewById(R.id.start_foreground);
            button.setOnClickListener(mForegroundListener);
            button = (Button)findViewById(R.id.start_background);
            button.setOnClickListener(mBackgroundListener);
            button = (Button)findViewById(R.id.stop);
            button.setOnClickListener(mStopListener);
        }

        private OnClickListener mForegroundListener = new OnClickListener() {
            public void onClick(View v) {//KJK_TALK: service�� forground�� �����Ų��. UI(notification)�� ���� ����
                Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
                intent.setClass(Controller.this, ForegroundService.class);
                startService(intent);
            }
        };

        private OnClickListener mBackgroundListener = new OnClickListener() {
            public void onClick(View v) {//KJK_TALK: service�� forground�� �����Ų��. UI�� ���� ����
                Intent intent = new Intent(ForegroundService.ACTION_BACKGROUND);
                intent.setClass(Controller.this, ForegroundService.class);
                startService(intent);
            }
        };

        private OnClickListener mStopListener = new OnClickListener() {
            public void onClick(View v) {
                stopService(new Intent(Controller.this,
                        ForegroundService.class));
            }
        };
    }
}
