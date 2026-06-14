package org.ProjectX.factories.Checkbox;

public class BanCheckboxFactory extends CheckboxFactory {
    @Override
    public CheckboxInterface create(){
        return new Ban();
    };
}
