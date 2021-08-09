/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.NewsoftVentasAPP.Entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author jmorel
 */
public class DatosCoordenada implements Serializable {

    private static final long serialVersionUID = 202010161232100101L;
    private double latitud;
    private double longitud;
    private Date fechaLecturaGPS;
    private String contenido;
    private String etiqueta;
    private String colorMarker;
    private double sizeMarker;
    private String iconoMarker;
    private int orden;

    public DatosCoordenada(double longitude, double latitude, Date date) {
        this.longitud = longitude;
        this.latitud = latitude;
        this.fechaLecturaGPS = date;
    }

    public DatosCoordenada() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.latitud) ^ (Double.doubleToLongBits(this.latitud) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.longitud) ^ (Double.doubleToLongBits(this.longitud) >>> 32));
        hash = 31 * hash + Objects.hashCode(this.fechaLecturaGPS);
        hash = 31 * hash + Objects.hashCode(this.contenido);
        hash = 31 * hash + Objects.hashCode(this.etiqueta);
        hash = 31 * hash + Objects.hashCode(this.colorMarker);
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.sizeMarker) ^ (Double.doubleToLongBits(this.sizeMarker) >>> 32));
        hash = 31 * hash + Objects.hashCode(this.iconoMarker);
        hash = 31 * hash + this.orden;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DatosCoordenada other = (DatosCoordenada) obj;
        if (Double.doubleToLongBits(this.latitud) != Double.doubleToLongBits(other.latitud)) {
            return false;
        }
        if (Double.doubleToLongBits(this.longitud) != Double.doubleToLongBits(other.longitud)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sizeMarker) != Double.doubleToLongBits(other.sizeMarker)) {
            return false;
        }
        if (this.orden != other.orden) {
            return false;
        }
        if (!Objects.equals(this.contenido, other.contenido)) {
            return false;
        }
        if (!Objects.equals(this.etiqueta, other.etiqueta)) {
            return false;
        }
        if (!Objects.equals(this.colorMarker, other.colorMarker)) {
            return false;
        }
        if (!Objects.equals(this.iconoMarker, other.iconoMarker)) {
            return false;
        }
        if (!Objects.equals(this.fechaLecturaGPS, other.fechaLecturaGPS)) {
            return false;
        }
        return true;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setFechaLecturaGPS(Date fechaLecturaGPS) {
        this.fechaLecturaGPS = fechaLecturaGPS;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public void setColorMarker(String colorMarker) {
        this.colorMarker = colorMarker;
    }

    public void setSizeMarker(double sizeMarker) {
        this.sizeMarker = sizeMarker;
    }

    public void setIconoMarker(String iconoMarker) {
        this.iconoMarker = iconoMarker;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public Date getFechaLecturaGPS() {
        return fechaLecturaGPS;
    }

    public String getContenido() {
        return contenido;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColorMarker() {
        return colorMarker;
    }

    public double getSizeMarker() {
        return sizeMarker;
    }

    public String getIconoMarker() {
        return iconoMarker;
    }

    public int getOrden() {
        return orden;
    }

    @Override
    public String toString() {
        return "DatosCoordenada{" + "latitud=" + latitud + ", longitud=" + longitud + ", fechaLecturaGPS=" + fechaLecturaGPS + ", contenido=" + contenido + ", etiqueta=" + etiqueta + ", colorMarker=" + colorMarker + ", sizeMarker=" + sizeMarker + ", iconoMarker=" + iconoMarker + ", orden=" + orden + '}';
    }

}
