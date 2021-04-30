package com.jjuncoder.sideproject.gesture.widget.zoom;

public interface Easing {

    double easeOut(double time, double start, double end, double duration);

    double easeInOut(double time, double start, double end, double duration);
}