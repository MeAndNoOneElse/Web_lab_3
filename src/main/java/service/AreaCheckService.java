package service;

import dto.ResultDTO;
import dto.PointDTO;

import entity.ResultEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import repository.RepositoryInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class AreaCheckService implements AreaCheckServiceInterface, Serializable {

    @Inject
    private RepositoryInterface repository;

    @Override
    public ResultDTO checkAndSave(PointDTO point) {
        long startTime = System.nanoTime();
        boolean hit = checkHit(point.getX(), point.getY(), point.getR());
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        Date date = new Date();

        ResultDTO resultDTO = new ResultDTO(point.getX(), point.getY(), point.getR(), hit, date, executionTime);

        ResultEntity result = new ResultEntity(1, point.getX(), Double.parseDouble(point.getY()), point.getR(), hit, date, executionTime);
        repository.saveResult(result);

        return resultDTO;
    }

    @Override
    public List<ResultDTO> getAllResults() {
        List<ResultEntity> entities = repository.getAllPoints();
        List<ResultDTO> results = new ArrayList<>();
        for (ResultEntity entity : entities) {
            ResultDTO dto = new ResultDTO(
                entity.getX(),
                String.valueOf(entity.getY()),
                entity.getR(),
                entity.isHit(),
                entity.getTimestamp(),
                entity.getExecutionTimeNano()
            );
            results.add(dto);
        }
        return results;
    }

    @Override
    public void deleteAllResults() {
        repository.deleteAllPoints();
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