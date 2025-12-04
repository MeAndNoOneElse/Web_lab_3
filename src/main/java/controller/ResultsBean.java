package controller;

import dto.ResultDTO;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class ResultsBean implements Serializable {
    private List<ResultDTO> results = new ArrayList<>();

    public List<ResultDTO> getResults() {
        return results;
    }

    public void addResult(ResultDTO result) {
        results.add(result);
    }

    public void clearResults() {
        results.clear();
    }
    public void removeResult(ResultDTO row) {
        results.remove(row);
    }


}
