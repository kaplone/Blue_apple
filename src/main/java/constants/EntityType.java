package constants;

public enum EntityType {
    CASE ("CASE"),
    CLE ("CLE"),
    CLE1 ("CLE1"),
    CLE2 ("CLE2"),
    CADENAS ("CADENAS"),
    CADENAS1 ("CADENAS1"),
    CADENAS2 ("CADENAS2"),
    HACHE ("HACHE"),
    BAGUETTE ("BAGUETTE"),
    CACTUS ("CACTUS"),
    POMME ("POMME"),
    POMME1 ("POMME1"),
    POMME2 ("POMME2"),
    POMME3 ("POMME3"),
    POMME4 ("POMME4"),
    FLEUR ("FLEUR"),
    MUR ("MUR"),
    FANTOME ("FANTOME"),
    BOUE ("BOUE"),
    JOUEUR ("JOUEUR");

    private String valeur;

    EntityType(String valeur) {
        this.valeur = valeur;
    }

    public String getValeur() {
        return valeur;
    }
}
