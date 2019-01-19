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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import models.Item;
import models.Niveau;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static constants.EntityType.*;

public class Main extends GameApplication {

    private Entity player;
    private List<Entity> pommes;
    private Entity pomme;
    private GameSettings settings;
    private Integer sizeX = 800;
    private Integer sizeY = 600;

    private Double vitesse;

    private DecimalFormat df;

    private Niveau niveau;
    private String[] keys;

    private Entity item;
    private List<Entity> items;

    private Timer timer;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Blue apple");
    }

    @Override
    protected void initGame(){

        pommes = new ArrayList<>();
        items = new ArrayList<>();

        niveau = new Niveau();

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            niveau = mapper.readValue(new File("src/main/resources/niveaux/niveau_01.yml"), Niveau.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int y = 0; y < niveau.getSize_v(); y++){
            for (int x = 0; x < niveau.getSize_h(); x++){
                Entity uneCase = Entities.builder()
                        .type(CASE)
                        .at(niveau.getBorder() + x * niveau.getEcart(), niveau.getBorder() + y * niveau.getEcart())
                        .viewFromNode(new Rectangle(100, 100, Color.valueOf(niveau.getCouleur())))

                        .buildAndAttach(getGameWorld());
            }
        }

        player = Entities.builder()
                .type(JOUEUR)
                .at(20 + niveau.getBorder() + niveau.getInit_h() * niveau.getEcart(),
                20 + niveau.getBorder() + niveau.getInit_v() * niveau.getEcart())
                .viewFromNode(new Rectangle(60, 60, Color.DODGERBLUE))
                .buildAndAttach(getGameWorld());
        getGameState().setValue("caseMovedX", niveau.getInit_h());
        getGameState().setValue("caseMovedY", niveau.getInit_v());

        for (Item item_ : niveau.getItems()) {
            item = Entities.builder()
                    .type(item_.getType())
                    .at(15 + niveau.getBorder() + item_.getxPos() * niveau.getEcart(),
                        5 + niveau.getBorder() + item_.getyPos() * niveau.getEcart())
                    .viewFromTexture(item_.getImagePath())
                    .buildAndAttach(getGameWorld());
            items.add(item);
        }
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Right") {

            @Override
            protected void onActionBegin() {
                if (getGameState().getInt("caseMovedX") < niveau.getSize_h() - 1){
                    getGameState().increment("caseMovedX", 1);
                    player.setX(20 + niveau.getBorder() + getGameState().getInt("caseMovedX") * niveau.getEcart());
                }

            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Move Left") {

            @Override
            protected void onActionBegin() {
                if (getGameState().getInt("caseMovedX") > 0){
                    getGameState().increment("caseMovedX", -1);
                    player.setX(20 + niveau.getBorder() + getGameState().getInt("caseMovedX") * niveau.getEcart());
                }

            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Move Up") {

            @Override
            protected void onActionBegin() {
                if (getGameState().getInt("caseMovedY") > 0){
                    getGameState().increment("caseMovedY", -1);
                    player.setY(20 + niveau.getBorder() + getGameState().getInt("caseMovedY") * niveau.getEcart());
                }

            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Move Down") {

            @Override
            protected void onActionBegin() {
                if (getGameState().getInt("caseMovedY") < niveau.getSize_v() - 1){
                    getGameState().increment("caseMovedY", 1);
                    player.setY(20 + niveau.getBorder() + getGameState().getInt("caseMovedY") * niveau.getEcart());
                }

            }
        }, KeyCode.DOWN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
