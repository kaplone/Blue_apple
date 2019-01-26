package app;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.Timer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import constants.EntityType;
import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    private Entity pomme;
    private GameSettings settings;
    private Integer sizeX = 1200;
    private Integer sizeY = 700;

    private Double vitesse;

    private DecimalFormat df;

    private Niveau niveau;
    private String[] keys;

    private Entity item;
    private List<Entity> items;

    private Timer timer;
    private VBox statistics;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(sizeX);
        settings.setHeight(sizeY);
        settings.setTitle("Blue apple");
    }

    @Override
    protected void initGame(){

        pommes = new ArrayList<>();
        items = new ArrayList<>();
        dalles = new HashMap<>();

        niveau = new Niveau();

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            niveau = mapper.readValue(new File("src/main/resources/niveaux/niveau_01.yml"), Niveau.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0f);

        Lighting l = new Lighting();
        l.setLight(light);
        l.setDiffuseConstant(0.5);
        l.setSurfaceScale(7.5f);

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

        player = Entities.builder()
                .type(JOUEUR)
                .at(niveau.getBorder() + niveau.getInit_h() * niveau.getEcart(),
                niveau.getBorder() + niveau.getInit_v() * niveau.getEcart())
                .viewFromTexture("fee_big.png")
                .buildAndAttach(getGameWorld());
        getGameState().setValue("caseMovedX", niveau.getInit_h());
        getGameState().setValue("caseMovedY", niveau.getInit_v());
        getGameState().setValue("aUneBaguette", false);
        getGameState().setValue("aLaCle1", false);
        getGameState().setValue("aLaCle2", false);
        getGameState().setValue("aLaCle3", false);

        for (Item item_ : niveau.getItems()) {
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
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Right") {

            @Override
            protected void onActionBegin() {
                Dalle nextDalle = dalles.get((getGameState().getInt("caseMovedX") + 1) + "_" + getGameState().getInt("caseMovedY"));
                Dalle actualDalle= dalles.get(getGameState().getInt("caseMovedX") + "_" + getGameState().getInt("caseMovedY"));

                Boolean mouvement = act(actualDalle, nextDalle);
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

                Boolean mouvement = act(actualDalle, nextDalle);
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

                Boolean mouvement = act(actualDalle, nextDalle);
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

                Boolean mouvement = act(actualDalle, nextDalle);
                if (mouvement){
                    getGameState().increment("caseMovedY", 1);
                }

            }
        }, KeyCode.DOWN);
    }

    private boolean act(Dalle actualDalle, Dalle nextDalle){
        Boolean mouvement = false;
        if (getGameState().getInt("caseMovedY") < niveau.getSize_v() - 1
                && !nextDalle.getTombee()
                &&  (getGameState().getBoolean("aUneBaguette") || !nextDalle.getType().equals(EntityType.FANTOME))){
            mouvement = true;
            player.setY(niveau.getBorder() + getGameState().getInt("caseMovedY") * niveau.getEcart());

            actualDalle.setTombee(true);
            nextDalle.setTombee(true);
            getGameWorld().removeEntities(actualDalle.getEntite(), nextDalle.getMotif());



            String image;
            if (nextDalle.getType().equals(EntityType.FANTOME)){
                getGameState().setValue("aUneBaguette", false);
            }
            if (nextDalle.getType().equals(EntityType.BAGUETTE)){
                getGameState().setValue("aUneBaguette", true);
            }
            if (getGameState().getBoolean("aUneBaguette")){
                image = "fee_baguette_big.png";
            }
            else {
                image = "fee_big.png";
            }
            player.removeFromWorld();
            player = Entities.builder()
                    .type(JOUEUR)
                    .at(nextDalle.getEntite().getX(),
                            nextDalle.getEntite().getY())
                    .viewFromTexture(image)
                    .buildAndAttach(getGameWorld());
        }
        else {
            player.removeFromWorld();
            player = Entities.builder()
                    .type(JOUEUR)
                    .at(player.getX(),
                            player.getY())
                    .viewFromTexture("fee_triste_big.png")
                    .buildAndAttach(getGameWorld());
        }
        return mouvement;
    }

    @Override
    protected void initUI() {

        df = new DecimalFormat("00.00");

        statistics = new VBox();
        statistics.setSpacing(5);
        statistics.setStyle("-fx-padding: 3;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        statistics.setTranslateX(650); // x = 700
        statistics.setTranslateY(50); // y = 100

        String[] labels = new String[] {"Pommes", "Pomme bleue", "Clé(s)",};

        ItemImages itemImagesPomme1 = new ItemImages(EntityType.POMME, "assets/textures/" + "pomme1_big.png");
        ItemImages itemImagesPomme2 = new ItemImages(EntityType.POMME, "assets/textures/" + "pomme2_big.png");
        ItemImages itemImagesPomme3 = new ItemImages(EntityType.POMME, "assets/textures/" + "pomme3_big.png");
        ItemImages itemImagesPomme4 = new ItemImages(EntityType.POMME, "assets/textures/" + "pomme4_big.png");
        ItemImages itemImagesCle = new ItemImages(EntityType.CLE, "assets/textures/" + "cle_1_neutre_big.png");


        HBox hboxPommes = new HBox();
        hboxPommes.getChildren().addAll(
                itemImagesPomme1.getImageView(-0.7, 60),
                itemImagesPomme2.getImageView(-0.7, 60),
                itemImagesPomme3.getImageView(-0.7, 60)
        );
        HBox hboxPommeBleue = new HBox();
        hboxPommeBleue.getChildren().addAll(
                itemImagesPomme4.getImageView(-0.7, 60)
        );

        HBox hboxCle = new HBox();
        hboxCle.getChildren().addAll(
                itemImagesCle.getImageView(-0.7, 60)
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
