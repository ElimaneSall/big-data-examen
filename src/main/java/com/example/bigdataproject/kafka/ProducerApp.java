package com.example.bigdataproject.kafka;

import com.example.bigdataproject.Climat;
import com.example.bigdataproject.ClimatImpl;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProducerApp {
    private static final String TOPIC_NAME = "waether";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&appid=8e8dcdabcf0f398cee6ba78071c95ff2";

    public static void main(String[] args) {
        ClimatImpl climat = new ClimatImpl();
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "client-producer-1");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            try {
                String weatherData = getWeatherDataFromAPI();
                String key = "weather";

                // Prétraitement des données
                JSONObject jsonObject = new JSONObject(weatherData);

                // Conversion des données
                int humidity = jsonObject.getJSONObject("main").getInt("humidity");
                int pressure = jsonObject.getJSONObject("main").getInt("pressure");
                double temperature = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
                double temperatureMax = jsonObject.getJSONObject("main").getDouble("temp_max") - 273.15;
                double temperatureMin = jsonObject.getJSONObject("main").getDouble("temp_min") - 273.15;
                int seaLevel = jsonObject.getJSONObject("main").getInt("sea_level");
                int grndLevel = jsonObject.getJSONObject("main").getInt("grnd_level");
                int visibility = jsonObject.getInt("visibility");
                String weatherMain = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

                // Conversion de la vitesse du vent en km/h
                double windSpeedMilesPerHour = jsonObject.getJSONObject("wind").getDouble("speed");
                double  windDirection = jsonObject.getJSONObject("wind").getInt("deg");
                double windSpeedKmPerHour = windSpeedMilesPerHour * 1.60934;

                // Conversion du temps de collecte en heure
                int timeCollecte = jsonObject.getInt("dt");
                LocalDateTime collecteDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeCollecte), ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String collecteTime = collecteDateTime.format(formatter);

                int timezone = jsonObject.getInt("timezone");
                LocalDateTime timezoneDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeCollecte), ZoneId.systemDefault());

                String timezoneTime = collecteDateTime.format(formatter);

                // Conversion des heures de lever et de coucher du soleil
                int sunrise = jsonObject.getJSONObject("sys").getInt("sunrise");
                LocalDateTime sunriseDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sunrise), ZoneId.systemDefault());
                String sunriseTime = sunriseDateTime.format(formatter);

                int sunset = jsonObject.getJSONObject("sys").getInt("sunset");
                LocalDateTime sunsetDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sunset), ZoneId.systemDefault());
                String sunsetTime = sunsetDateTime.format(formatter);

                //current Time
                LocalDateTime currentTime = LocalDateTime.now();
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedTime = currentTime.format(formatter2);


                // Création de l'objet JSON avec les données sélectionnées
                JSONObject selectedData = new JSONObject();
                selectedData.put("humidity", humidity);
                selectedData.put("pressure", pressure);
                selectedData.put("temperature", temperature);
                selectedData.put("temperatureMax", temperatureMax);
                selectedData.put("temperatureMin", temperatureMin);
                selectedData.put("seaLevel", seaLevel);
                selectedData.put("grndLevel", grndLevel);
                selectedData.put("visibility", visibility);
                selectedData.put("windSpeed", windSpeedKmPerHour);
                selectedData.put("windDirection", windDirection);
                selectedData.put("timeCollecte", collecteTime);
                selectedData.put("timezone", timezoneTime);
                selectedData.put("sunrise", sunriseTime);
                selectedData.put("sunset", sunsetTime);
                selectedData.put("weatherMain", weatherMain);
                selectedData.put("currentTime", formattedTime);

                // Envoi des données sélectionnées au topic Kafka

                Climat climat1 = new Climat();

                climat1.setSunrise(Timestamp.valueOf(sunriseDateTime) );
                climat1.setVisibility(visibility);
                climat1.setTimezone(Timestamp.valueOf(timezoneDateTime));
                climat1.setWeatherMain(weatherMain);
                climat1.setTemperatureMax(BigDecimal.valueOf(temperatureMax));
                climat1.setTimeCollecte(Timestamp.valueOf(collecteDateTime));
                climat1.setTemperatureMin(BigDecimal.valueOf(temperatureMin));
                climat1.setPressure(pressure);
                climat1.setCurrentTime(Timestamp.valueOf(currentTime));
                climat1.setSeaLevel(seaLevel);
                climat1.setSunset(Timestamp.valueOf(sunsetDateTime));
                climat1.setTemperature(BigDecimal.valueOf(temperature));
                climat1.setHumidity(humidity);
                climat1.setWindDirection((int) windDirection);
                climat1.setGrndLevel(grndLevel);
                climat1.setWindSpeed(BigDecimal.valueOf(windSpeedKmPerHour));

                climat.saveClimat(climat1);

                kafkaProducer.send(new ProducerRecord<>(TOPIC_NAME, key, selectedData.toString()), (metadata, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent weather data => " + selectedData.toString() + ", Partition => " + metadata.partition() + ", Offset => " + metadata.offset());
                    } else {
                        System.err.println("Error sending weather data: " + ex.getMessage());
                    }
                });
            } catch (IOException e) {
                System.err.println("Failed to fetch weather data: " + e.getMessage());
            }
        }, 0, 15, TimeUnit.MINUTES);
    }

    private static String getWeatherDataFromAPI() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } else {
            throw new IOException("Failed to fetch weather data. Response Code: " + responseCode);
        }
    }
}
