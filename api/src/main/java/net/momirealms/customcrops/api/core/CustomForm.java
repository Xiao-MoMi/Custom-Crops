package net.momirealms.customcrops.api.core;

public enum CustomForm {

    TRIPWIRE(ExistenceForm.BLOCK),
    NOTE_BLOCK(ExistenceForm.BLOCK),
    MUSHROOM(ExistenceForm.BLOCK),
    CHORUS(ExistenceForm.BLOCK),
    ITEM_FRAME(ExistenceForm.FURNITURE),
    ITEM_DISPLAY(ExistenceForm.FURNITURE),
    ARMOR_STAND(ExistenceForm.FURNITURE);

    private final ExistenceForm form;

    CustomForm(ExistenceForm form) {
        this.form = form;
    }

    public ExistenceForm existenceForm() {
        return form;
    }
}
