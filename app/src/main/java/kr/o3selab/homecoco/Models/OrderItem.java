package kr.o3selab.homecoco.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderItem implements Serializable {

    public String mDeviceID;
    public String mServiceType;
    public Long mRegdate;
    public String mUserEmail;
    public String mRequest;
    public String mHomeType;
    public String mHomeTypeDetail;
    public String mEBFType;
    public String mReservationTime;
    public String mAddress;
    public String mDetailAddress;
    public String mPhoneNumber;
    public ArrayList<String> mImages;
    public String mVideo;
    public String mUid;
    public String mStatus;

    public OrderItem() {
        mDeviceID = null;
        mServiceType = null;
        mRegdate = null;
        mUserEmail = null;
        mRequest = null;
        mHomeType = null;
        mHomeTypeDetail = null;
        mEBFType = null;
        mReservationTime = null;
        mAddress = null;
        mDetailAddress = null;
        mPhoneNumber = null;
        mImages = null;
        mVideo = null;
        mStatus = null;
        mUid = null;
    }

    public OrderItem(OrderItem orderItem) {
        this.mDeviceID = orderItem.mDeviceID;
        this.mServiceType = orderItem.mServiceType;
        this.mRegdate = orderItem.mRegdate;
        this.mUserEmail = orderItem.mUserEmail;
        this.mRequest = orderItem.mRequest;
        this.mHomeType = orderItem.mHomeType;
        this.mHomeTypeDetail = orderItem.mHomeTypeDetail;
        this.mEBFType = orderItem.mEBFType;
        this.mReservationTime = orderItem.mReservationTime;
        this.mAddress = orderItem.mAddress;
        this.mDetailAddress = orderItem.mDetailAddress;
        this.mPhoneNumber = orderItem.mPhoneNumber;
        this.mImages = orderItem.mImages;
        this.mVideo = orderItem.mVideo;
        this.mStatus = orderItem.mStatus;
    }

    @Override
    public boolean equals(Object obj) {
        OrderItem object = (OrderItem) obj;
        return object.mDeviceID.equals(this.mDeviceID) && object.mRegdate.equals(this.mRegdate);
    }

    @Override
    public int hashCode() {
        return (this.mDeviceID + String.valueOf(mRegdate)).hashCode();
    }

    @Override
    public String toString() {
        return mPhoneNumber + " " + mRegdate;
    }
}
