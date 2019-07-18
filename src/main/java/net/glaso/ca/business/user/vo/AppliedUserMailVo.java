package net.glaso.ca.business.user.vo;

import java.util.Date;

public class AppliedUserMailVo {
    private int seqId;
    private Date sendDate;
    private Date expiredDate;
    private String authUri;
    private int appliedUserInfoSeqId;
    private int activatedState;
    private int state;

    public int getSeqId() {
        return seqId;
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getAuthUri() {
        return authUri;
    }

    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }

    public int getAppliedUserInfoSeqId() {
        return appliedUserInfoSeqId;
    }

    public void setAppliedUserInfoSeqId(int appliedUserInfoSeqId) {
        this.appliedUserInfoSeqId = appliedUserInfoSeqId;
    }

    public int getActivatedState() {
        return activatedState;
    }

    public void setActivatedState(int activatedState) {
        this.activatedState = activatedState;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AppliedUserMailVo{" +
                "seqId=" + seqId +
                ", sendDate=" + sendDate +
                ", expiredDate=" + expiredDate +
                ", authUri='" + authUri + '\'' +
                ", appliedUserInfoSeqId=" + appliedUserInfoSeqId +
                ", activatedState=" + activatedState +
                ", state=" + state +
                '}';
    }
}
