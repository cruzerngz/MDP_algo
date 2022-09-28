package Entities;

public class Robot extends Entity {

    int width;
    int height;
    int[][] padding;
    int paddingSize;

    public Robot(int width, int height) {
        this.width = width;
        this.height = height;
        this.paddingSize = (int) Math.floor(height / 2);

        super.type = Type.ROBOT;
        super.symbol = "#";
    }

    public int[] getSize() {
        return new int[] { width, height };
    }

    public void setCoords(int x, int y) {
        super.x = x;
        super.y = y;
        setPadding();
    }

    /*
     * Padding refers to the coordinates, other than the center of the robot,
     * that the robot occupies due to its size
     */
    private void setPadding() {
        int startX = (int) (x - Math.floor(width / 2));
        int startY = (int) (y - Math.floor(height / 2));
        int index = 0;
        padding = new int[width * height][2];
        for (int i = startX; i < startX + width; i++) {
            for (int j = startY; j < startY + height; j++) {
                int[] paddingCoord = { i, j };
                padding[index] = paddingCoord;
                index++;
            }
        }
    }

    public int[][] getPadding() {
        return padding;
    }

    public int getPaddingSize() {
        return paddingSize;
    }

}
