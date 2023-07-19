package main;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import javax.imageio.ImageIO;

public class MapController implements Initializable {
    @FXML
    public WebView webView;

    @FXML
    public Button saveButton;

    private WebEngine webEngine;

    List<Double> latitude;
    List<Double> longitude;
    List<String> tweetText;
    List<String> mediaEntities;
    int cnt;

    @Override
    public void initialize(URL urlmap, ResourceBundle resourceBundleMap) {
        webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/html/map.html").toString());
        webEngine.setJavaScriptEnabled(true);
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("app", this);

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue ov, Worker.State oldState, State newState) {
                if (newState == State.SUCCEEDED) {
                    webEngine.executeScript("createMap()");
                    for (int i = 0; i < cnt; i++){
                        if (mediaEntities.get(i)!=null) {
                            window.call("addMarkerImage",latitude.get(i),longitude.get(i),tweetText.get(i), mediaEntities.get(i));
                        }
                        else {
                            window.call("addMarker",latitude.get(i),longitude.get(i),tweetText.get(i));
                        }
                    }
                }

            }
        });

    }

    //Fa lo screen della WebView corrente
    public void save() throws Exception {
        File destFile = new File("map.png");
        WritableImage snapshot = webView.snapshot(new SnapshotParameters(), null);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
        try {
            ImageIO.write(renderedImage, "png", destFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    //Prendo i dati passati dal Controller
    public void transferPositions(List<Double> latitude, List<Double> longitude, List<String> tweetText, int cnt, List<String> mediaEntities){
        this.latitude = latitude;
        this.longitude = longitude;
        this.tweetText = tweetText;
        this.cnt = cnt;
        this.mediaEntities = mediaEntities;

    }
}