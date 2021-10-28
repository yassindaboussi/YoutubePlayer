/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package YoutubePlayer;

import static YoutubePlayer.MainApp.stage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yassin
 */
public class YoutubePlayerController implements Initializable {

    private final String GITHUB = "https://github.com/yassindaboussi";
    private final String FACEBOOK = "https://www.facebook.com/yassdaboussi";
    @FXML
    private HBox titleBar;
    @FXML
    private HBox title;
    @FXML
    private Label pin;
    @FXML
    private Label resize;
    @FXML
    private VBox root;
    @FXML
    private WebView browser;
    @FXML
    private HBox searchBar;
    @FXML
    private TextField search;
    private XY dragXY = new XY();
    private double dx;
    private double dy;
    private final String regExp = "^.*(youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|playlist\\?|\\/watch\\?v=|\\&v=|\\?v=)([^#\\&\\?]*)(.*)"; // untested
    private final String youtube = "https://www.youtube.com";
    public static int Clicked = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String welcomeHtmlPath = getClass().getResource("welcome.html").toExternalForm();
        browser.getEngine().load(welcomeHtmlPath);
        search.setText("https://www.youtube.com/watch?v=H4-5PFdr0GQ&list=RDEM-S6q3IxlUfJBdaEJnmVeXA");
        search.setOnKeyPressed(this::search);
    }

    @FXML
    private void Zoom(MouseEvent event) {
        if (!stage.isMaximized()) {
            stage.setWidth(800.0);
            stage.setHeight(630.0);
        }
    }

    @FXML
    private void unZoom(MouseEvent event) {
        if (!stage.isMaximized()) {
            stage.setWidth(410.0);
            stage.setHeight(310.0);
        }
    }

    @FXML
    private void searchicon(MouseEvent event) {
        Clicked++;
        if (Clicked == 1) {
            search.setVisible(true);
        }
        if (Clicked == 2) {
            search.setVisible(false);
            Clicked=0;
        }
    }

    private class XY {

        private double x;
        private double y;
    }

    private void search(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            String url = search.getText();
            Matcher matcher;
            Pattern pattern = Pattern.compile(regExp);
            matcher = pattern.matcher(url);

            if (matcher.find()) {
                String src = "http://www.youtube.com/embed/" + matcher.group(2) + "?autoplay=1&loop&playlist=" + matcher.group(2);

                if (matcher.group(3).startsWith("&list")) {
                    System.out.println("playlist");
                    src = "http://www.youtube.com/embed/" + matcher.group(2) + "?autoplay=1&loop=1&" + matcher.group(3);
                    System.out.println("playList");
                } else {
                    src = "http://www.youtube.com/embed/" + matcher.group(2) + "?autoplay=1&loop=1&playlist=" + matcher.group(2);
                    System.out.println("Not a playList");
                }

                System.out.println("src ==>> " + src);
                browser.getEngine().load(src);
                search.setVisible(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Popup YouTube");
                alert.setContentText("Please provide a video/playlist URL");
                alert.showAndWait();
            }
        }
    }

    private void minimize_app(MouseEvent event) {
        MainApp.stage.setIconified(true);
    }

    ChangeListener<Boolean> listener = (observable, oldValue, newVal) -> {
        if (newVal) {
            root.getChildren().remove(titleBar); //Hide bar when is FullScreeen
        } else {
            root.getChildren().add(0, titleBar);//Restor(show Bar)
        }
    };

    @FXML
    private void fullScreen() {
        if (!stage.isFullScreen()) {
            stage.fullScreenProperty().removeListener(listener);
            stage.fullScreenProperty().addListener(listener);
            stage.setFullScreen(true);
        }
    }

    @FXML
    private void titleReleased(MouseEvent event) {
        if (event.getScreenY() == 0 && !stage.isMaximized()) {
            resize();
        }
    }

    @FXML
    private void titleDragged(MouseEvent event) {
        if (stage.isMaximized()) {
            double pw = stage.getWidth();
            resize();
            double nw = stage.getWidth();
            dragXY.x /= (pw / nw);
        }
        stage.setX(event.getScreenX() - dragXY.x);
        stage.setY(event.getScreenY() - dragXY.y);
    }

    @FXML
    private void titleSelected(MouseEvent event) {
        dragXY.x = event.getScreenX() - stage.getX();
        dragXY.y = event.getScreenY() - stage.getY();
    }

    @FXML
    private void pin(MouseEvent event) {
        if (stage.isAlwaysOnTop()) {
            pin.setText("📌");
            stage.setAlwaysOnTop(false);
        } else {
            pin.setText("Unpin");
            stage.setAlwaysOnTop(true);
        }
        browser.requestFocus();
    }

    @FXML
    private void minimize(MouseEvent event) {
        stage.setIconified(true);
        browser.requestFocus();
    }

    @FXML
    private void resize() {
        if (stage == null) {
            stage = (Stage) title.getScene().getWindow(); // an ugly way of initializing stage
        }
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            resize.setText("⬜");
        } else {
            stage.setY(0);
            stage.setMaximized(true);
            resize.setText("💢");
        }
        browser.requestFocus();
    }

    @FXML
    private void close(MouseEvent event) {
        stage.close();
        System.exit(0);//force close
    }

}
