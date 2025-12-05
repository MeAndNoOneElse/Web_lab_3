package controller;

import dto.ResultDTO;
import service.AreaCheckServiceInterface;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class ResultsBean implements Serializable {

    @Inject
    private AreaCheckServiceInterface areaCheckService;

    private List<ResultDTO> results = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadResultsFromDatabase();
    }

    private void loadResultsFromDatabase() {
        results.clear();
        results.addAll(areaCheckService.getAllResults());
    }

    public List<ResultDTO> getResults() {
        return results;
    }

    public void addResult(ResultDTO result) {
        results.add(0, result); // добавляем в начало списка
    }

    public void clearResults() {
        results.clear();
        areaCheckService.deleteAllResults();
    }

    public void removeResult(ResultDTO row) {
        results.remove(row);
    }
}
