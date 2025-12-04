package service;

import dto.ResultDTO;
import dto.PointDTO;
import controller.ErrorBean;
import controller.ResultsBean;

import entity.ResultEntity;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import repository.History;



import java.io.Serializable;
import java.util.Date;

@Named
@ViewScoped
public class AreaCheckService implements Serializable {
    @Inject
    private ErrorBean errorBean;

    @Inject
    private ResultsBean resultsBean;

    @Inject
    private History history;

    public ResultDTO checkAndSave(PointDTO point) {

        errorBean.clear();
        boolean hit = false;
        try {
            hit = checkHit(point.getX(), point.getY(), point.getR());
        } catch (Exception e) {
            errorBean.setErrorMessage("Ошибка проверки точки(в AreaCheckService): " + e.getMessage());
            return null;
        }

        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        Date date = new Date();

        ResultDTO resultDTO = new ResultDTO(point.getX(), point.getY(), point.getR(), hit, date, executionTime);

        resultsBean.addResult(resultDTO);

        ResultEntity result = new ResultEntity(1,point.getX(), Double.parseDouble(point.getY()), point.getR(), hit, date, executionTime );
        history.saveResult(result);

        return resultDTO;
    }

    private boolean checkHit(double x, String yStr, double r) {
        double y;
        try {
            y = Double.parseDouble(yStr);
        } catch (Exception e) {
            return false;
        }
        if (x >= 0 && y >= 0) {
            return x <= r && y <= r / 2;
        }
        if (x <= 0 && y >= 0) {
            return y <= x + r / 2;
        }
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (r * r);
        }
        return false;
    }
}