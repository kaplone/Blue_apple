package models;

import constants.EntityType;

public class Item {

    private String imagePath;
    private Integer xPos;
    private Integer yPos;
    private EntityType type;

    public Boolean isStopped() {
        return EntityType.CACTUS.equals(type);
    }

    public EntityType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = EntityType.valueOf(type);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getxPos() {
        return xPos;
    }

    public void setxPos(Integer xPos) {
        this.xPos = xPos;
    }

    public Integer getyPos() {
        return yPos;
    }

    public void setyPos(Integer yPos) {
        this.yPos = yPos;
    }
}
