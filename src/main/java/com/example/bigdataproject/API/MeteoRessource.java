package com.example.bigdataproject.API;

import com.example.bigdataproject.Climat;
import com.example.bigdataproject.ClimatImpl;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Path("/meteo")
    public class MeteoRessource {


    ClimatImpl climat = new ClimatImpl();
        // temperature moyenne de la journee
        @Path("/meamTemperatureToday")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public double meamTemperatureToday(){
            List<Climat> climats = climat.getAllTemperatureToday();
            // Calculer la moyenne des températures
            double sum = 0.0;
            for (Climat c : climats) {
                sum = sum+ c.getTemperature().doubleValue(); // Assurez-vous que vous avez une méthode getTemperature() dans la classe Climat pour obtenir la valeur de la température
            }
            double averageTemperature = sum / climats.size();

            return averageTemperature;
        }
        // Moyenne de la temperature par heure
        @Path("/meamTemperaturePerHour")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Map<String, Double> meamTemperaturePerHour() {
            // Récupérer l'heure actuelle
            Date currentHour = new Date();

            System.out.println(currentHour);

            // Convertir l'heure actuelle en format "HH"
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
            String currentHourString = hourFormat.format(currentHour);

            // Créer le dictionnaire pour stocker les températures par heure
            Map<String, Double> temperatureByHour = new TreeMap<>(); // TreeMap pour trier les résultats par heure

            // Pour chaque heure de 00h jusqu'à l'heure actuelle
            for (int hour = 0; hour < Integer.parseInt(currentHourString); hour++) {
                // Définir l'heure dans le calendrier
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentHour);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                // Récupérer la date correspondant à l'heure donnée
                Date hourDate = calendar.getTime();

                // Vérifier si des données Climat sont disponibles pour l'heure donnée
                List<Climat> climatList = climat.getAllDataPerHour(hourDate);
                if (!climatList.isEmpty()) {
                    // Calculer la moyenne de température pour cette heure
                    Double averageTemperature = calculateAverageTemperature(climatList);

                    // Ajouter l'heure et la moyenne de température dans le dictionnaire
                    String hourString = String.format("%02dh", hour); // Formatage de l'heure pour ajouter un zéro devant les heures de 0 à 9
                    temperatureByHour.put(hourString, averageTemperature);
                }
            }

            return temperatureByHour;
        }

    private double calculateAverageTemperature(List<Climat> climatList) {
        if (climatList.isEmpty()) {
            return 0.0;
        }

        // Calculer la moyenne de température pour l'heure donnée
        double sum = 0.0;
        for (Climat c : climatList) {
            sum += c.getTemperature().doubleValue(); // Assurez-vous que vous avez une méthode getTemperature() dans la classe Climat pour obtenir la valeur de la température
        }
        return sum / climatList.size();
    }

    //Current Data
    @Path("/lastData")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Climat currentData(){
        return climat.getLastData();
    }
//        // temperature moyenne des 15 dernieres minutes
//        @Path("/meamTemperatureOfLast15min")
//        @GET
//        @Produces(MediaType.APPLICATION_JSON)
//        public double meamTemperatureOfLast15min() {
//            // A implementer
//            return 21.7;
//        }
//
//        // l'humidite actuelle basée sur le calcul de la moyenne des 15 dernieres minutes
//        @Path("/currentHumidity")
//        @GET
//        @Produces(MediaType.APPLICATION_JSON)
//        public double currentHumidity() {
//            // A implementer
//            return 21.7;
//        }
//
//        // la vitesse du vent actuelle basée sur les données des 15 dernieres minutes
//        @Path("/currentWindSpeed")
//        @GET
//        @Produces(MediaType.APPLICATION_JSON)
//        public double currentWindSpeed() {
//            // A implementer
//            return 21.7;
//        }
//
//        // la direction du vent actuelle basée sur les données des 15 dernieres minutes
//        @Path("/currentWindDirection")
//        @GET
//        @Produces(MediaType.APPLICATION_JSON)
//        public double currentWindDirection() {
//            // A implementer
//            return 21.7;
//        }
//
//    // L'ensoleillement actuel basé sur les données des 15 dernieres minutes
//        @Path("/ensoleillement")
//        @GET
//        @Produces(MediaType.APPLICATION_JSON)
//        public String ensoleillement() {
//            // A implementer
//            return "Cloud";
//        }
//
//        // L'heure du couché du soleil
//        @Path("/sunriseTime")
//        @GET
//        @Produces(MediaType.APPLICATION_JSON)
//        public LocalDateTime sunriseTime() {
//            // A implementer
//            LocalDateTime currentTime = LocalDateTime.now();
//            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String formattedTime = currentTime.format(formatter2);
//            return currentTime;
//        }
//        // L'heure du levée du soleil
//        @Path("/sunsetTime")
//        @GET
//        @Produces(MediaType.APPLICATION_JSON)
//        public LocalDateTime sunsetTime() {
//            // A implementer
//            LocalDateTime currentTime = LocalDateTime.now();
//            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
////            String formattedTime = currentTime.format(formatter2);
//            return currentTime;
//        }

    }
