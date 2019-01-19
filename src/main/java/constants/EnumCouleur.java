package constants;

import javafx.scene.paint.Color;

public enum EnumCouleur {
    ROUGE (Color.ORANGERED),
    ORANGE (Color.ORANGE),
    JAUNE (Color.YELLOW),
    VERT_1 (Color.LIME),
    VERT_2 (Color.LIMEGREEN);

    private Color couleur;

    EnumCouleur (Color couleur){
        this.couleur = couleur;
    }

    public Color getCouleur() {
        return couleur;
    }

    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }
}
