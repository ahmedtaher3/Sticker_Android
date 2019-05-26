package com.sticker_android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("authrized_status")
    @Expose
    private String authrizedStatus;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("sql_error")
    @Expose
    private Object sqlError;
    @SerializedName("paylpad")
    @Expose
    private NewPaylpad paylpad;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAuthrizedStatus() {
        return authrizedStatus;
    }

    public void setAuthrizedStatus(String authrizedStatus) {
        this.authrizedStatus = authrizedStatus;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getSqlError() {
        return sqlError;
    }

    public void setSqlError(Object sqlError) {
        this.sqlError = sqlError;
    }

    public NewPaylpad getPaylpad() {
        return paylpad;
    }

    public void setPaylpad(NewPaylpad paylpad) {
        this.paylpad = paylpad;
    }
}
