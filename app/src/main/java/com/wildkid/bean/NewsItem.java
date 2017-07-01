package com.wildkid.bean;

/**
 * Created by admin2 on 2017/7/1.
 */

public class NewsItem {
    private String Sentence;
    private String sentence_cn;
    private float Timing;
    private float EndTiming;

    public String getSentence() {
        return Sentence;
    }

    public void setSentence(String sentence) {
        Sentence = sentence;
    }

    public String getSentence_cn() {
        return sentence_cn;
    }

    public void setSentence_cn(String sentence_cn) {
        this.sentence_cn = sentence_cn;
    }

    public float getTiming() {
        return Timing;
    }

    public void setTiming(float timing) {
        Timing = timing;
    }

    public float getEndTiming() {
        return EndTiming;
    }

    public void setEndTiming(float endTiming) {
        EndTiming = endTiming;
    }
}
