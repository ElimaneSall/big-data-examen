package com.example.bigdataproject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.*;
import  com.example.bigdataproject.Climat;

public class ClimatImpl {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("puMeteo");

    public Climat saveClimat(Climat climat) {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            em.persist(climat);
            et.commit();
        } catch (RuntimeException e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
//        System.out.println("Success");
        return climat;
    }

    public List<Climat> getAllClimats() {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Climat c", Climat.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Climat> getAllTemperatureToday() {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            // Récupérer la date actuelle
            Date today = new Date();

            // Exécuter une requête pour récupérer les données Climat pour aujourd'hui
            String queryString = "SELECT c FROM Climat c WHERE DATE(c.timeCollecte) = CURRENT_DATE";
            TypedQuery<Climat> query = em.createQuery(queryString, Climat.class);
            List<Climat> climatList = query.getResultList();

            return climatList;
        } finally {
            em.close();
        }
    }


    public List<Climat> getAllDataPerHour(Date hour) {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            // Exécuter une requête pour récupérer les données Climat pour l'heure donnée
            String queryString = "SELECT c FROM Climat c WHERE HOUR(c.timeCollecte) = HOUR(:hour) AND MINUTE(c.timeCollecte) = MINUTE(:hour)";
            TypedQuery<Climat> query = em.createQuery(queryString, Climat.class);
            query.setParameter("hour", hour);
            List<Climat> climatList = query.getResultList();

            return climatList;
        } finally {
            em.close();
        }
    }
    public Climat getLastData() {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            // Exécuter une requête pour récupérer les données Climat triées par ordre décroissant de la date de collecte
            String queryString = "SELECT c FROM Climat c ORDER BY c.timeCollecte DESC";
            TypedQuery<Climat> query = em.createQuery(queryString, Climat.class);
            query.setMaxResults(1); // Limiter les résultats à un seul enregistrement
            List<Climat> climatList = query.getResultList();

            if (!climatList.isEmpty()) {
                // Si la liste n'est pas vide, retourner le premier enregistrement (le dernier enregistrement en fonction de la date de collecte)
                return climatList.get(0);
            } else {
                // Si la liste est vide (aucun enregistrement), retourner null
                return null;
            }
        } finally {
            em.close();
        }
    }



}

