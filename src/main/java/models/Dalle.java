package models;

import com.almasb.fxgl.entity.Entity;

public class Dalle {

    private Boolean tombee;
    private Entity entite;
    private Entity motif;

    public Dalle(Entity entite) {
        this.tombee = false;
        this.entite = entite;
        this.motif = new Entity();
    }

    public Entity getMotif() {
        return motif;
    }

    public void setMotif(Entity motif) {
        this.motif = motif;
    }

    public Entity getEntite() {
        return entite;
    }

    public void setEntite(Entity entite) {
        this.entite = entite;
    }

    public Boolean getTombee() {
        return tombee;
    }

    public void setTombee(Boolean tombee) {
        this.tombee = tombee;
    }
}
