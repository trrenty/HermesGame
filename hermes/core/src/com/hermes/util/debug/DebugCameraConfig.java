package com.hermes.util.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Logger;

public class DebugCameraConfig {

    private static final Logger log = new Logger(DebugCameraConfig.class.getName(), Logger.DEBUG);

    //names/keys in key-value map or names of default stuff in json
    private static final String MAX_ZOOM_IN = "maxZoomIn";
    private static final String MAX_ZOOM_OUT = "maxZoomOut";
    private static final String ZOOM_SPEED = "zoomSpeed";
    private static final String MOVE_SPEED = "moveSpeed";

    private static final String LEFT_KEY = "leftKey";
    private static final String RIGHT_KEY = "rightKey";
    private static final String UP_KEY = "upKey";
    private static final String DOWN_KEY = "downKey";

    private static final String ZOOM_IN_KEY = "zoomInKey";
    private static final String ZOOM_OUT_KEY = "zoomOutKey";

    private static final String RESET_KEY = "resetKey";
    private static final String LOG_KEY = "logKey";

    private static final int DEFAULT_LEFT_KEY = Input.Keys.A;
    private static final int DEFAULT_RIGHT_KEY = Input.Keys.D;
    private static final int DEFAULT_UP_KEY = Input.Keys.W;
    private static final int DEFAULT_DOWN_KEY = Input.Keys.S;

    private static final int DEFAULT_ZOOM_IN_KEY = Input.Keys.COMMA;
    private static final int DEFAULT_ZOOM_OUT_KEY = Input.Keys.PERIOD;

    private static final int DEFAULT_RESET_KEY = Input.Keys.BACKSPACE;
    private static final int DEFAULT_LOG_KEY = Input.Keys.ENTER;

    private static final float DEFAULT_MOV_SPEED = 20.0f;
    private static final float DEFAULT_ZOOM_SPEED = 2.0f;
    private static final float DEFAULT_MAX_ZOOM_IN = 0.20f;
    private static final float DEFAULT_MAX_ZOOM_OUT = 30f;

    private static final String FILE_PATH = "debug/debugCameraConfig.json";

    //attributes

    private float maxZoomIn;
    private float maxZoomOut;
    private float moveSpeed;
    private float zoomSpeed;

    private int leftKey;
    private int rightKey;
    private int upKey;
    private int downKey;

    private int zoomInKey;
    private int zoomOutKey;

    private int resetKey;
    private int logKey;

    private FileHandle fileHandle;

    public DebugCameraConfig() {
        init();
    }

    private void init() {
        fileHandle = Gdx.files.internal(FILE_PATH);

        if (fileHandle.exists()) {
            load();
        } else {
            log.info("Using defaults. File does not exist= " + FILE_PATH);
            setupDefaults();
        }
    }

    private void load() {
        try {
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            maxZoomIn = root.getFloat(MAX_ZOOM_IN, DEFAULT_MAX_ZOOM_IN);
            maxZoomOut = root.getFloat(MAX_ZOOM_OUT, DEFAULT_MAX_ZOOM_OUT);
            moveSpeed = root.getFloat(MOVE_SPEED, DEFAULT_MOV_SPEED);
            zoomSpeed = root.getFloat(ZOOM_SPEED, DEFAULT_ZOOM_SPEED);

            leftKey = getInputKeyValue(root, LEFT_KEY, DEFAULT_LEFT_KEY);
            rightKey = getInputKeyValue(root, RIGHT_KEY, DEFAULT_RIGHT_KEY);
            upKey = getInputKeyValue(root, UP_KEY, DEFAULT_UP_KEY);
            downKey = getInputKeyValue(root, DOWN_KEY, DEFAULT_DOWN_KEY);

            zoomInKey = getInputKeyValue(root, ZOOM_IN_KEY, DEFAULT_ZOOM_IN_KEY);
            zoomOutKey = getInputKeyValue(root, ZOOM_OUT_KEY, DEFAULT_ZOOM_OUT_KEY);

            resetKey = getInputKeyValue(root, RESET_KEY, DEFAULT_RESET_KEY);
            logKey = getInputKeyValue(root, LOG_KEY, DEFAULT_LOG_KEY);

        } catch (Exception e) {
            log.error("Error loading " + FILE_PATH + " using default.", e);
            setupDefaults();
        }
    }

    private void setupDefaults() {
        maxZoomIn = DEFAULT_MAX_ZOOM_IN;
        maxZoomOut = DEFAULT_ZOOM_OUT_KEY;
        moveSpeed = DEFAULT_MOV_SPEED;
        zoomSpeed = DEFAULT_ZOOM_SPEED;

        leftKey = DEFAULT_LEFT_KEY;
        rightKey = DEFAULT_RIGHT_KEY;
        upKey = DEFAULT_UP_KEY;
        downKey = DEFAULT_DOWN_KEY;

        zoomInKey = DEFAULT_ZOOM_IN_KEY;
        zoomOutKey = DEFAULT_ZOOM_OUT_KEY;
        resetKey = DEFAULT_RESET_KEY;
        logKey = DEFAULT_LOG_KEY;
    }

    public static String getRightKey() { return RIGHT_KEY; }

    public float getMaxZoomIn() { return maxZoomIn; }

    public float getMaxZoomOut() { return maxZoomOut; }

    public float getMoveSpeed() { return moveSpeed; }

    public float getZoomSpeed() { return zoomSpeed; }

    public boolean isLeftPressed() { return Gdx.input.isKeyPressed(leftKey); }

    public boolean isRightPressed() { return Gdx.input.isKeyPressed(rightKey); }

    public boolean isUpPressed() { return Gdx.input.isKeyPressed(upKey); }

    public boolean isDownPressed() { return Gdx.input.isKeyPressed(downKey); }

    public boolean isZoomInPressed() { return Gdx.input.isKeyPressed(zoomInKey); }

    public boolean isZoomOutPressed() { return Gdx.input.isKeyPressed(zoomOutKey); }

    public boolean isResetPressed() { return Gdx.input.isKeyPressed(resetKey); }

    public boolean isLogPressed() { return Gdx.input.isKeyPressed(logKey); }

    @Override
    public String toString() {
        String LS = System.getProperty("line.separator");
        return "DebugCameraConfig ( " + LS +
                "maxZoomIn=" + maxZoomIn + LS +
                "maxZoomOut=" + maxZoomOut + LS +
                "moveSpeed=" + moveSpeed + LS +
                "zoomSpeed=" + zoomSpeed + LS +
                "leftKey=" + Input.Keys.toString(leftKey) + LS +
                "rightKey=" + Input.Keys.toString(rightKey) + LS +
                "upKey=" + Input.Keys.toString(upKey) + LS +
                "downKey=" + Input.Keys.toString(downKey) + LS +
                "zoomInKey=" + Input.Keys.toString(zoomInKey) + LS +
                "zoomOutKey=" + Input.Keys.toString(zoomOutKey) + LS +
                "resetKey=" + Input.Keys.toString(resetKey) + LS +
                "logKey=" + Input.Keys.toString(logKey)  + LS +
                ")";
    }

    private static int getInputKeyValue(JsonValue root, String key, int defaultKey) {
        String keyString = root.getString(key, Input.Keys.toString(defaultKey));
        return Input.Keys.valueOf(keyString);
    }
}
