package com.example.mac.suchik.UI;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.example.mac.suchik.R;

import java.util.Objects;

public class Settings extends Fragment {

    private CheckBox head;
    private CheckBox glove;
    private CheckBox scarf;
    private CheckBox coat;
    private CheckBox jeans;
    private CheckBox shirt;
    private CheckBox boot;
    private CheckBox eyeglasses;
    private CheckBox jogger_pants;
    private CheckBox sweater;

    private RadioGroup degrees;

    private SharedPreferences settings;

    final String DEGREES = "degrees";
    final String HEAD = "head";
    final String GLOVE = "glove";
    final String SCARF = "scarf";
    final String COAT = "coat";
    final String JEANS = "jeans";
    final String SHIRT = "tshirt";
    final String BOOT = "boot";
    final String EYEGLASSES = "eyeglasses";
    final String JOGGER_PANTS = "joggerpants";
    final String SWEATER = "sweater";

    SharedPreferences.Editor ed;

    boolean count = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_main, container, false);
    }

    private Drawable resizeDrawable(Drawable d, int width, int height){
        return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(((BitmapDrawable)d).getBitmap(), width, height, true));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settings = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        degrees = view.findViewById(R.id.monitored_switch);
        if (!settings.getBoolean(DEGREES, false)) ((RadioButton)degrees.getChildAt(1)).setChecked(true);
        degrees.setOnCheckedChangeListener(new DegreesChangeListener());
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        System.out.println(size.x);
        int w = size.x / 4;
        int h = w;
        head = view.findViewById(R.id.head);
        head.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.head), w, h));
        glove = view.findViewById(R.id.glove);
        glove.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.glove), w, h));
        scarf = view.findViewById(R.id.scarf);
        scarf.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.scarf), w, h));
        coat = view.findViewById(R.id.coat);
        coat.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.coat), w, h));
        jeans = view.findViewById(R.id.jeans);
        jeans.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.jeans), w, h));
        shirt = view.findViewById(R.id.shirt);
        shirt.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.tshirt), w, h));
        boot = view.findViewById(R.id.boot);
        boot.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.boot), w, h));
        eyeglasses = view.findViewById(R.id.eyeglasses);
        eyeglasses.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.eyeglasses), w, h));
        jogger_pants = view.findViewById(R.id.jogger_pants);
        jogger_pants.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.joggerpants), w, h));
        sweater = view.findViewById(R.id.sweater);
        sweater.setButtonDrawable(resizeDrawable(getResources().getDrawable(R.drawable.sweater), w, h));

        head.setOnCheckedChangeListener(new SettingsChangeListener(HEAD));
        glove.setOnCheckedChangeListener(new SettingsChangeListener(GLOVE));
        coat.setOnCheckedChangeListener(new SettingsChangeListener(COAT));
        jeans.setOnCheckedChangeListener(new SettingsChangeListener(JEANS));
        shirt.setOnCheckedChangeListener(new SettingsChangeListener(SHIRT));
        boot.setOnCheckedChangeListener(new SettingsChangeListener(BOOT));
        eyeglasses.setOnCheckedChangeListener(new SettingsChangeListener(EYEGLASSES));
        jogger_pants.setOnCheckedChangeListener(new SettingsChangeListener(JOGGER_PANTS));
        sweater.setOnCheckedChangeListener(new SettingsChangeListener(SWEATER));
        scarf.setOnCheckedChangeListener(new SettingsChangeListener(SCARF));

        if (settings.getBoolean(HEAD, false)) head.setChecked(true);
        if (settings.getBoolean(GLOVE, false)) glove.setChecked(true);
        if (settings.getBoolean(SCARF, false)) scarf.setChecked(true);
        if (settings.getBoolean(COAT, false)) coat.setChecked(true);
        if (settings.getBoolean(JEANS, false)) jeans.setChecked(true);
        if (settings.getBoolean(SHIRT, false)) shirt.setChecked(true);
        if (settings.getBoolean(BOOT, false)) boot.setChecked(true);
        if (settings.getBoolean(EYEGLASSES, false)) eyeglasses.setChecked(true);
        if (settings.getBoolean(JOGGER_PANTS, false)) jogger_pants.setChecked(true);
        if (settings.getBoolean(SWEATER, false)) sweater.setChecked(true);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (count){
            ed.apply();
        }

    }

    class SettingsChangeListener implements CompoundButton.OnCheckedChangeListener {
        private String settingName;

        public SettingsChangeListener(String settingName) {
            this.settingName = settingName;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ed = settings.edit();
            ed.putBoolean(settingName, isChecked);
            Resources resources = getResources();
            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            System.out.println(size.x);
            int w = size.x / 4;
            if (!isChecked)
                buttonView.setButtonDrawable(resizeDrawable(getImage(getContext(), settingName), w, w));
//            Objects.requireNonNull(CompoundButtonCompat.getButtonDrawable(button)).setColorFilter(null);
            else{
                buttonView.setButtonDrawable(resizeDrawable(getImage(getContext(), settingName + "gray"), w, w));
//            float[] matrix = new float[]{
//                    0, 0, 0, 0, 122,
//                    0, 0, 0, 0, 122,
//                    0, 0, 0, 0, 122,
//                    1, 1, 1, 1, 0};
//            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
//                Objects.requireNonNull(CompoundButtonCompat.getButtonDrawable(button)).setColorFilter(filter);
            }
            ed.commit();
            count = true;
        }
    }

    public static Drawable getImage(Context c, String ImageName) {
        return ContextCompat.getDrawable(c, c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }

    class DegreesChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            ed = settings.edit();
            ed.putBoolean(DEGREES, checkedId == R.id.C);
            ed.commit();
        }
    }
}
