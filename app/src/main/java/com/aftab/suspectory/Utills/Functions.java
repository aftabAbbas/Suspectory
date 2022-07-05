package com.aftab.suspectory.Utills;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.aftab.suspectory.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("deprecation")
public class Functions {
    public static Dialog dialog;

    public static void hideSystemUI(Context context) {


        Activity activity = (Activity) context;
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }
    public static void showKeyBoard(Context context, EditText editText) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        editText.requestFocus();
    }
    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static String getAddress(Context context, String latStr, String lonStr) {
        String address = "Fetching Location";

        double lat = Double.parseDouble(latStr);
        double lon = Double.parseDouble(lonStr);

        if (lat != 0 && lat != 0) {

            try {

                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                address = addresses.get(0).getAddressLine(0);


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }


        return address;
    }


    public static String getDateTime(String time) {
        long milliSeconds = Long.parseLong(time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy',' hh:mm a", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    public static String checkCountryISO(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso().toUpperCase();

    }

    public static String timeManager(String time) {
        String _endDate = getCurrentDate();
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        Date startDate = null;   // initialize start date

        try {
            startDate = myFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date endDate = null; // initialize  end date
        try {
            endDate = myFormat.parse(_endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert endDate != null;
        assert startDate != null;
        long difference = endDate.getTime() - startDate.getTime();

        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);

        if (min < 1) {
            return "now";
        }

        if (hours < 1) {
            if (min <= 1)
                return min + " minute ago";
            else
                return min + " minutes ago";
        }

        if (days < 1) {
            if (hours <= 1)
                return hours + " hour ago";
            else
                return hours + " hours ago";
        }

        if (days < 365) {
            return ChangeDateFormat(time);
        }
        return ChangeDateFormat2(time);
    }

    public static String ChangeDateFormat(String date) {
        DateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
        Date date1 = null;
        try {
            date1 = originalFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date1 != null;
        return targetFormat.format(date1);
    }

    public static String ChangeDateFormat2(String date) {
        DateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        Date date1 = null;
        try {
            date1 = originalFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date1 != null;
        return targetFormat.format(date1);
    }


    public static void showSnackBar(Context context, String message) {
        Activity activity = (Activity) context;
        Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Ok", view -> {
                }).setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light)).show();
    }

    public static void loadingDialog(Context context, String text, boolean showHide) {


        if (showHide) {

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.loading_dialog);
            dialog.setCancelable(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            dialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            TextView tv = dialog.findViewById(R.id.textView);
            tv.setText(text);

            dialog.show();

        } else {

            dialog.dismiss();

        }

    }

    public static int getRandomRequestCode() {


        String date = getCurrentDate();
        String miili = getMilliSeconds(date);

        String shuffeledString = shuffleText(miili);

        char[] chars = shuffeledString.toCharArray();

        String str = "";

        for (int i = 0; i < 4; i++) {

            str = str + chars[i] + "";

        }


        return Integer.parseInt(str);
    }

    public static String shuffleText(String input) {


        Random rand = new Random();

        int length = 20;

        String randomString = "";

        char[] text = new char[length];

        for (int i = 0; i < length; i++) {
            text[i] = input.charAt(rand.nextInt(input.length()));

        }


        for (int i = 0; i < text.length; i++) {
            randomString += text[i];
        }

        return randomString;
    }

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);

        return df.format(c);
    }

    public static String getMilliSeconds(String time) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        long millis = date.getTime();

        return String.valueOf(millis);
    }

    public static void hideKeyBoard(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }

    public static Transition enterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(300);

        return bounds;
    }

    public static Transition returnTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new DecelerateInterpolator());
        bounds.setDuration(300);

        return bounds;
    }

}
