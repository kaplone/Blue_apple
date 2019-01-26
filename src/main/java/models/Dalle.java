package models;

import com.almasb.fxgl.entity.Entity;
import constants.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Dalle {

    private Boolean tombee;
    private Entity entite;
    private Entity motif;
    private EntityType type;
    private List<Entity> shine;

    public Dalle(Entity entite) {
        this.tombee = false;
        this.entite = entite;
        this.motif = new Entity();
        this.shine = new ArrayList<>();
    }

    public List<Entity> getShine() {
        return shine;
    }

    public void addShine(Entity shine) {
        this.shine.add(shine);
    }

    public void clearShine() {
        shine.forEach(a -> a.removeFromWorld());
        this.shine.clear();
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
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
