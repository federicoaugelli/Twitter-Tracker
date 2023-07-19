package main;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import javax.imageio.ImageIO;

public class WordCloudController implements Initializable {
    @FXML
    public WebView webView;

    @FXML
    public Button saveButton;

    private WebEngine webEngine;

    String data;

    JsonArray jsonList;

    @Override
    public void initialize(URL urlcloud, ResourceBundle resourceBundleMap) {

        webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/html/wordcloud.html").toString());
        webEngine.setJavaScriptEnabled(true);
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("app", this);

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    window.call("initialize", data);
                    webViewClickListener(webEngine, window);
                }
            }
        });

    }

    //Fa lo screen della WebView corrente
    public void save() {
        File destFile = new File("wordcloud.png");
        WritableImage snapshot = webView.snapshot(new SnapshotParameters(), null);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
        try {
            ImageIO.write(renderedImage, "png", destFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Ricavo i dati dalla jsonList di tweet
    public void transferText(JsonArray jsonList) {
        this.jsonList = jsonList;
        //testo da passare alla funzione per la wordcloud
        StringBuilder cloudText = new StringBuilder();
        for (JsonElement o : jsonList) {
            cloudText.append(o.getAsJsonObject().get("text").toString());
        }

        //creo finestra
        data = cloudText.toString();

    }

    //creazione wordcloud relativa alla parola cliccata
    private void webViewClickListener(WebEngine webEngine, JSObject jsObject) {
        Element cloudWordContainer = webEngine.getDocument().getElementById("container");
        ((EventTarget) cloudWordContainer).addEventListener("click", e -> {
            if (jsonList != null) {
                String clickedWord = webEngine.executeScript("clickedWord").toString().toLowerCase();
                StringBuilder cloudWordText = new StringBuilder();
                for (JsonElement jsonElement : jsonList) {
                    String text = jsonElement.getAsJsonObject().get("text").toString().toLowerCase();
                    //se la parola cliccata nella cloud word è presente nel tweet, allora lo aggiungo al testo
                    // per la nuova cloud word
                    if (text.contains(clickedWord))
                        cloudWordText.append(text);
                }
                jsObject.call("initialize", cloudWordText.toString());
            }
        }, false);
    }
}