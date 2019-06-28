package com.urtcdemo.utils;

import java.io.PrintWriter;
import java.io.StringWriter;


public class UnhandledExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "AppRTCMobileActivity";
//    private final Activity activity;

    public UnhandledExceptionHandler() {

    }

    // Returns the Message attached to the original Cause of |t|.
    private static String getTopLevelCauseMessage(Throwable t) {
        Throwable topLevelCause = t;
        while (topLevelCause.getCause() != null) {
            topLevelCause = topLevelCause.getCause();
        }
        return topLevelCause.getMessage();
    }

    // Returns a human-readable String of the stacktrace in |t|, recursively
    // through all Causes that led to |t|.
    private static String getRecursiveStackTrace(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    @Override
    public void uncaughtException(Thread unusedThread, final Throwable e) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                e.printStackTrace();
                String title = "Fatal error: " + getTopLevelCauseMessage(e);
                String msg = getRecursiveStackTrace(e);
//                TextView errorView = new TextView(activity);
//                errorView.setText(msg);
//                errorView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
//                ScrollView scrollingContainer = new ScrollView(activity);
//                scrollingContainer.addView(errorView);
               // LogUtils.e(title + "\n\n" + msg);
//                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        diaURTCLogUtils.dismiss();
//                        System.exit(1);
//                    }
//                };
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity.getApplicationContext());
//                builder.setTitle(title)
//                        .setView(scrollingContainer)
//                        .setPositiveButton("Exit", listener)
////                        .show();
//            }
//        });
    }
}
