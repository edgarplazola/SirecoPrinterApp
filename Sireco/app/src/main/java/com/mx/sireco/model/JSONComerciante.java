package com.mx.sireco.model;

import java.math.BigDecimal;

/* renamed from: galileosolutions.com.mx.sireco.galileosolutions.com.mx.sireco.model.JSONComerciante */
public class JSONComerciante {
    String perapm;
    String perapp;
    String pernom;
    String pueide;
    BigDecimal puemts;
    String tiades;

    public String getPernom() {
        return this.pernom;
    }

    public void setPernom(String pernom2) {
        this.pernom = pernom2;
    }

    public String getPerapp() {
        return this.perapp;
    }

    public void setPerapp(String perapp2) {
        this.perapp = perapp2;
    }

    public String getPerapm() {
        return this.perapm;
    }

    public void setPerapm(String perapm2) {
        this.perapm = perapm2;
    }

    public String getTiades() {
        return this.tiades;
    }

    public void setTiades(String tiades2) {
        this.tiades = tiades2;
    }

    public String getPueide() {
        return this.pueide;
    }

    public void setPueide(String pueide2) {
        this.pueide = pueide2;
    }

    public BigDecimal getPuemts() {
        return this.puemts;
    }

    public void setPuemts(BigDecimal puemts2) {
        this.puemts = puemts2;
    }
}
