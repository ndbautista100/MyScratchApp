package com.example.scratchappfeature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//Using this class to pick up whenever the font is being changed
public class ItemViewModel extends ViewModel {

    private final MutableLiveData<String> selectedItem = new MutableLiveData<String>();

    public void setData(String item){
        selectedItem.setValue(item);
    }

    public LiveData<String> getSelectedItem(){
        return selectedItem;
    }
}
