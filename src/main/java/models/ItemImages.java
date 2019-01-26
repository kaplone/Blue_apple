package models;

import constants.EntityType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemImages {

    private static Integer nextId;
    private static ColorAdjust desaturer;

    private Image imageDeBase;
    private Integer id;
    private EntityType type;
    private String url;

    static {
        nextId = 0;
        desaturer = new ColorAdjust();
        desaturer.setSaturation(-0.7);
        desaturer.setBrightness(0.5);
        desaturer.setContrast(0.4);
    }

    public ItemImages(EntityType type, String url) {
        this.type = type;
        this.url = url;
        this.id = nextId;
        nextId ++;
    }

    public ImageView getImageView(Double saturation, Integer height){
        imageDeBase = new Image(url, height, height, true, true);
        ImageView imageView =  new ImageView(imageDeBase);
        imageView.setEffect(desaturer);
        return imageView;
    }
}
