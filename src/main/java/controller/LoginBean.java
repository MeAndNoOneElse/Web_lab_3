//package controller;
//
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
//import java.io.Serializable;
//
//@ManagedBean
//@SessionScoped
//public class LoginBean implements Serializable {
//    private String userId;
//    private String password;
//    private boolean loggedIn = false;
//
//    // Getters and Setters
//    public String getUserId() { return userId; }
//    public void setUserId(String userId) { this.userId = userId; }
//
//    public String getPassword() { return password; }
//    public void setPassword(String password) { this.password = password; }
//
//    public boolean isLoggedIn() { return loggedIn; }
//
//    /**
//     * Метод входа (пока без реальной аутентификации)
//     */
//    public String login() {
//        // Здесь можно добавить проверку userId и password
//        if (userId != null && !userId.trim().isEmpty()) {
//            loggedIn = true;
//            return "main"; // Перейти на main.xhtml
//        }
//        return null; // Остаться на start.xhtml
//    }
//
//    /**
//     * Метод выхода
//     */
//    public String logout() {
//        userId = null;
//        password = null;
//        loggedIn = false;
//        return "start"; // Вернуться на start.xhtml
//    }
//}
