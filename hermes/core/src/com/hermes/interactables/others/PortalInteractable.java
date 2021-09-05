package com.hermes.interactables.others;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hermes.assets.ChildrenNames;
import com.hermes.common.EntityActions;
import com.hermes.interactables.BaseInteractable;
import com.hermes.util.Pair;
import games.rednblack.editor.renderer.SceneLoader;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.sprite.SpriteAnimationComponent;
import games.rednblack.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;


public class PortalInteractable extends BaseInteractable {

    private final SceneLoader sceneLoader;
    Array<Pair<Entity, Vector2>> entityDestinationPair = new Array<>();

    public PortalInteractable(Entity entity, ItemWrapper root, SceneLoader sceneLoader) {
        super(entity);
        this.sceneLoader = sceneLoader;
        MainItemComponent mc = ComponentRetriever.get(entity, MainItemComponent.class);
        String[] objects = mc.customVariables.getStringVariable("objects").split(", ");
        String[] destinations = mc.customVariables.getStringVariable("destinations").split(", ");
        for (int i = 0; i< objects.length; i++) {
            Entity entityToTeleport = root.getChild(objects[i]).getEntity();
            if (entityToTeleport != null) {
                Entity entityDestination = root.getChild(destinations[i]).getEntity();
                if (entityDestination != null) {
                    TransformComponent tc = ComponentRetriever.get(entityDestination, TransformComponent.class);
                    TransformComponent teleportTc = ComponentRetriever.get(entityToTeleport, TransformComponent.class);
                    DimensionsComponent dc = ComponentRetriever.get(entityToTeleport, DimensionsComponent.class);
                    entityDestinationPair.add(new Pair<>(entityToTeleport, new Vector2(tc.x + teleportTc.scaleX * dc.width / 2, tc.y + teleportTc.scaleY *dc.height / 2)));
                }
            }

        }
    }

    @Override
    public void interact() {
        for (Pair<Entity, Vector2> entityVector2Pair : entityDestinationPair) {
            EntityActions.teleportEntity(entityVector2Pair.getFirst(), entityVector2Pair.getSecond(), sceneLoader);
        }
        Entity ent = EntityActions.getChildOfEntity(entity, ChildrenNames.SPRITE_ANIMATION);
        if (ent != null) {
            SpriteAnimationComponent sa = ComponentRetriever.get(ent, SpriteAnimationComponent.class);
            SpriteAnimationStateComponent sas = ComponentRetriever.get(ent, SpriteAnimationStateComponent.class);
            if (sa != null) {
//                sa.playMode = sa.playMode == Animation.PlayMode.NORMAL ? Animation.PlayMode.REVERSED : Animation.PlayMode.NORMAL;
                sas.time = 0;
            }
        }
    }
}
