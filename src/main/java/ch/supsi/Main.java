package ch.supsi;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //TODO: metti commenti e traduci quelli esistenti in inglese


        // Percorso del file di preferenze
        System.out.println("prova" + Paths.get(UserPreferences.getInputFilePath()));
        Path inputPath = Paths.get(UserPreferences.getInputFilePath()).toAbsolutePath();
        Path outputPath = Paths.get(UserPreferences.getOutputFilePath()).toAbsolutePath();

        System.out.println("Input file absolute path: " + inputPath);
        System.out.println("Output file absolute path: " + outputPath);


        // Lettura file
        List<Entry> entries = FileHandler.readEntries(inputPath);

        //Calcola statistiche
        int totalMovies = Statistics.totalMovies(entries);
        double averageRunTime = Statistics.averageRunTime(entries);
        String bestDirector = Statistics.bestDirector(entries);
        String mostPresentActor = Statistics.mostPresentActor(entries);
        int mostProductiveYear = Statistics.mostProductiveYear(entries);

        // Scrittura risultati
        FileHandler.writeStatistics(outputPath, totalMovies, averageRunTime, bestDirector, mostPresentActor, mostProductiveYear);
    }
}