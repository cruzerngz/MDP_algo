# Path Finding Algorithm

## Demo

1. Clone this repo and run `Main.java`
2. You will see a pop up window
3. Copy and paste the following onto the right most text box
    ```
    1 1 90
    5 7 270 17
    5 13 180 35
    12 9 0 31
    15 15 270 16
    15 4 90 34
    0 19 270 22
    ```
    This is the configuration file

    The first line represents [robot X coordinate, robot Y coordinate, robot direction]

    The subsequent lines reprsent [obstacle x coordinate, obstacle y coordinate, obstacle direction, obstacle photo id]

4. Click on the **Generate Arena From Configuration Button**
5. You should see (or may not see depending on how you placed the obstacles) a sample of the arena
6. Click the front and back arrows to see how the robot moves. Note that it does not capture diagonal movements well