package com.hermes.assets.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hermes.assets.AssetDescriptors;
import com.hermes.config.GameConfig;
import games.rednblack.editor.renderer.resources.AsyncResourceManager;

import java.util.HashMap;
import java.util.MissingResourceException;

public class GUIScene extends ApplicationAdapter {


    private static final Logger log = new Logger(GUIScene.class.getName(), Logger.DEBUG);

    private Stage stage;
    private I18NBundle bundle;

    private AssetManager assetManager;
    private Table upTable;
    private Label upLabel;
    private Image upImage;



    private Label downLabel;
    private Table healthTable;
    private Table taskTable;
    private Table itemTable;
    private Label notifierLabel;
    private Skin skin;
    private final HashMap<String, CheckBox> tasks = new HashMap<>();
    private final HashMap<String, Image> items = new HashMap<>();
    private final HashMap<String, Label> itemCount = new HashMap<>();

    public static final GUIScene INSTANCE = new GUIScene();
    private Table bigTable;

    private GUIScene() {

    }

    public void create(AssetManager assetManager, Viewport uiViewport, Batch batch) {
        this.assetManager = assetManager;
        bundle = assetManager.get(AssetDescriptors.I18N_BUNDLE);
        bundle.getLocale();

        stage = new Stage(uiViewport, batch);
        skin = assetManager.get(AssetDescriptors.GUI_SKIN);

        bigTable = new Table();
        bigTable.setFillParent(true);

        healthTable = new Table();
        healthTable.align(Align.topLeft);

        bigTable.add(healthTable).growX().expandY().align(Align.topLeft);

//        upLabel = new Label("dialogue", skin, "small");
//        upLabel.setAlignment(Align.left);
//        upLabel.setWrap(true);
        upTable = new Table();
        upTable.setBackground(skin.getDrawable("unit-speechBubble"));
        upTable.setName(ActorNames.DIALOGUE);

        upLabel = new Label("as", skin, "code");
        upLabel.setAlignment(Align.center);
        upLabel.setWrap(true);

        upImage = new Image(skin, "unit-speechBubble");
        upImage.setScaling(Scaling.fit);

        bigTable.add(upTable).growX().expandY().align(Align.top).colspan(3);
//        upLabel.setVisible(false);


        upTable.getColor().a = 0;


        itemTable = new Table();
        itemTable.align(Align.topRight);

        bigTable.add(itemTable).growX().expandY().align(Align.topRight);

        bigTable.row();
        taskTable = new Table();
        taskTable.align(Align.topLeft);
//        taskTable.setVisible(false);
        taskTable.getColor().a = 0;


        bigTable.add(taskTable).growX().expandY().align(Align.topLeft).uniformX();

        bigTable.add().growX();

        downLabel = new Label("Lorem ipsum", skin);
        downLabel.setName(ActorNames.INFO);
//        downLabel.setVisible(false);
        downLabel.getColor().a = 0;
        bigTable.add(downLabel).expandX().align(Align.bottom);

        bigTable.add().growX();

        Table table1 = new Table();
        table1.align(Align.bottomRight);

        notifierLabel = new Label(bundle.format(BundleKeys.NEW_TASK), skin, "simple");
//        addTask(BundleKeys.NEW_TASK);
        notifierLabel.getColor().a = 0;
        table1.add(notifierLabel).align(Align.bottomRight);

        bigTable.add(table1).growX().align(Align.bottomRight).uniformX().padBottom(20f).padRight(20f);

        bigTable.pack();

        stage.addActor(bigTable);
    }

    public CheckBox addTask(String taskName, Object ... args) {
        CheckBox checkBox = new CheckBox(bundle.format(taskName, args), skin);
        checkBox.setName(taskName);
        checkBox.setChecked(false);
        tasks.put(taskName, checkBox);
        taskTable.add(checkBox).align(Align.topLeft).padLeft(30).row();

        notifyPlayer(BundleKeys.NEW_TASK);

//        checkBox.addAction(new SequenceAction(Actions.fadeIn(0.5f), Actions.fadeOut(1f)));
        return checkBox;
    }


    public void notifyPlayer(String infoKey, String ... args) {
        notifierLabel.setText(bundle.format(infoKey, args));
        notifierLabel.addAction(
                Actions.sequence(
                        Actions.fadeIn(GameConfig.UI_ANIM_SPEED_FAST),
                        Actions.delay(1f),
                        Actions.fadeOut(GameConfig.UI_ANIM_SPEED_FAST)
                )
        );
    }

    public void checkTask(String taskName) {
        CheckBox task = tasks.get(taskName);
        if (task != null) {
            task.setChecked(true);
            notifyPlayer(BundleKeys.TASK_DONE);

        }
    }

    public void addItem(String itemId, TextureRegion region) {
        if (region == null) return;
        log.debug("item added");
        if (items.get(itemId) != null) {
            int nr = Integer.parseInt(itemCount.get(itemId).getText().toString());
            itemCount.get(itemId).setText(++nr);
        } else {
            Image image = new Image(region);
            image.setScaling(Scaling.fit);
            itemTable.add(image).maxSize(100.0f);
            items.put(itemId, image);
            notifyPlayer(BundleKeys.NEW_ITEM);

            // test
            Label label = new Label("1", skin, "simple");

            label.setPosition(1870 , 940);

            for (Label value : itemCount.values()) {
                value.setPosition(value.getX() - 100, value.getY());
            }

            bigTable.addActor(label);
            bigTable.pack();
            itemCount.put(itemId, label);
        }
    }

    public void removeItem(String itemId) {
        Image img = items.get(itemId);
        if (img == null) return;
        itemTable.removeActor(img);
        items.remove(itemId);
        bigTable.removeActor(itemCount.get(itemId));
        itemCount.remove(itemId);
    }

    public void addHealth() {
        addHealth(1);
    }

    public void addHealth(int n) {
        for (int i = 0; i < n; i++) {
            Image image = new Image(skin, "health-thing");
            image.setScaling(Scaling.fit);
            healthTable.add(image).maxSize(100.0f);
        }
    }

    public void removeHealth(int n) {
        for (int i = 0; i < n; i++) {
            removeHealth();
        }
    }

    public void removeHealth() {
        if (!healthTable.hasChildren()) return;
        removeHealthRecursively(0);
    }

    private void removeHealthRecursively(int index) {
        if (index >= healthTable.getChildren().size) {
            return;
        }
        Actor actor = healthTable.getChild(index);
        if (actor.hasActions()) {
            removeHealthRecursively(++index);
        } else {
            actor.addAction(new ParallelAction(Actions.fadeOut(0.5f), Actions.moveBy(actor.getX(), actor.getY() - 50, 0.5f)));
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (healthTable.hasChildren()) {
                        healthTable.removeActorAt(0, false);
                    }
                }
            }, 1f);
        }

    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        tasks.clear();
        items.clear();
        itemCount.clear();
    }

    public void showTasks(boolean show) {
        if (show) {
            taskTable.addAction(Actions.fadeIn(GameConfig.UI_ANIM_SPEED_FAST));
            for (CheckBox value : tasks.values()) {
                if (value.isChecked()) {
                    Action action = new Action() {
                        @Override
                        public boolean act(float delta) {
                            taskTable.removeActor(value);
                            return true;
                        }
                    };
                    value.addAction(Actions.delay(2f, action));
                }
            }
        } else {
            taskTable.addAction(Actions.fadeOut(GameConfig.UI_ANIM_SPEED_FAST));
        }
    }

    public void setDownLabel(String bundleKey, Object ... params) {
        downLabel.setText(bundle.format(bundleKey, params));
//        downLabel.getColor().a = 1;
        downLabel.addAction(Actions.fadeIn(GameConfig.UI_ANIM_SPEED_FAST));
//        Actions.fadeOut(GameConfig.UI_ANIM_SPEED);
//        downLabel.setVisible(true);
    }

    public void setText(CheckBox actor, String bundleKey, Object ...params) {
        actor.setText(bundle.format(bundleKey, params));
    }

    public void setDialogueLabel(String bundleKey, Object ... params) {
        setDialogueLabel(bundleKey, "dialogue", params);
    }

    public void setDialogueLabel(String bundleKey,String style, Object ... params) {
        setDialogueText(bundle.format(bundleKey, params), style);
//        upLabel.getColor().a = 1;
//        upLabel.setVisible(true);
    }
    public void setDialogueText(String text) {
        setDialogueText(text, "dialogue");
    }

    public int getNrOfLabelsWithPrefix(String prefix) {
        for (int i = 0; i < 10; i++) {
            try {
                bundle.get(prefix + i);
            } catch (MissingResourceException ignore) {
                return i;
            }

        }
        return 10;
    }

    public void setDialogueText(String text, String style) {
        try {
            upLabel.setStyle(skin.get(style, Label.LabelStyle.class));
            log.debug(style);
        } catch (GdxRuntimeException e) {
            log.debug("exception");
            upLabel.setStyle(skin.get("dialogue", Label.LabelStyle.class));
        }
        upLabel.setText(text);

        upTable.clearChildren();
        upTable.add(upLabel).grow();
        upTable.addAction(Actions.fadeIn(GameConfig.UI_ANIM_SPEED_FAST));
        upTable.pack();
    }

    public void setDialogueTextAndImage(String text, String imageName) {
        upLabel.setStyle(skin.get("dialogue", Label.LabelStyle.class));
        upLabel.setText(bundle.format(text));

        upImage.setDrawable(new TextureRegionDrawable(assetManager.get("project.dt", AsyncResourceManager.class).getTextureRegion(imageName)));
        upTable.clearChildren();
        upTable.add(upImage).maxSize(100);
        upTable.add(upLabel).grow();
        upTable.pack();

        upTable.addAction(Actions.fadeIn(GameConfig.UI_ANIM_SPEED_FAST));

    }
    public void hideAll() {
        stage.getRoot().addAction(Actions.fadeOut(GameConfig.UI_ANIM_SPEED_FAST));
//        upLabel.getColor().a = 0;
//        stage.getRoot().setVisible(false);
    }
    public void hideLabels() {
        upTable.addAction(Actions.fadeOut(GameConfig.UI_ANIM_SPEED_FAST));
        downLabel.addAction(Actions.fadeOut(GameConfig.UI_ANIM_SPEED_FAST));
//        upLabel.getColor().a = 0;
//        downLabel.getColor().a = 0;
//        upLabel.setVisible(false);
//        downLabel.setVisible(false);
    }

    public boolean isDownLabelHidden() {
        return MathUtils.isEqual(downLabel.getColor().a, 0);
    }

    public String getFromBundle(String key) {
        try {
            return bundle.format(key);
        } catch (MissingResourceException | NullPointerException ignored) {
            return "...";
        }
    }

    public Label addLabel(String text, float x, float y) {
        Label label = new Label(text, skin, "dialogue");
        label.setPosition(x, y);
        bigTable.add(label);
        bigTable.pack();
        return  label;
    }


    public void setDownText(String text) {

        downLabel.setText(text);
    }

    public void hideDownLabel() {
        downLabel.addAction(Actions.fadeOut(GameConfig.UI_ANIM_SPEED_FAST));
    }
}
