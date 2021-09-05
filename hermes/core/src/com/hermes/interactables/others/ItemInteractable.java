package com.hermes.interactables.others;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Logger;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.Inventory;
import com.hermes.common.Item;
import com.hermes.component.ActionOnRemoveComponent;
import com.hermes.component.HealthComponent;
import com.hermes.component.PlayerComponent;
import com.hermes.interactables.BaseInteractable;
import games.rednblack.editor.renderer.SceneLoader;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.components.TextureRegionComponent;
import games.rednblack.editor.renderer.resources.IResourceRetriever;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class ItemInteractable extends BaseInteractable {

    private static final Logger log = new Logger(ItemInteractable.class.getName(), Logger.DEBUG);

    private final SceneLoader sceneLoader;
    private final Inventory playerInventory;
    private final Entity player;

    public ItemInteractable(Entity entity, SceneLoader sceneLoader) {
        super(entity);
        this.sceneLoader = sceneLoader;
        setInfoKey(BundleKeys.PICK_UP);

        ItemWrapper iw = new ItemWrapper(sceneLoader.getRoot());
        Entity player = iw.getChild(ChildrenNames.PLAYER_ID).getEntity();

        PlayerComponent pc = ComponentRetriever.get(player, PlayerComponent.class);

        this.player = player;
        playerInventory = pc.inventory;
    }

    @Override
    public void interact() {

        MainItemComponent mic = ComponentRetriever.get(entity, MainItemComponent.class);

        String name = mic.customVariables.getStringVariable("name");
        String type = mic.customVariables.getStringVariable("type");
        String imageName = mic.customVariables.getStringVariable("imageName");

        if (type != null) {
            if (type.equals("health")) {
                GUIScene.INSTANCE.addHealth();
                ComponentRetriever.get(player, HealthComponent.class).heal(1);
            } else if (name != null) {
                if (imageName == null) {
                    Entity imageOfItem = new ItemWrapper(entity).getChild(ChildrenNames.IMAGE_CHILD).getEntity();
                    TextureRegionComponent trc = ComponentRetriever.get(imageOfItem, TextureRegionComponent.class);
                    if (trc != null) {
                        imageName = trc.regionName;
                    }
                }
                IResourceRetriever rm = sceneLoader.getRm();
                GUIScene.INSTANCE.addItem(name, rm.getTextureRegion(imageName));
                playerInventory.addItem(new Item(Item.ItemType.valueOf(type.toUpperCase()), name));
            }
        }

        ActionOnRemoveComponent action = ComponentRetriever.get(entity, ActionOnRemoveComponent.class);
        if (action != null) {
            action.onRemove.doAction();
        }

        sceneLoader.getEngine().removeEntity(entity);


    }
}
