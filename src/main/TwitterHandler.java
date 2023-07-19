package main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import twitter4j.*;
import twitter4j.conf.*;
import java.io.*;
import java.util.*;

public class TwitterHandler {

    private static String CONSUMER_KEY;
    private static String CONSUMER_SECRET;
    private static String ACCESS_TOKEN;
    private static String ACCESS_TOKEN_SECRET;

    Twitter twitter;

    private TwitterStream stream;

    private final ConfigurationBuilder config;

    private Gson gson;

    private List<Status> st = new ArrayList<>();
    List<Status> tweetList = new ArrayList<>();

    public TwitterHandler() throws IOException {
        //Carico le credenziali tramite i token
        try {
            String path = "C:/Users/matte/IdeaProjects/Twitter - Tracker";
            String filename ="credential.json";
            FileReader file = new FileReader(path + "/" + filename);
            BufferedReader buffer = new BufferedReader(file);
            Gson reader = new Gson();
            JsonObject keys = reader.fromJson(buffer.readLine(), JsonObject.class);
            CONSUMER_KEY = keys.get("CONSUMER_KEY").getAsString();
            CONSUMER_SECRET = keys.get("CONSUMER_SECRET").getAsString();
            ACCESS_TOKEN = keys.get("ACCESS_TOKEN").getAsString();
            ACCESS_TOKEN_SECRET = keys.get("ACCESS_TOKEN_SECRET").getAsString();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

         config = new ConfigurationBuilder();
         config.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET)
                .setTweetModeExtended(true);

         Configuration credential = config.build();

         stream = new TwitterStreamFactory(credential).getInstance();

         twitter = new TwitterFactory(credential).getInstance();

         gson = new Gson();
    }


    //Posta i tweet su Twitter
    public void login(String post){
        try {
            StatusUpdate statusUpdate = new StatusUpdate("Tweet generato automaticamente dall'applicazione Twitter - Tracker #IngSw2020");
            File file = new File(post);
            statusUpdate.setMedia(file);
            twitter.updateStatus(statusUpdate);
        }
        catch (TwitterException e) {
            e.printStackTrace();
        }
    }


    //Fa partire la ricerca Stream
    public void streamSearch(String text, ObservableList<String> tweetList, boolean[] localizedStream, boolean localizedSt, boolean safeNewsSt) {
        //Parole chiavi di ricerca
        String[] words = text.toLowerCase().split(",");

        StatusListener listener = new StatusListener() {
            int cnt = 0;

            @Override
            public void onStatus(Status status) {

                String statusText = status.getText();
                if (Arrays.stream(words).parallel().anyMatch(statusText.toLowerCase()::contains)) {
                    cnt++;
                    String txt = "#" + cnt + ":" + statusText + "|| id:" + status.getId();
                    st.add(status);
                    Platform.runLater(() -> {
                        //Aggiorno la lista dei Tweet
                        tweetList.add(txt);
                    });
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //Utilizzando i dati solo per scopi accademici non è utile implementare la funzione
            }

            @Override
            public void onTrackLimitationNotice(int i) {
                //La nostra stream rimane limitata
            }

            @Override
            public void onScrubGeo(long l, long l1) {
                //Utilizzando i dati solo per scopi accademici non è utile implementare la funzione
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
                //Non è importante per noi che i tweet vadano in coda per una connessione lenta
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        stream.addListener(listener);
        FilterQuery filter = new FilterQuery();

        //Italy country and regions boundaries
        double[][] it = {{7.695112, 37.116637}, {13.382145, 47.083604}};
        double[][] valleDAosta = {{7.101327, 45.474321}, {7.853890, 45.911734}};
        double[][] piemonte = {{7.436167, 44.137175}, {8.566277, 45.723390}};
        double[][] lombardia = {{8.673443, 45.080405}, {10.505001, 46.217683}};
        double[][] trentino = {{10.543970, 45.960930}, {11.722792, 46.927573}};
        double[][] veneto = {{11.274115, 45.042524}, {12.329048, 46.282632}};
        double[][] friuli = {{12.567361, 45.841079}, {13.696305, 46.502950}};
        double[][] liguria = {{7.545982, 43.797649}, {9.485273, 44.537876}};
        double[][] emilia = {{10.240211, 44.315297}, {12.283393, 44.906958}};
        double[][] toscana = {{10.344102, 42.458204}, {11.680828, 44.096844}};
        double[][] marche = {{13.232260, 42.743700}, {13.661675, 44.106792}};
        double[][] umbria = {{12.338801, 42.488856}, {12.754364, 43.411476}};
        double[][] lazio = {{11.831317, 41.263597}, {13.463132, 42.761280}};
        double[][] abruzzo = {{13.519369, 41.816416}, {14.494230, 42.864485}};
        double[][] molise = {{14.044781, 41.494799}, {14.810743, 42.037771}};
        double[][] campania = {{14.139735, 39.988907}, {14.994321, 41.456856}};
        double[][] puglia = {{15.545252, 40.989685}, {18.113333, 41.842733}};
        double[][] basilicata = {{15.677391, 40.068325}, {16.608104, 40.850768}};
        double[][] calabria = {{15.659438, 37.981676}, {17.156360, 40.033799}};
        double[][] sicilia = {{12.500069, 36.734686}, {15.472323, 38.213881}};
        double[][] sardegna = {{7.962152, 38.855973}, {9.890253, 41.264603}};


        //Filtro per la regione selezionata dall'utente
        if (localizedSt) {
            if (localizedStream[0]) {
                filter.locations(it);
            }
            else if (localizedStream[1]){
                filter.locations(valleDAosta);

            }
            else if (localizedStream[2]){
                filter.locations(piemonte);

            }
            else if (localizedStream[3]){
                filter.locations(lombardia);

            }
            else if (localizedStream[4]){
                filter.locations(trentino);

            }
            else if (localizedStream[5]){
                filter.locations(veneto);

            }
            else if (localizedStream[6]){
                filter.locations(friuli);

            }
            else if (localizedStream[7]){
                filter.locations(liguria);

            }
            else if (localizedStream[8]){
                filter.locations(emilia);

            }
            else if (localizedStream[9]){
                filter.locations(toscana);

            }
            else if (localizedStream[10]){
                filter.locations(marche);

            }
            else if (localizedStream[11]){
                filter.locations(umbria);

            }
            else if (localizedStream[12]){
                filter.locations(lazio);

            }
            else if (localizedStream[13]){
                filter.locations(abruzzo);

            }
            else if (localizedStream[14]){
                filter.locations(molise);

            }
            else if (localizedStream[15]){
                filter.locations(campania);

            }
            else if (localizedStream[16]){
                filter.locations(puglia);

            }
            else if (localizedStream[17]){
                filter.locations(basilicata);

            }
            else if (localizedStream[18]){
                filter.locations(calabria);

            }
            else if (localizedStream[19]){
                filter.locations(sicilia);

            }
            else if (localizedStream[20]){
                filter.locations(sardegna);

            }

            stream.filter(filter);
        }

        //filtro notizie sicure
        else if(safeNewsSt){
            //corriere della sera, repubblica, skytg24, rainews, gazzettino, tgcom24, ilmessaggero, internazionale, sole24ore, ilpost, il fatto quotidiano, huffpost italia, l'espresso, la stampa, adnkronos, agenzia ansa, tgla7, tg3web, tg2rai, tg1rai, tgrrai, il foglio, il giornale
            long[] ids = {395218906, 18935802, 5893702, 14060262, 6831142, 36079217, 15254807, 420351046, 13379089, 52424550, 543774554, 85852409, 29416653, 25676606, 150725695, 828717014, 69959408, 526530582, 197763661, 804354408, 49954018, 25508589};
            filter.follow(ids);
            stream.filter(filter);
        }
        else {

            stream.filter(words);
        }
    }


    //Stoppa la Ricerca Stream
    public JsonArray stopStream(){

        stream.cleanUp();
        stream.clearListeners();
        stream.shutdown();
        return gson.toJsonTree(st).getAsJsonArray();
    }



    //Implementa la ricerca search
    public JsonArray search(String searchTerm, boolean[] localizedSearch, String dateBegin, String dateEnd, boolean temporal, boolean popolare){

        Query query = new Query(searchTerm);

        //Numero massimo ottenibile di tweet con le API gratuite
        query.setCount(100);

        //Filtro temporale
        if (temporal) {

            query.setSince(dateBegin);
            query.setUntil(dateEnd);
        }
        //Filtro per popolarità
        if(popolare) {
          query.resultType(Query.ResultType.popular);

        }
        else {
           //Filtro per la regione selezionata dall'utente
           if (localizedSearch[0]) {
               query.setGeoCode(new GeoLocation(41.902782, 12.496366), 650, Query.KILOMETERS);
           } else if (localizedSearch[1]) {
               //VALLE D'AOSTA
               query.setGeoCode(new GeoLocation(45.743297, 7.401959), 35.29, Query.KILOMETERS);

           } else if (localizedSearch[2]) {
               //PIEMONTE
               query.setGeoCode(new GeoLocation(45.090263, 7.837612), 102.76, Query.KILOMETERS);
           } else if (localizedSearch[3]) {
               //LOMBARDIA
               query.setGeoCode(new GeoLocation(45.701830, 9.814031), 72.54, Query.KILOMETERS);

           } else if (localizedSearch[4]) {
               //TRENTINO
               query.setGeoCode(new GeoLocation(46.479308, 11.363428), 54.67, Query.KILOMETERS);

           } else if (localizedSearch[5]) {
               //VENETO
               query.setGeoCode(new GeoLocation(45.684884, 11.924427), 86.04, Query.KILOMETERS);

           } else if (localizedSearch[6]) {
               //FRIULI VENEZIA GIULIA
               query.setGeoCode(new GeoLocation(46.198614, 13.018161), 42.99, Query.KILOMETERS);

           } else if (localizedSearch[7]) {
               //LIGURIA
               query.setGeoCode(new GeoLocation(44.167619, 8.844896), 82.03, Query.KILOMETERS);

           } else if (localizedSearch[8]) {
               //EMILIA ROMAGNA
               query.setGeoCode(new GeoLocation(44.629737, 10.751119), 104.45, Query.KILOMETERS);

           } else if (localizedSearch[9]) {
               //TOSCANA
               query.setGeoCode(new GeoLocation(43.489351, 11.297743), 79.90, Query.KILOMETERS);

           } else if (localizedSearch[10]) {
               //MARCHE
               query.setGeoCode(new GeoLocation(43.405303, 13.191726), 63.88, Query.KILOMETERS);

           } else if (localizedSearch[11]) {
               //UMBRIA
               query.setGeoCode(new GeoLocation(43.023176, 12.473646), 57.24, Query.KILOMETERS);

           } else if (localizedSearch[12]) {
               //LAZIO
               query.setGeoCode(new GeoLocation(41.945345, 12.825925), 54.87, Query.KILOMETERS);

           } else if (localizedSearch[13]) {
               //ABRUZZO
               query.setGeoCode(new GeoLocation(42.295357, 13.928094), 49.37, Query.KILOMETERS);

           } else if (localizedSearch[14]) {
               //MOLISE
               query.setGeoCode(new GeoLocation(41.647614, 14.612296), 130.57, Query.KILOMETERS);

           } else if (localizedSearch[15]) {
               //CAMPANIA
               query.setGeoCode(new GeoLocation(40.813223, 14.770118), 95, Query.KILOMETERS);

           } else if (localizedSearch[16]) {
               //PUGLIA
               query.setGeoCode(new GeoLocation(41.312852, 16.897005), 170, Query.KILOMETERS);

           } else if (localizedSearch[17]) {
               //BASILICATA
               query.setGeoCode(new GeoLocation(40.497214, 16.111931), 63, Query.KILOMETERS);

           } else if (localizedSearch[18]) {
               //CALABRIA
               query.setGeoCode(new GeoLocation(39.064205, 16.476212), 117, Query.KILOMETERS);

           } else if (localizedSearch[19]) {
               //SICILIA
               query.setGeoCode(new GeoLocation(37.590523, 14.070206), 143.91, Query.KILOMETERS);

           } else if (localizedSearch[20]) {
               //SARDEGNA
               query.setGeoCode(new GeoLocation(40.080175, 9.077905), 42, Query.KILOMETERS);

           }
       }
        query.setLang("it");

        try {
            tweetList = twitter.search(query).getTweets();
        }
        catch (TwitterException e) {
            e.printStackTrace();
        }
        return gson.toJsonTree(tweetList).getAsJsonArray();
    }



    //Dato l'id di un tweet, restituisce il tweet a cui corrisponde
    public Status searchById(String id){
        Status status = null;
        try {
            status = twitter.showStatus(Long.parseLong(id));
            if (status == null) {
                System.out.println("Tweet not found");
            } else {
                System.out.println("@" + status.getUser().getScreenName()
                        + " - " + status.getText());
            }
        } catch (TwitterException e) {
            System.err.print("Failed to search tweets: " + e.getMessage());
            e.printStackTrace();
        }
        return status;
    }

}
