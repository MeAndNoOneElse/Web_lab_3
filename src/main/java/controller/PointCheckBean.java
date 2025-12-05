package controller;

import dto.PointDTO;

import jakarta.inject.Inject;
import service.AreaCheckService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;


@Named
@ViewScoped
public class PointCheckBean implements Serializable {
    @Inject
    private AreaCheckService areaCheckService;


    private double x;
    private String y = "0";
    private double r = 2.0;

    // Getters and Setters
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        if (r != null) {
            this.r = r; // сохраняем новый выбор
        }
    }
    public String checkArea() {
        PointDTO pointDTO = new PointDTO(x, y, r);
        areaCheckService.checkAndSave(pointDTO);
        return null;
    }
}
