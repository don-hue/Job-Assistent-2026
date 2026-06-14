package org.ProjectX.factories.Checkbox;

public class AppliedCheckboxFactory extends CheckboxFactory {
    @Override
    public CheckboxInterface create() {
        return new Applied();
    }
}
