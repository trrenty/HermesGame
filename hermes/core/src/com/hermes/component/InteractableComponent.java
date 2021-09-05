package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.hermes.interactables.Interactable;

public class InteractableComponent implements Component, Pool.Poolable {

    private Interactable interactable;

    public void setInteractable(Interactable interactable) {
        this.interactable = interactable;
    }

    public void interact() {
        interactable.interact();
    }
    public void beginContact() {
        interactable.beginContact();
    }
    public void endCotact() {
        if (interactable != null) {
            interactable.endContact();
        }
    }
    @Override
    public void reset() {
        interactable = null;
    }
}
