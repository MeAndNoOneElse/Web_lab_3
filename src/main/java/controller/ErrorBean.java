package controller;





import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;


@Named
@ViewScoped
public class ErrorBean implements Serializable {
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void clear() {
        this.errorMessage = null;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}

