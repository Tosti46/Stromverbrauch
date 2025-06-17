package zaehlerstand;

public class VerbrauchEintrag {

    public String monat;
    public int zaehlerstand;
    public int verbrauch;
    public int kosten;
    public int verbrauchProTag;
    public double strompreis;

    // Standard-Konstruktor (wird z. B. von Speicher- oder GUI-Klassen genutzt)
    public VerbrauchEintrag(String monat, int zaehlerstand, int verbrauch, int kosten, int verbrauchProTag) {
        this.monat = monat;
        this.zaehlerstand = zaehlerstand;
        this.verbrauch = verbrauch;
        this.kosten = kosten;
        this.verbrauchProTag = verbrauchProTag;
        this.strompreis = VerbrauchSpeicher.STANDARD_PREIS; // falls nicht extra übergeben
    }

    // Neuer Konstruktor mit explizitem Strompreis
    public VerbrauchEintrag(String monat, int zaehlerstand, int verbrauch, int kosten, int verbrauchProTag, double strompreis) {
        this.monat = monat;
        this.zaehlerstand = zaehlerstand;
        this.verbrauch = verbrauch;
        this.kosten = kosten;
        this.verbrauchProTag = verbrauchProTag;
        this.strompreis = strompreis;
    }
}