package models;

import javafx.scene.paint.Color;

import java.util.List;

public class Niveau {

    private List<Item> items;
    private String nom;
    Integer size_v;
    Integer size_h;
    Integer border;
    Integer ecart;
    Integer init_v;
    Integer init_h;
    String couleur;

    public Integer getInit_v() {
        return init_v;
    }

    public void setInit_v(Integer init_v) {
        this.init_v = init_v;
    }

    public Integer getInit_h() {
        return init_h;
    }

    public void setInit_h(Integer init_h) {
        this.init_h = init_h;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public Integer getBorder() {
        return border;
    }

    public void setBorder(Integer border) {
        this.border = border;
    }

    public Integer getEcart() {
        return ecart;
    }

    public void setEcart(Integer ecart) {
        this.ecart = ecart;
    }

    public Integer getSize_v() {
        return size_v;
    }

    public void setSize_v(Integer size_v) {
        this.size_v = size_v;
    }

    public Integer getSize_h() {
        return size_h;
    }

    public void setSize_h(Integer size_h) {
        this.size_h = size_h;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
