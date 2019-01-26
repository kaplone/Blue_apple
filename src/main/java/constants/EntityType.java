package constants;

public enum EntityType {
    CASE ("CASE"),
    CLE ("CLE"),
    CADENAS ("CADENAS"),
    HACHE ("HACHE"),
    BAGUETTE ("BAGUETTE"),
    CACTUS ("CACTUS"),
    POMME ("POMME"),
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
