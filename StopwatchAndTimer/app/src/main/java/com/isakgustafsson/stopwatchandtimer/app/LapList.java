package com.isakgustafsson.stopwatchandtimer.app;

/**
 * Created by Home on 02/03/14.
 */
public class LapList {
    private long mId;
    private String mLapNumber;
    private String mLapTime;

    public LapList(long mId, String mLapNumber, String mLapTime) {
        this.mId = mId;
        this.mLapNumber = mLapNumber;
        this.mLapTime = mLapTime;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmLapNumber() {
        return mLapNumber;
    }

    public void setmLapNumber(String mLapNumber) {
        this.mLapNumber = mLapNumber;
    }

    public String getmLapTime() {
        return mLapTime;
    }

    public void setmLapTime(String mLapTime) {
        this.mLapTime = mLapTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LapList lapList = (LapList) o;

        if (mId != lapList.mId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }
}
