
package main;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ChartController {

    @FXML
    public WebView webView;

    @FXML
    public Button saveButton;

    private WebEngine webEngine;

    private int likes;

    private int retweet;

    private int[] cntTweet;


    public void initialize() {

        cntTweet = new int[20];

        webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/html/chart.html").toString());
        webEngine.setJavaScriptEnabled(true);
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("app", this);

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    window.call("createGraph", cntTweet);
                    window.call("createGraph2",likes, retweet);
                }
            }
        });
    }

    //Fa lo screen della WebView corrente
    public void save(){
        File destFile = new File("chart.png");
        WritableImage snapshot = webView.snapshot(new SnapshotParameters(), null);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
        try {
            ImageIO.write(renderedImage, "png", destFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Ricava i vari dati dalla jsonList di tweet
    public void transferDataChart(JsonArray jsonList) {

        JsonArray jsonTweets = jsonList;

        if (jsonTweets!=null) {
            for (Object o : jsonTweets) {
                JsonObject jsonPlace = (JsonObject) ((JsonObject)o).get("place");
                int jsonLikes = ((JsonObject) o).get("favoriteCount").getAsInt();
                int jsonRetweet = ((JsonObject) o).get("retweetCount").getAsInt();

                likes += jsonLikes;
                retweet += jsonRetweet;


                if (jsonPlace!=null) {
                    String regionName = jsonPlace.get("fullName").toString();
                    regionName = regionName.replaceAll("^[^,]*, ", "");
                    regionName = regionName.replaceAll("\"", "");


                    switch (regionName) {
                        case "Valle d'Aosta": cntTweet[0] += 1;
                            break;
                        case "Piemonte": cntTweet[1] += 1;
                            break;
                        case "Lombardia": cntTweet[2] += 1;
                            break;
                        case "Trentino Alto Adige": cntTweet[3] += 1;
                            break;
                        case "Veneto": cntTweet[4] += 1;
                            break;
                        case "Friuli Venezia Giulia": cntTweet[5] += 1;
                            break;
                        case "Liguria": cntTweet[6] += 1;
                            break;
                        case "Emilia Romagna": cntTweet[7] += 1;
                            break;
                        case "Toscana": cntTweet[8] += 1;
                            break;
                        case "Marche": cntTweet[9] += 1;
                            break;
                        case "Umbria": cntTweet[10] += 1;
                            break;
                        case "Lazio": cntTweet[11] += 1;
                            break;
                        case "Abruzzo": cntTweet[12] += 1;
                            break;
                        case "Molise": cntTweet[13] += 1;
                            break;
                        case "Campania": cntTweet[14] += 1;
                            break;
                        case "Puglia": cntTweet[15] += 1;
                            break;
                        case "Basilicata": cntTweet[16] += 1;
                            break;
                        case "Calabria": cntTweet[17] += 1;
                            break;
                        case "Sicilia": cntTweet[18] += 1;
                            break;
                        case "Sardegna": cntTweet[19] += 1;
                            break;
                        default: break;
                    }
                }
            }
       }
    }


}