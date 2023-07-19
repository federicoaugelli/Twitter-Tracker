package main;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.stage.*;
import twitter4j.GeoLocation;
import twitter4j.MediaEntity;
import twitter4j.Status;
import java.io.*;
import java.net.URL;
import java.util.*;


public class Controller implements Initializable {

    @FXML
    public ComboBox<String> filterSearch;

    @FXML
    public DatePicker datePickerBegin;

    @FXML
    public DatePicker datePickerEnd;

    @FXML
    public ComboBox<String> filterStream;

    @FXML
    public ListView<String> listView;

    @FXML
    public Button streamButton;

    @FXML
    public Button searchButton;

    @FXML
    public TextField streamBar;

    @FXML
    public Button streamStop;

    @FXML
    public ComboBox location;

    @FXML
    public TextField searchBar;

    @FXML
    public TableView tweetTable;

    @FXML
    public TableColumn tweetNumberColumn;

    @FXML
    public TableColumn likesColumn;

    @FXML
    public TableColumn retweetColumn;

    @FXML
    public TableColumn followersColumn;

    @FXML
    public TableColumn userColumn;

    @FXML
    public TableColumn imageColumn;

    @FXML
    public Button login;

    @FXML
    public Button conta;

    @FXML
    public Button grafico;

    @FXML
    public Button wordcloud;

    @FXML
    public ListView<ImageView> imageListView;

    TwitterHandler twitterHandler;

    private Gson gson;

    private JsonArray jsonList;

    ObservableList<String> tweetList;

    ObservableList<ImageView> imageList;

    private boolean localizedSt;

    private boolean safeNewsSt;

    private boolean[] localized;

    private boolean temporal;

    private boolean popolare;

    private List<Double> latitude;

    private List<Double> longitude;

    private List<String> descriptions;

    private List<String> mediaEntities;


    //contatore per tweet geolocalizzati
    private int cnt;
    //contatore per tweet
    private int tweetCount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cnt = 0;
        tweetCount = 0;
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        descriptions = new ArrayList<>();
        mediaEntities = new ArrayList<>();

        try {
            twitterHandler = new TwitterHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gson = new Gson();
        filterSearch.getItems().setAll("Nessun Filtro", "Filtro Temporale", "Filtro Tendenza", "Filtro Posizione");
        filterStream.getItems().setAll("Nessun Filtro", "Filtro Posizione", "Filtro Notizie Sicure");
        location.getItems().setAll("Italia", "Valle d'Aosta", "Piemonte", "Lombardia", "Trentino Alto Adige", "Veneto",
                "Friuli Venezia Giulia", "Liguria", "Emilia Romagna", "Toscana", "Marche", "Umbria", "Lazio", "Abruzzo", "Molise",
                "Campania", "Puglia", "Basilicata", "Calabria", "Sicilia", "Sardegna");
        tweetList = FXCollections.observableList(new ArrayList<String>());
        imageList = FXCollections.observableList(new ArrayList<ImageView>());
        listView.setItems(tweetList);
        location.setOpacity(0);
        location.setDisable(true);
        datePickerBegin.setOpacity(0);
        datePickerEnd.setOpacity(0);

        localizedSt = false;
        safeNewsSt = false;

        localized = new boolean[21];
        for (int i = 0; i < 21; i++) {
            localized[i] = false;
        }

        temporal = false;
        popolare = false;


        tweetNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tweetNumber"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        likesColumn.setCellValueFactory(new PropertyValueFactory<>("likes"));
        retweetColumn.setCellValueFactory(new PropertyValueFactory<>("retweet"));
        followersColumn.setCellValueFactory(new PropertyValueFactory<>("followers"));

        //Gestione evento click su un elemento della lista
        listView.setOnMouseClicked(mouseEvent -> {
            String tweet = listView.getSelectionModel().getSelectedItem();

            //L'utente potrebbe cliccare su una riga vuota
            if (tweet != null) {
                //Recupero la numerazione del tweet nella lista e rimuovo newline e break
                tweet = tweet.replace("\n", "").replace("\r", "");
                int tweetNumber = Integer.parseInt(tweet.replaceAll(":.*", "").replaceAll("#", ""));

                //Recupero l'id del tweet dal testo
                String id = tweet.replaceAll(".*[|| id:]", "");
                Status tweetFound = twitterHandler.searchById(id);
                MediaEntity[] media = tweetFound.getMediaEntities();
                Image image;
                ImageView imageView = null;
                if (media.length > 0) {
                    MediaEntity im = media[0];
                    image = new Image(im.getMediaURL());
                    imageView = new ImageView(image);
                    imageView.setFitHeight(200);
                    imageView.setFitWidth(200);
                }
                if (tweetFound!=null){
                    if (imageView!=null){
                        TweetStats tweetStats = new TweetStats(tweetNumber, tweetFound.getUser().getName(), tweetFound.getFavoriteCount(),
                                tweetFound.getRetweetCount(),tweetFound.getUser().getFollowersCount(), imageView/*,tweetFound.getGeoLocation().toString()*/);
                        tweetTable.getItems().add(tweetStats);
                    }
                    else {
                        Image image2 = new Image(new File("../tweet.jpg").toURI().toString());
                        ImageView imageView1 = new ImageView(image2);
                        TweetStats tweetStats = new TweetStats(tweetNumber, tweetFound.getUser().getName(), tweetFound.getFavoriteCount(),
                                tweetFound.getRetweetCount(),tweetFound.getUser().getFollowersCount(), imageView1);
                        tweetTable.getItems().add(tweetStats);
                    }
                }
            }
        });
    }


    public void loginTwitter() throws Exception{
        FileChooser fc = new FileChooser();
        fc.setTitle("Scegli l'immagine da postare");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("file PNG","*.png"));
        File file = fc.showOpenDialog(null);

        //Post periodico su twitter
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    twitterHandler.login(file.toString());

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }, 0, 60000);   //6 ore = 21600000
    }

    //Gestisce la selezione dei filtri della ricerca Search
    public void selectFilterSearch(){

      int index = filterSearch.getSelectionModel().getSelectedIndex();

      if (index == 0) {
            datePickerBegin.setOpacity(0);
            datePickerEnd.setOpacity(0);
            location.setOpacity(0);
            location.setDisable(true);
            datePickerBegin.setDisable(true);
            datePickerEnd.setDisable(true);
            temporal = false;
            popolare = false;
        }

      else if (index == 1) {
            datePickerBegin.setOpacity(1);
            datePickerEnd.setOpacity(1);
            location.setOpacity(0);
            location.setDisable(true);
            datePickerBegin.setDisable(false);
            datePickerEnd.setDisable(false);
            temporal = true;
            popolare = false;
        }

        else if (index == 2) {
            datePickerBegin.setOpacity(0);
            datePickerEnd.setOpacity(0);
            datePickerBegin.setDisable(true);
            datePickerEnd.setDisable(true);
            location.setOpacity(0);
            location.setDisable(true);
            temporal = false;
            popolare = true;
        }

         else if (index==3) {
            location.setOpacity(1);
            datePickerBegin.setOpacity(0);
            datePickerEnd.setOpacity(0);
            datePickerBegin.setDisable(true);
            datePickerEnd.setDisable(true);
            location.setDisable(false);
            temporal = false;
            popolare = false;
        }
          else {
            datePickerBegin.setOpacity(0);
            datePickerEnd.setOpacity(0);
            location.setOpacity(0);
          }
    }

    //Chiama la funzione che fa iniziare la ricerca Stream
    public void streamSearch() {
        String text = streamBar.getText();
        if (text.length() > 0){
            tweetTable.getItems().clear();
            listView.getItems().clear();
            twitterHandler.streamSearch(text, tweetList, localized, localizedSt, safeNewsSt);
        }
    }

    //Stoppa la ricerca stream
    public void stopStream() {
        jsonList = twitterHandler.stopStream();
        for (int i = 0; i < 21; i++) {
            localized[i] = false;
        }
    }

    //Gestisce la selezione dei filtri della ricerca Stream
    public void selectFilterStream() {
        int index = filterStream.getSelectionModel().getSelectedIndex();
        if (index == 0) {
            location.setOpacity(0);
            localizedSt = false;
            location.setDisable(true);
            safeNewsSt = false;
        }
        else if (index == 1) {
            location.setOpacity(1);
            localizedSt = true;
            location.setDisable(false);
            safeNewsSt = false;
        }
        else if (index==2) {
            location.setOpacity(0);
            location.setDisable(true);
            localizedSt = false;
            safeNewsSt = true;
        }
         else {
            localizedSt = false;
            safeNewsSt = false;
            location.setOpacity(0);
         }
    }

    public void selectRegion(){
        int selection = location.getSelectionModel().getSelectedIndex();

        switch (selection) {
            case 0: localized[0] = true;
                break;
            case 1: localized[1] = true;
                break;
            case 2: localized[2] = true;
                break;
            case 3: localized[3] = true;
                break;
            case 4: localized[4] = true;
                break;
            case 5: localized[5] = true;
                break;
            case 6: localized[6] = true;
                break;
            case 7: localized[7] = true;
                break;
            case 8: localized[8] = true;
                break;
            case 9: localized[9] = true;
                break;
            case 10:localized[10] = true;
                break;
            case 11:localized[11] = true;
                break;
            case 12:localized[12] = true;
                break;
            case 13:localized[13] = true;
                break;
            case 14:localized[14] = true;
                break;
            case 15:localized[15] = true;
                break;
            case 16:localized[16] = true;
                break;
            case 17:localized[17] = true;
                break;
            case 18:localized[18] = true;
                break;
            case 19:localized[19] = true;
                break;
            case 20:localized[20] = true;
                break;
            default: break;
        }
    }

    //Chiama la funzione che ricerca i tweet tramite la Search API
    public void search(){
        String searchText = searchBar.getText();
        String dateBegin = null;
        String dateEnd = null;


        if (datePickerBegin.getValue()!=null && datePickerEnd.getValue()!=null) {
            dateBegin = datePickerBegin.getValue().toString();
            dateEnd = datePickerEnd.getValue().toString();
        }

        if (searchText.length()>0){

            jsonList = twitterHandler.search(searchText, localized, dateBegin, dateEnd, temporal, popolare);
            loadTweetList();
        }
    }

    //Carica i tweet nella ListView
    public void loadTweetList() {
        for (int i = 0; i < 21; i++) {
            localized[i] = false;
        }
        if (jsonList != null) {
            listView.getItems().clear();
            tweetList.clear();
            listView.getItems().clear();
            tweetTable.getItems().clear();
            tweetCount = 0;
            for (JsonElement o : jsonList) {
                tweetCount++;
                String text = "#" + tweetCount + ": " + o.getAsJsonObject().get("text").toString()
                        + " || id:" + o.getAsJsonObject().get("id");
                tweetList.add(text);
                listView.setItems(tweetList);
            }
        }
    }

    //Gestisce il salvataggio dei tweet in un file JSON
    public void saveTweet(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Archivia tweet");
        fc.setInitialFileName("Example");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("file JSON","*.json"));
        File file = fc.showSaveDialog(null);

        try {

            //Se il file esiste già il programma fa l'append dei tweet
            if (file != null) {
                if (file.exists()){
                    FileReader f = new FileReader(file.getAbsolutePath());
                    JsonReader jsonReader = new JsonReader(f);
                    JsonArray jsonListToAdd = gson.fromJson(jsonReader, JsonArray.class);
                    jsonListToAdd.addAll(jsonList);
                    FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
                    fileWriter.write(gson.toJson(jsonListToAdd));
                    fileWriter.close();
                }
            else {
                    FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
                    fileWriter.write(gson.toJson(jsonList));
                    fileWriter.close();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Caricamento di un archivio di tweet salvato precedentemente
    public void loadJson() throws Exception{

        jsonList = new JsonArray();
        String jsonFilePath = null;
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON files (*.json)","*.json"));
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null){
            jsonFilePath = selectedFile.getAbsolutePath();
        }
        else{
            System.out.println("Il file non è valido o non è stato selezionato");
        }

        FileReader file = new FileReader(jsonFilePath);
        JsonReader jsonReader = new JsonReader(file);
        Gson reader = new Gson();

        try {
            if (jsonFilePath!=null) {
                jsonList = reader.fromJson(jsonReader, JsonArray.class);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        loadTweetList();
    }


    //Gestisce l'apertura della mappa
    @FXML
    public void openMap (ActionEvent event){
        try {
            tweetPositions();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/map.fxml"));
            Parent rootMap = loader.load();
            MapController mapController = loader.getController();
            mapController.transferPositions(latitude, longitude, descriptions,cnt, mediaEntities);
            Stage stage = new Stage();
            stage.setTitle("Mappa dei tweet");
            stage.setScene(new Scene(rootMap));
            stage.show();
        }

        catch (Exception e){
            e.printStackTrace();
            System.out.println("Couldn't load map window");
        }

    }

    //Calcola la posizione dei tweet geolocalizzati
    public void tweetPositions() {
        if (jsonList!=null) {
            latitude.clear();
            longitude.clear();
            descriptions.clear();
            mediaEntities.clear();
            cnt = 0;
            tweetCount = 0;
            String mediaUrl = null;
            for (Object o : jsonList){
                JsonObject place = (JsonObject) ((JsonObject)o).get("place");
                String tweetText = ((JsonObject)o).get("text").getAsString();
                JsonArray media = (JsonArray) ((JsonObject)o).get("mediaEntities");
                JsonObject geoLocation = (JsonObject) ((JsonObject)o).get("geoLocation");
                mediaUrl = null;
                tweetCount++;

                if (geoLocation!=null) {
                    cnt++;
                    double lat = Double.parseDouble(geoLocation.get("latitude").toString());
                    double lon = Double.parseDouble(geoLocation.get("longitude").toString());

                    if (media.size() > 0) {
                        mediaUrl = media.get(0).getAsJsonObject().get("mediaURL").getAsString();
                    }
                    latitude.add(lat);
                    longitude.add(lon);
                    descriptions.add(tweetText);
                    mediaEntities.add(mediaUrl);

                }
                else if(place != null){
                    JsonArray jsonBB = place.get("boundingBoxCoordinates").getAsJsonArray();
                    GeoLocation[][] locations = gson.fromJson(jsonBB, GeoLocation[][].class);
                    latitude.add((locations[0][0].getLatitude()+locations[0][1].getLatitude()
                            +locations[0][2].getLatitude()+locations[0][3].getLatitude())/4);
                    longitude.add((locations[0][0].getLongitude()+locations[0][1].getLongitude()
                            +locations[0][2].getLongitude()+locations[0][3].getLongitude())/4);
                    descriptions.add(tweetText);


                    if (media.size() > 0) {
                        mediaUrl = media.get(0).getAsJsonObject().get("mediaURL").getAsString();
                    }

                    mediaEntities.add(mediaUrl);
                }
            }
        }
    }

    //Utile a noi per contare i tweet
    public void contaFoto() throws Exception{
        int cntGeo = 0;
        int cntMed = 0;
        try {
            String path = "C:/Users/matte/IdeaProjects/Twitter - Tracker";
            String filename ="Terremoto.json";
            FileReader file = new FileReader(path + "/" + filename);
            BufferedReader  buffer = new BufferedReader(file);
            JsonReader jsonReader = new JsonReader(buffer);
            Gson reader = new Gson();
            JsonArray tweets = reader.fromJson(jsonReader, JsonArray.class);
            for (Object o : tweets){
                JsonObject geoLocation = (JsonObject) ((JsonObject)o).get("geoLocation");
                JsonObject place = (JsonObject) ((JsonObject)o).get("place");
                JsonArray media = (JsonArray) ((JsonObject)o).get("mediaEntities");
                if (geoLocation != null) {
                    cntGeo++;
                }
                else if (place != null) {
                    cntGeo++;
                }
                if (media.size() > 0) {
                    cntMed++;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Geolocated tweet: " + cntGeo);
        System.out.println("Tweet with media: " + cntMed);
    }

    //Mostra i grafici prodotti
    @FXML
    public void VisualizzaGrafico(ActionEvent event){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/chart.fxml"));
            Parent rootChart = loader.load();
            ChartController chartController = loader.getController();
            chartController.transferDataChart(jsonList);
            Stage stage = new Stage();
            stage.setTitle("Grafico dei tweet");
            stage.setScene(new Scene(rootChart));
            stage.show();
        }

        catch (Exception e){
            e.printStackTrace();
            System.out.println("Couldn't load graph window");
        }
    }

    //Mostra la wordcloud prodotta
    public void CreateWordCloud() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/wordcloud.fxml"));
            Parent rootMap = loader.load();
            WordCloudController wordCloudController = loader.getController();
            wordCloudController.transferText(jsonList);
            Stage stage = new Stage();
            stage.setTitle("Word Cloud");
            stage.setScene(new Scene(rootMap));
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Couldn't load wordcloud window");
        }
    }

}
