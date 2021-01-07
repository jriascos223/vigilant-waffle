package tech.jriascos.data;

public class Ability {
    private Subability ability;
    private boolean is_hidden;
    private int slot;

    public Ability(Subability ability, boolean is_hidden, int slot) {
        this.ability = ability;
        this.is_hidden = is_hidden;
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean getHidden() {
        return this.is_hidden;
    }

    public Subability getSubability() {
        return this.ability;
    }

}
