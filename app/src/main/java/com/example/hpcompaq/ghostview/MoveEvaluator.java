package com.example.hpcompaq.ghostview;

import android.animation.TypeEvaluator;

/**
 * Created by hpcompaq on 2016/11/8.
 */

public class MoveEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        float s = (float) startValue;
        float e = (float) endValue;
        float y = s + (e-s)*fraction;
        return y;
    }
}
