/*
 * This file is part of the Drawchemy project - https://code.google.com/p/drawchemy/
 *
 * Copyright (c) 2014 Pilmeyer Patrick
 *
 * Drawchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Drawchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Drawchemy.  If not, see <http://www.gnu.org/licenses/>.
 */

package draw.chemy.color;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;

import org.al.chemy.R;

import java.util.ArrayList;
import java.util.List;

public class ColorUIFragment extends Fragment {

    private SeekBar fHueBar;
    private SeekBar fSaturationBar;
    private SeekBar fBrightnessBar;
    private SeekBar fAlphaSeekBar;
    private SeekBar fHueSwitchSeekbar;

    private CheckBox fHueCheckBox;

    private int fColor;
    private List<ColorChangeListener> fColorListeners;
    private List<HueSwitchListener> fHueSwitchListeners;

    private int fHue;
    private int fSaturation;
    private int fBrightness;
    private int fAlpha;

    private int fHueAmp = 0;
    private boolean fHueEnable = false;

    private float temp[];

    private ShapeDrawable fHueDrawable;
    private ShapeDrawable fSaturationDrawable;
    private ShapeDrawable fBrightnessDrawable;

    public ColorUIFragment() {
        fColorListeners = new ArrayList<ColorChangeListener>();
        fHueSwitchListeners = new ArrayList<HueSwitchListener>();
        temp = new float[3];
        fHueDrawable = new ShapeDrawable(new RectShape());
        fSaturationDrawable = new ShapeDrawable(new RectShape());
        fBrightnessDrawable = new ShapeDrawable(new RectShape());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.colorlayout,
                container, false);

        setColor(fColor);

        fHueBar = (SeekBar) view.findViewById(R.id.seekHue);

        fSaturationBar = (SeekBar) view.findViewById(R.id.seekSaturation);
        fBrightnessBar = (SeekBar) view.findViewById(R.id.seekBrightness);
        fAlphaSeekBar = (SeekBar) view.findViewById(R.id.seekAlpha);

        fHueBar.setProgress(fHue);
        fSaturationBar.setProgress(fSaturation);
        fBrightnessBar.setProgress(fBrightness);
        fAlphaSeekBar.setProgress(fAlpha);

        fSaturationBar.setProgressDrawable(fSaturationDrawable);
        fBrightnessBar.setProgressDrawable(fBrightnessDrawable);
        fHueBar.setProgressDrawable(fHueDrawable);

        setBrightnessDrawable();
        setSaturationDrawable();
        setHueDrawable();

        MyOnSeekBarChangeListener listener = new MyOnSeekBarChangeListener();
        fHueBar.setOnSeekBarChangeListener(listener);
        fSaturationBar.setOnSeekBarChangeListener(listener);
        fBrightnessBar.setOnSeekBarChangeListener(listener);
        fAlphaSeekBar.setOnSeekBarChangeListener(listener);

        fHueSwitchSeekbar = (SeekBar) view.findViewById(R.id.i_hue_switch_ampl);

        fHueSwitchSeekbar.setProgress(fHueAmp);

        fHueSwitchSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fHueAmp = fHueSwitchSeekbar.getProgress();
                for (HueSwitchListener listener : fHueSwitchListeners) {
                    listener.amplitudeChanged(fHueSwitchSeekbar.getProgress() / 100.f);
                }
            }
        });

        fHueCheckBox = (CheckBox) view.findViewById(R.id.i_hue_switch);
        fHueCheckBox.setChecked(fHueEnable);
        if (fHueEnable) {
            fHueCheckBox.setText(R.string.hue_switch_enabled);
        } else {
            fHueCheckBox.setText(R.string.hue_switch_disabled);
        }

        fHueCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fHueEnable = fHueCheckBox.isChecked();
                if (fHueEnable) {
                    fHueCheckBox.setText(R.string.hue_switch_enabled);
                } else {
                    fHueCheckBox.setText(R.string.hue_switch_disabled);
                }
                for (HueSwitchListener listener : fHueSwitchListeners) {
                    listener.stateChanged(fHueEnable);
                }
            }
        });


        Button fFinishButton = (Button) view.findViewById(R.id.finishButton);
        fFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }


    private void changeColor() {
        fHue = fHueBar.getProgress();
        fSaturation = fSaturationBar.getProgress();
        fBrightness = fBrightnessBar.getProgress();
        temp[0] = fHue;
        temp[1] = fSaturation / 100.f;
        temp[2] = fBrightness / 100.f;
        fAlpha = fAlphaSeekBar.getProgress();

        fColor = Color.HSVToColor(fAlpha, temp);

        setBrightnessDrawable();
        setSaturationDrawable();

        for (ColorChangeListener listener : fColorListeners) {
            listener.colorChange(fColor);
        }
    }

    public void setColor(int aColor, boolean sendEvent) {

        setColor(aColor);
        if (fHueBar == null) {
            return;
        }

        int hue = fHue;
        int saturation = fSaturation;
        int brightness = fBrightness;
        int alpha = fAlpha;
        fHueBar.setProgress(hue);
        fSaturationBar.setProgress(saturation);
        fBrightnessBar.setProgress(brightness);
        fAlphaSeekBar.setProgress(alpha);

        setBrightnessDrawable();
        setSaturationDrawable();

        if (sendEvent) {
            for (ColorChangeListener listener : fColorListeners) {
                listener.colorChange(fColor);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fHueBar = null;
        fSaturationBar = null;
        fBrightnessBar = null;
        fAlphaSeekBar = null;
        fHueSwitchSeekbar = null;
        fHueCheckBox = null;
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            changeColor();
        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            changeColor();
        }
    }

    public void addColorListener(ColorChangeListener aListener) {
        fColorListeners.add(aListener);
    }

    public void addHueSwitchListener(HueSwitchListener aListener) {
        fHueSwitchListeners.add(aListener);
    }


    public interface ColorChangeListener {
        public void colorChange(int aColor);
    }

    public interface HueSwitchListener {

        public void stateChanged(boolean isEnabled);

        public void amplitudeChanged(float aAmplitude);

    }

    private void setColor(int aColor) {
        Color.colorToHSV(aColor, temp);
        fHue = (int) temp[0];
        fSaturation = (int) (temp[1] * 100);
        fBrightness = (int) (temp[2] * 100);
        fAlpha = Color.alpha(aColor);
        fColor = aColor;
    }


    private void setBrightnessDrawable() {
        Paint p = fBrightnessDrawable.getPaint();
        final int colorA = Color.HSVToColor(new float[]{fHue, fSaturation / 100.f, 0.f});
        final int colorB = Color.HSVToColor(new float[]{fHue, fSaturation / 100.f, 1.f});
        int width = fBrightnessBar.getWidth();
        p.setShader(new LinearGradient(0, 0, width, 0, colorA, colorB, Shader.TileMode.CLAMP));
        fBrightnessDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int i, int i2) {
                return new LinearGradient(0, 0, i, 0, colorA, colorB, Shader.TileMode.CLAMP);
            }
        });
        fBrightnessBar.invalidate();
    }

    private void setSaturationDrawable() {
        Paint p = fSaturationDrawable.getPaint();
        final int colorA = Color.HSVToColor(new float[]{fHue, 0.f, fBrightness / 100.f});
        final int colorB = Color.HSVToColor(new float[]{fHue, 1.f, fBrightness / 100.f});
        int width = fSaturationBar.getWidth();
        p.setShader(new LinearGradient(0, 0, width, 0, colorA, colorB, Shader.TileMode.CLAMP));
        fSaturationDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int i, int i2) {
                return new LinearGradient(0, 0, i, 0, colorA, colorB, Shader.TileMode.CLAMP);
            }
        });
        fSaturationBar.invalidate();
    }

    private void setHueDrawable() {
        Paint p = fHueDrawable.getPaint();
        int size = 6;
        final int colors[] = new int[size + 1];

        float temp[] = new float[3];
        temp[1] = 1.f;
        temp[2] = 1.f;
        for (int i = 0; i < size; i++) {
            temp[0] = i * (360.f / size);
            colors[i] = Color.HSVToColor(temp);
        }
        temp[0] = 0.f;
        colors[size] = Color.HSVToColor(temp);

        int width = fHueBar.getWidth();
        p.setShader(new LinearGradient(0, 0, width, 0, colors, null, Shader.TileMode.MIRROR));
        fHueDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int i, int i2) {
                return new LinearGradient(0, 0, i, 0, colors, null, Shader.TileMode.MIRROR);
            }
        });
        fHueBar.invalidate();
    }
}