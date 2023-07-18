package com.example.bigdataproject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Tester {

    public static void main(String[] args) {
        ClimatImpl climat = new ClimatImpl();

        Climat climat1 = new Climat(

                Timestamp.valueOf("2023-07-04 03:37:05"),
                10000,
                Timestamp.valueOf("2023-07-04 09:18:47"),
                "Clouds",
                new BigDecimal("23.010000000000048"),
                Timestamp.valueOf("2023-07-04 09:18:47"),
                new BigDecimal("19.410000000000025"),
                1015,
                Timestamp.valueOf("2023-07-04 09:18:57"),
                1015,
                Timestamp.valueOf("2023-07-04 19:03:19"),
                new BigDecimal("21.629999999999995"),
                75,
                35,
                932,
                new BigDecimal("2.4622902"));

//        climat.saveClimat(climat1);


//        List<Climat> climats = climat.getAllTemperatureToday();
//        for (Climat c : climats) {
//            // Faites ce que vous voulez avec les données récupérées
//            System.out.println("Climat ID: " + c.getId());
//            // ...
//        }

        // Convertir une heure en objet Date pour tester
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date hourToRetrieve;
        try {
            hourToRetrieve = dateFormat.parse("2023-07-17 19:32:57"); // Remplacez cette heure par l'heure souhaitée
        } catch (ParseException e) {
            System.err.println("Erreur lors de la conversion de l'heure : " + e.getMessage());
            return;
        }

        // Appeler la fonction getAllDataPerHour() pour récupérer les données pour l'heure spécifiée
        List<Climat> climatList = climat.getAllDataPerHour(hourToRetrieve);

        // Afficher les données récupérées
        for (Climat c : climatList) {
            System.out.println("Heure de collecte : " + c.getTimeCollecte() + ", Température : " + c.getTemperature());
        }
    }

    }

