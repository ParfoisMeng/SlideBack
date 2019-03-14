package com.parfoismeng.slidebacklib.callback;

public abstract class SlideCallBack implements SlideBackCallBack {
    private SlideBackCallBack callBack;

    public SlideCallBack() {
    }

    public SlideCallBack(SlideBackCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onSlideBack() {
        if (null != callBack) {
            callBack.onSlideBack();
        }
    }

    public abstract void onSlide(int edgeFrom);
}