package app;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.Timer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import constants.EntityType;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import models.Dalle;
import models.Item;
import models.ItemImages;
import models.Niveau;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static constants.EntityType.*;

public class Main extends GameApplication {

    private Entity player;
    private List<Entity> pommes;
    private Map<String, Dalle> dalles;
    private GameSettings settings;
    private Integer sizeX = 1200;
    private Integer sizeY = 700;

    private DecimalFormat df;

    private Niveau niveau;
    private String[] keys;

    private Entity item;
    private List<Entity> items;

    private Timer timer;
    private VBox statistics;

    private ItemImages itemImagesPomme1;
    private ItemImages itemImagesPomme2;
    private ItemImages itemImagesPomme3;
    private ItemImages itemImagesPomme4;
    private ItemImages itemImagesCleNeutre;
    private ItemImages itemImagesCle1;
    private ItemImages itemImagesCle2;
    private ItemImages itemImagesFantome;
    private ItemImages itemImagesFantomeAie;

    private Integer pommesOK;

    private String[] labels;

    private Integer level = 1;

    private List<Dalle> shineToRemove;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(sizeX);
        settings.setHeight(sizeY);
        settings.setTitle("Blue apple");
    }

    @Override
    protected void initGame(){

        shineToRemove = new ArrayList<>();

        pommes = new ArrayList<>();
        items = new ArrayList<>();
        dalles = new HashMap<>();

        pommesOK = 0;

        niveau = new Niveau();

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            niveau = mapper.readValue(new File(String.format("src/main/resources/niveaux/niveau_%02d.yml", level)), Niveau.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0f);

        Lighting l = new Lighting();
        l.setLight(light);
        l.setDiffuseConstant(0.5);
        l.setSurfaceScale(7.5f);

        dalles.clear();
        items.clear();

        for (int y = 0; y < niveau.getSize_v(); y++){
            for (int x = 0; x < niveau.getSize_h(); x++){
                Rectangle r = new Rectangle(100, 100, Color.valueOf(niveau.getCouleur()));
                r.setArcHeight(25);
                r.setArcWidth(25);
                r.setEffect(l);
                Entity uneCase = Entities.builder()
                        .type(CASE)
                        .at(niveau.getBorder() + x * niveau.getEcart(), niveau.getBorder() + y * niveau.getEcart())
                        .viewFromNode(r)

                        .buildAndAttach(getGameWorld());
                dalles.put(x + "_" + y, new Dalle(uneCase));
            }
        }

        for (Item item_ : niveau.getItems()) {
            if (!EntityType.FEE.equals(item_.getType())){
                item = Entities.builder()
                        .type(item_.getType())
                        .at(niveau.getBorder() + item_.getxPos() * niveau.getEcart(),
                                niveau.getBorder() + item_.getyPos() * niveau.getEcart())
                        .viewFromTexture(item_.getImagePath())
                        .buildAndAttach(getGameWorld());
                items.add(item);
                dalles.get(item_.getxPos() + "_" + item_.getyPos()).setMotif(item);
                dalles.get(item_.getxPos() + "_" + item_.getyPos()).setType(item_.getType());
            }
            else {
                if (player != null){
                    player.removeFromWorld();
                }
                player = Entities.builder()
                        .type(JOUEUR)
                        .at(niveau.getBorder() + niveau.getInit_h() * niveau.getEcart(),
                                niveau.getBorder() + niveau.getInit_v() * niveau.getEcart())
                        .viewFromTexture("fee_big.png")
                        .buildAndAttach(getGameWorld());
                dalles.get(item_.getxPos() + "_" + item_.getyPos()).setMotif(player);
                dalles.get(item_.getxPos() + "_" + item_.getyPos()).setType(item_.getType());
                getGameState().clear();
                getGameState().setValue("caseMovedX", niveau.getInit_h());
                getGameState().setValue("caseMovedY", niveau.getInit_v());
                getGameState().setValue("aUneBaguette", false);
                getGameState().setValue("aLaCle1", false);
                getGameState().setValue("aLaCle2", false);
                getGameState().setValue("aLaCle3", false);
            }
        }
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Reset") {

            @Override
            protected void onActionBegin() {
                getGameScene().clear();
                initGame();
                initUI();
            }
        }, KeyCode.BACK_SPACE);

        input.addAction(new UserAction("Move Right") {

            @Override
            protected void onActionBegin() {
                Dalle nextDalle = dalles.get((getGameState().getInt("caseMovedX") + 1) + "_" + getGameState().getInt("caseMovedY"));
                Dalle actualDalle= dalles.get(getGameState().getInt("caseMovedX") + "_" + getGameState().getInt("caseMovedY"));

                Boolean pasAuBord = getGameState().getInt("caseMovedX") < niveau.getSize_h() - 1;
                Boolean mouvement = act(pasAuBord, actualDalle, nextDalle);
                if (mouvement){
                    getGameState().increment("caseMovedX", + 1);
                }

            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Move Left") {

            @Override
            protected void onActionBegin() {
                Dalle nextDalle = dalles.get((getGameState().getInt("caseMovedX") - 1) + "_" + getGameState().getInt("caseMovedY"));
                Dalle actualDalle= dalles.get(getGameState().getInt("caseMovedX") + "_" + getGameState().getInt("caseMovedY"));

                Boolean pasAuBord = getGameState().getInt("caseMovedX") > 0;
                Boolean mouvement = act(pasAuBord, actualDalle, nextDalle);
                if (mouvement){
                    getGameState().increment("caseMovedX", - 1);
                }
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Move Up") {

            @Override
            protected void onActionBegin() {
                Dalle nextDalle = dalles.get(getGameState().getInt("caseMovedX") + "_" + (getGameState().getInt("caseMovedY") - 1));
                Dalle actualDalle= dalles.get(getGameState().getInt("caseMovedX") + "_" + getGameState().getInt("caseMovedY"));

                Boolean pasAuBord = getGameState().getInt("caseMovedY") > 0;
                Boolean mouvement = act(pasAuBord, actualDalle, nextDalle);
                if (mouvement){
                    getGameState().increment("caseMovedY", - 1);
                }
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Move Down") {

            @Override
            protected void onActionBegin() {
                Dalle nextDalle = dalles.get(getGameState().getInt("caseMovedX") + "_" + (getGameState().getInt("caseMovedY") + 1));
                Dalle actualDalle= dalles.get(getGameState().getInt("caseMovedX") + "_" + getGameState().getInt("caseMovedY"));

                Boolean pasAuBord = getGameState().getInt("caseMovedY") < niveau.getSize_v() - 1;
                Boolean mouvement = act(pasAuBord, actualDalle, nextDalle);
                if (mouvement){
                    getGameState().increment("caseMovedY", 1);
                }

            }
        }, KeyCode.DOWN);
    }

    private boolean act(Boolean bord, Dalle actualDalle, Dalle nextDalle){
        Boolean mouvement = false;
        if (bord
                && !nextDalle.getTombee()
                && (getGameState().getBoolean("aUneBaguette") || !nextDalle.getType().equals(EntityType.FANTOME))
                && (getGameState().getBoolean("aLaCle1") || !nextDalle.getType().equals(EntityType.CADENAS1))
                && (getGameState().getBoolean("aLaCle2") || !nextDalle.getType().equals(EntityType.CADENAS2))
                && (pommesOK == 3 || !nextDalle.getType().equals(EntityType.POMME4))){

            mouvement = true;

            actualDalle.setTombee(true);
            nextDalle.setTombee(true);
            getGameWorld().removeEntities(actualDalle.getEntite(), nextDalle.getMotif());

            ObservableList<Node> statisticPommes = ((HBox) statistics.getChildren().get(0)).getChildren();
            Iterator<Node> itPommePlusText = statisticPommes.iterator();
            itPommePlusText.next();
            HBox itPommeHbox = (HBox) itPommePlusText.next();
            Iterator<Node> itPomme = itPommeHbox.getChildren().iterator();

            ObservableList<Node> statisticPommeBleue = ((HBox) statistics.getChildren().get(1)).getChildren();
            ObservableList<Node> statisticCle= ((HBox) statistics.getChildren().get(2)).getChildren();

            int index = 0;

            switch (nextDalle.getType()){
                case FANTOME: getGameState().setValue("aUneBaguette", false);
                    List<Dalle> tuilesFantomeAie = dalles.values()
                            .stream()
                            .filter(a -> !a.getTombee() && (EntityType.FANTOME.equals(a.getType())))
                            .collect(Collectors.toList());
                    tuilesFantomeAie.forEach(a -> {
                        a.getMotif().removeFromWorld();
                        Entity aie = Entities.builder()
                                .type(FANTOME)
                                .at(a.getEntite().getX(),
                                        a.getEntite().getY())
                                .viewFromTexture("fantome_big.png")
                                .buildAndAttach(getGameWorld());
                        a.setMotif(aie);
                    });
                    break;
                case BAGUETTE: getGameState().setValue("aUneBaguette", true);
                    List<Dalle> tuilesFantome = dalles.values()
                            .stream()
                            .filter(a -> !a.getTombee() && (EntityType.FANTOME.equals(a.getType())))
                            .collect(Collectors.toList());
                    tuilesFantome.forEach(a -> {
                        a.getMotif().removeFromWorld();
                        Entity aie = Entities.builder()
                                .type(FANTOME)
                                .at(a.getEntite().getX(),
                                        a.getEntite().getY())
                                .viewFromTexture("fantome_aie_big.png")
                                .buildAndAttach(getGameWorld());
                        a.setMotif(aie);
                    });
                    break;
                case POMME1:
                    while (itPomme.hasNext()) {
                        ImageView pomme = (ImageView) itPomme.next();
                        if (pomme.equals(itemImagesPomme1.getImageViewDefaut())) {
                            pommesOK ++;
                            break;
                        }
                        index++;
                    }
                    if (pommesOK == 3){
                        Iterator<Node> pommeBleueIteraror =  statisticPommeBleue.iterator();
                        pommeBleueIteraror.next();
                        HBox pommeBleueHbox = (HBox) pommeBleueIteraror.next();
                        pommeBleueHbox.getChildren().set(0, itemImagesPomme4.getImageView(-0.5, 75));
                    }
                    itPommeHbox.getChildren().set(index, itemImagesPomme1.getImageView(0.2, 75));
                    break;
                case POMME2:
                    while (itPomme.hasNext()) {
                        ImageView pomme = (ImageView) itPomme.next();
                        if (pomme.equals(itemImagesPomme2.getImageViewDefaut())) {
                            pommesOK ++;
                            break;
                        }
                        index++;
                     }
                    if (pommesOK == 3){
                        Iterator<Node> pommeBleueIteraror =  statisticPommeBleue.iterator();
                        pommeBleueIteraror.next();
                        HBox pommeBleueHbox = (HBox) pommeBleueIteraror.next();
                        pommeBleueHbox.getChildren().set(0, itemImagesPomme4.getImageView(-0.5, 75));
                    }
                     itPommeHbox.getChildren().set(index, itemImagesPomme2.getImageView(0.2, 75));
                     break;
                case POMME3:
                    while (itPomme.hasNext()) {
                        ImageView pomme = (ImageView) itPomme.next();
                        if (pomme.equals(itemImagesPomme3.getImageViewDefaut())) {
                            pommesOK ++;
                            break;
                        }
                        index++;

                    }
                    if (pommesOK == 3){
                        Iterator<Node> pommeBleueIteraror =  statisticPommeBleue.iterator();
                        pommeBleueIteraror.next();
                        HBox pommeBleueHbox = (HBox) pommeBleueIteraror.next();
                        pommeBleueHbox.getChildren().set(0, itemImagesPomme4.getImageView(-0.5, 75));
                    }
                    itPommeHbox.getChildren().set(index, itemImagesPomme3.getImageView(0.2, 75));
                    break;
                case POMME4:
                    Iterator<Node> pommeBleueIteraror =  statisticPommeBleue.iterator();
                    pommeBleueIteraror.next();
                    HBox pommeBleueHbox = (HBox) pommeBleueIteraror.next();
                    pommeBleueHbox.getChildren().set(0, itemImagesPomme4.getImageView(0.2, 75));
                    level ++;
                    items.clear();
                    getGameScene().clear();
                    getGameState().clear();
                    getGameWorld().clear();
                    initGame();
                    initUI();
                    mouvement = false;
                    break;
                case CLE1: statisticCle.clear();
                    statisticCle.add(new Text(labels[2]));
                    statisticCle.add(itemImagesCle1.getImageView(0d, 50));
                    getGameState().setValue("aLaCle1", true);
                    List<Dalle> tuilesCadenas1 = dalles.values()
                            .stream()
                            .filter(a -> !a.getTombee() && (EntityType.CADENAS1.equals(a.getType())))
                            .collect(Collectors.toList());
                    tuilesCadenas1.forEach(a -> {
                        a.getMotif().removeFromWorld();
                        Entity cadenas_ouvert = Entities.builder()
                                .type(CADENAS)
                                .at(a.getEntite().getX(),
                                        a.getEntite().getY())
                                .viewFromTexture("cadenas_1_ouvert_big.png")
                                .buildAndAttach(getGameWorld());
                        a.setMotif(cadenas_ouvert);
                    });
                    List<Dalle> tuilesCadenas2 = dalles.values()
                            .stream()
                            .filter(a -> !a.getTombee() && (EntityType.CADENAS2.equals(a.getType())))
                            .collect(Collectors.toList());
                    tuilesCadenas2.forEach(a -> {
                        a.getMotif().removeFromWorld();
                        Entity cadenas_ouvert = Entities.builder()
                                .type(CADENAS)
                                .at(a.getEntite().getX(),
                                        a.getEntite().getY())
                                .viewFromTexture("cadenas_2_big.png")
                                .buildAndAttach(getGameWorld());
                        a.setMotif(cadenas_ouvert);
                    });
                    break;
                case CLE2: statisticCle.clear();
                    statisticCle.add(new Text(labels[2]));
                    statisticCle.add(itemImagesCle2.getImageView(0d, 50));
                    getGameState().setValue("aLaCle2", true);
                    List<Dalle> tuilesCadenas1_ = dalles.values()
                            .stream()
                            .filter(a -> !a.getTombee() && (EntityType.CADENAS1.equals(a.getType())))
                            .collect(Collectors.toList());
                    tuilesCadenas1_.forEach(a -> {
                        a.getMotif().removeFromWorld();
                        Entity cadenas_ouvert = Entities.builder()
                                .type(CADENAS)
                                .at(a.getEntite().getX(),
                                        a.getEntite().getY())
                                .viewFromTexture("cadenas_1_big.png")
                                .buildAndAttach(getGameWorld());
                        a.setMotif(cadenas_ouvert);
                    });
                    List<Dalle> tuilesCadenas2_ = dalles.values()
                            .stream()
                            .filter(a -> !a.getTombee() && (EntityType.CADENAS2.equals(a.getType())))
                            .collect(Collectors.toList());
                    tuilesCadenas2_.forEach(a -> {
                        a.getMotif().removeFromWorld();
                        Entity cadenas_ouvert = Entities.builder()
                                .type(CADENAS)
                                .at(a.getEntite().getX(),
                                        a.getEntite().getY())
                                .viewFromTexture("cadenas_2_ouvert_big.png")
                                .buildAndAttach(getGameWorld());
                        a.setMotif(cadenas_ouvert);
                    });
                    break;
                case CADENAS1: statisticCle.clear();
                    statisticCle.add(new Text(labels[2]));
                    statisticCle.add(itemImagesCleNeutre.getImageView(- 0.4d, 50));
                    getGameState().setValue("aLaCle1", false);
                    List<Dalle> tuilesCadenas1Used = dalles.values()
                            .stream()
                            .filter(a -> !a.getTombee() && (EntityType.CADENAS1.equals(a.getType())))
                            .collect(Collectors.toList());
                    tuilesCadenas1Used.forEach(a -> {
                        a.getMotif().removeFromWorld();
                        Entity cadenas_ouvert = Entities.builder()
                                .type(CADENAS)
                                .at(a.getEntite().getX(),
                                        a.getEntite().getY())
                                .viewFromTexture("cadenas_1_big.png")
                                .buildAndAttach(getGameWorld());
                        a.setMotif(cadenas_ouvert);
                    });
                    break;
                case CADENAS2: statisticCle.clear();
                    statisticCle.add(new Text(labels[2]));
                    statisticCle.add(itemImagesCleNeutre.getImageView(- 0.4d, 50));
                    getGameState().setValue("aLaCle2", false);
                    List<Dalle> tuilesCadenas2Used = dalles.values()
                            .stream()
                            .filter(a -> !a.getTombee() && (EntityType.CADENAS2.equals(a.getType())))
                            .collect(Collectors.toList());
                    tuilesCadenas2Used.forEach(a -> {
                        a.getMotif().removeFromWorld();
                        Entity cadenas_ouvert = Entities.builder()
                                .type(CADENAS)
                                .at(a.getEntite().getX(),
                                        a.getEntite().getY())
                                .viewFromTexture("cadenas_2_big.png")
                                .buildAndAttach(getGameWorld());
                        a.setMotif(cadenas_ouvert);
                    });
                    break;
            }

            shineToRemove.forEach(a -> {
                a.clearShine();
            });

            String image;
            if (getGameState().getBoolean("aUneBaguette")){
                image = "fee_baguette_big.png";
            }
            else {
                image = "fee_big.png";
            }

            if (mouvement){
                player.removeFromWorld();
                player = Entities.builder()
                        .type(JOUEUR)
                        .at(nextDalle.getEntite().getX(),
                                nextDalle.getEntite().getY())
                        .viewFromTexture(image)
                        .buildAndAttach(getGameWorld());
            }
        }
        else {
            player.removeFromWorld();
            player = Entities.builder()
                    .type(JOUEUR)
                    .at(player.getX(),
                            player.getY())
                    .viewFromTexture("fee_triste_big.png")
                    .buildAndAttach(getGameWorld());

            if (nextDalle != null && nextDalle.getType() != null){
                switch (nextDalle.getType()){
                    case FANTOME:
                        List<Dalle> tuilesBaguettes = dalles.values()
                                .stream()
                                .filter(a -> !a.getTombee() && EntityType.BAGUETTE.equals(a.getType()))
                                .collect(Collectors.toList());
                        tuilesBaguettes.forEach(a -> {
                            a.getMotif().removeFromWorld();
                            Entity shine = Entities.builder()
                                    .type(SHINE)
                                    .at(a.getEntite().getX(),
                                            a.getEntite().getY())
                                    .viewFromTexture("shine.png")
                                    .buildAndAttach(getGameWorld());
                            getGameWorld().addEntity(a.getMotif());
                            a.addShine(shine);
                        });
                        shineToRemove.addAll(tuilesBaguettes);
                        break;
                    case POMME4:
                        List<Dalle> tuilesPommes = dalles.values()
                                .stream()
                                .filter(a -> !a.getTombee() && (EntityType.POMME1.equals(a.getType())
                                        || EntityType.POMME2.equals(a.getType())
                                        || EntityType.POMME3.equals(a.getType())))
                                .collect(Collectors.toList());
                        tuilesPommes.forEach(a -> {
                            a.getMotif().removeFromWorld();
                            Entity shine = Entities.builder()
                                    .type(SHINE)
                                    .at(a.getEntite().getX(),
                                            a.getEntite().getY())
                                    .viewFromTexture("shine.png")
                                    .buildAndAttach(getGameWorld());
                            getGameWorld().addEntity(a.getMotif());
                            a.addShine(shine);
                        });
                        shineToRemove.addAll(tuilesPommes);
                        break;
                    case CADENAS1 :
                        List<Dalle> tuilesCle1 = dalles.values()
                                .stream()
                                .filter(a -> !a.getTombee() && EntityType.CLE1.equals(a.getType()))
                                .collect(Collectors.toList());
                        tuilesCle1.forEach(a -> {
                            a.getMotif().removeFromWorld();
                            Entity shine = Entities.builder()
                                    .type(SHINE)
                                    .at(a.getEntite().getX(),
                                            a.getEntite().getY())
                                    .viewFromTexture("shine.png")
                                    .buildAndAttach(getGameWorld());
                            getGameWorld().addEntity(a.getMotif());
                            a.addShine(shine);
                        });
                        shineToRemove.addAll(tuilesCle1);
                        break;
                    case CADENAS2:
                        List<Dalle> tuilesCle2 = dalles.values()
                                .stream()
                                .filter(a -> !a.getTombee() && EntityType.CLE2.equals(a.getType()))
                                .collect(Collectors.toList());
                        tuilesCle2.forEach(a -> {
                            a.getMotif().removeFromWorld();
                            Entity shine = Entities.builder()
                                    .type(SHINE)
                                    .at(a.getEntite().getX(),
                                            a.getEntite().getY())
                                    .viewFromTexture("shine.png")
                                    .buildAndAttach(getGameWorld());
                            getGameWorld().addEntity(a.getMotif());
                            a.addShine(shine);
                        });
                        shineToRemove.addAll(tuilesCle2);
                        break;
                }
            }
        }
        return mouvement;
    }

    @Override
    protected void initUI() {

        Text titre = new Text();
        titre.setText("Niveau " + level);
        titre.setFont(new Font(40));
        titre.setX(20);
        titre.setY(40);

        Text sous_titre = new Text();
        sous_titre.setText("La fée doit atteindre la pomme bleue ...");
        sous_titre.setFont(new Font(25));
        sous_titre.setX(30);
        sous_titre.setY(70);

        getGameScene().addUINodes(titre, sous_titre);

        df = new DecimalFormat("00.00");

        statistics = new VBox();
        statistics.setSpacing(5);
        statistics.setStyle("-fx-padding: 3;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        statistics.setTranslateX(650); // x = 700
        statistics.setTranslateY(50); // y = 100

        labels = new String[] {"Pommes", "Pomme bleue", "Clé(s)",};

        itemImagesPomme1 = new ItemImages(EntityType.POMME, "assets/textures/" + "pomme1_big.png");
        itemImagesPomme2 = new ItemImages(EntityType.POMME, "assets/textures/" + "pomme2_big.png");
        itemImagesPomme3 = new ItemImages(EntityType.POMME, "assets/textures/" + "pomme3_big.png");
        itemImagesPomme4 = new ItemImages(EntityType.POMME, "assets/textures/" + "pomme4_big.png");
        itemImagesCleNeutre = new ItemImages(EntityType.CLE, "assets/textures/" + "cle_1_neutre_big.png");
        itemImagesCle1 = new ItemImages(EntityType.CLE, "assets/textures/" + "cle_1_big.png");
        itemImagesCle2 = new ItemImages(EntityType.CLE, "assets/textures/" + "cle_2_big.png");
        itemImagesFantome = new ItemImages(EntityType.CLE, "assets/textures/" + "fantome_big.png");
        itemImagesFantomeAie = new ItemImages(EntityType.CLE, "assets/textures/" + "fantome_aie_big.png");


        HBox hboxPommes = new HBox();
        hboxPommes.getChildren().addAll(
                itemImagesPomme1.getImageViewDefaut(),
                itemImagesPomme2.getImageViewDefaut(),
                itemImagesPomme3.getImageViewDefaut()
        );
        HBox hboxPommeBleue = new HBox();
        hboxPommeBleue.getChildren().addAll(
                itemImagesPomme4.getImageView(-0.7, 60)
        );

        HBox hboxCle = new HBox();
        hboxCle.getChildren().addAll(
                itemImagesCleNeutre.getImageView(-0.7, 60)
        );

        HBox[] icones = new HBox[] {
                hboxPommes,
                hboxPommeBleue,
                hboxCle,
        };

        for (int i = 0; i < 3; i++) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setSpacing(20);
            hbox.setStyle("-fx-padding: 2;" + "-fx-border-style: solid inside;"
                    + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: black;");
            Text text = new Text();
            text.setText(labels[i]);
            hbox.getChildren().addAll(text, icones[i]);
            statistics.getChildren().addAll(hbox);
        }

        getGameScene().addUINode(statistics); // add to the scene graph
    }

    public static void main(String[] args) {
        launch(args);
    }
}
