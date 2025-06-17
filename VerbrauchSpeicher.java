package zaehlerstand;

import java.io.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class VerbrauchSpeicher {

    public static final String DATEI = "verbrauch.csv";
    public static final double STANDARD_PREIS = 0.38;

    public static void speichernEintrag(double neuerStandDouble, LocalDate datum, double strompreis) {
        List<VerbrauchEintrag> einträge = ladeEinträge();

        VerbrauchEintrag letzter = einträge.isEmpty() ? null : einträge.get(einträge.size() - 1);
        int neuerStand = (int) neuerStandDouble;
        int verbrauch = (letzter == null) ? 0 : neuerStand - letzter.zaehlerstand;
        if (verbrauch < 0) verbrauch = 0;

        int tage = 30; // pauschal
        int kosten = (int) Math.round(verbrauch * strompreis);
        int verbrauchProTag = verbrauch / tage;

        String monatJahr = datum.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " " + datum.getYear();

        VerbrauchEintrag neu = new VerbrauchEintrag(monatJahr, neuerStand, verbrauch, kosten, verbrauchProTag, strompreis);
        einträge.add(neu);
        speichereAlle(einträge);
    }

    public static List<VerbrauchEintrag> ladeEinträge() {
        List<VerbrauchEintrag> liste = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(DATEI))) {
            String zeile;
            while ((zeile = reader.readLine()) != null) {
                String[] teile = zeile.split(",");
                if (teile.length >= 6) {
                    String monat = teile[0];
                    int stand = Integer.parseInt(teile[1]);
                    int verbrauch = Integer.parseInt(teile[2]);
                    int kosten = Integer.parseInt(teile[3]);
                    int proTag = Integer.parseInt(teile[4]);
                    double strompreis = Double.parseDouble(teile[5]);
                    VerbrauchEintrag e = new VerbrauchEintrag(monat, stand, verbrauch, kosten, proTag, strompreis);
                    liste.add(e);
                } else if (teile.length >= 5) {
                    String monat = teile[0];
                    int stand = Integer.parseInt(teile[1]);
                    int verbrauch = Integer.parseInt(teile[2]);
                    int kosten = Integer.parseInt(teile[3]);
                    int proTag = Integer.parseInt(teile[4]);
                    VerbrauchEintrag e = new VerbrauchEintrag(monat, stand, verbrauch, kosten, proTag, STANDARD_PREIS);
                    liste.add(e);
                }
            }
        } catch (IOException e) {
            // Datei existiert evtl. noch nicht
        }

        return liste;
    }

    private static void speichereAlle(List<VerbrauchEintrag> einträge) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATEI))) {
            for (VerbrauchEintrag e : einträge) {
                writer.printf("%s,%d,%d,%d,%d,%.2f%n", e.monat, e.zaehlerstand, e.verbrauch, e.kosten, e.verbrauchProTag, e.strompreis);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean loescheEintrag(VerbrauchEintrag ziel) {
        List<VerbrauchEintrag> einträge = ladeEinträge();
        boolean entfernt = einträge.removeIf(e ->
                e.monat.equals(ziel.monat)
                && e.zaehlerstand == ziel.zaehlerstand
                && e.verbrauch == ziel.verbrauch
        );
        if (entfernt) {
            speichereAlle(einträge);
        }
        return entfernt;
    }
}