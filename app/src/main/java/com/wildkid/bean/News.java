package com.wildkid.bean;

/**
 * Created by admin2 on 2017/7/1.
 */

public class News {
    private String VoaId;
    private String Title_cn;
    private String Title;
    private String Sound;
    private String ReadCount;
    private String Pic;
    private String CreatTime;
    private String DescCn;

    public String getVoaId() {
        return VoaId;
    }

    public void setVoaId(String voaId) {
        VoaId = voaId;
    }

    public String getTitle_cn() {
        return Title_cn;
    }

    public void setTitle_cn(String title_cn) {
        Title_cn = title_cn;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSound() {
        return Sound;
    }

    public void setSound(String sound) {
        Sound = sound;
    }

    public String getReadCount() {
        return ReadCount;
    }

    public void setReadCount(String readCount) {
        ReadCount = readCount;
    }

    public String getPic() {
        return Pic;
    }

    public void setPic(String pic) {
        Pic = pic;
    }

    public String getCreatTime() {
        return CreatTime;
    }

    public void setCreatTime(String creatTime) {
        CreatTime = creatTime;
    }

    public String getDescCn() {
        return DescCn;
    }

    public void setDescCn(String descCn) {
        DescCn = descCn;
    }
}
