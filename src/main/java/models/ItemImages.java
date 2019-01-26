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
    private ImageView imageViewDefaut;

    static {
        nextId = 0;
    }

    public ItemImages(EntityType type, String url) {
        this.type = type;
        this.url = url;
        this.id = nextId;
        nextId ++;
        imageViewDefaut = getImageView(- 0.8d, 60);
    }

    public ImageView getImageViewDefaut() {
        return imageViewDefaut;
    }

    public ImageView getImageView(Double saturation, Integer height){
        imageDeBase = new Image(url, height, height, true, true);
        ColorAdjust desaturerBis = new ColorAdjust();
        desaturerBis.setSaturation(saturation);
        desaturerBis.setBrightness(0);
        desaturerBis.setContrast(0);

        ImageView imageView =  new ImageView(imageDeBase);
        imageView.setEffect(desaturerBis);
        return imageView;
    }
}
