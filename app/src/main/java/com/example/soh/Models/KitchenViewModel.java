package com.example.soh.Models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.soh.API.APIInterface;

import java.util.ArrayList;
import java.util.List;

public class KitchenViewModel extends ViewModel {
    private MutableLiveData<List<Kitchen>> mListKitchenLiveData;
    private List<Kitchen> mListKitchen;

    // Thêm LiveData để theo dõi sự thay đổi trong danh sách món ăn
    private MutableLiveData<Boolean> mUpdateKitchenListLiveData = new MutableLiveData<>();

    public KitchenViewModel() {
        mListKitchenLiveData = new MutableLiveData<>();

        initData();
    }

    private void initData() {
        mListKitchen = new ArrayList<>();
        mListKitchenLiveData.setValue(mListKitchen);
    }

    public MutableLiveData<List<Kitchen>> getListKitchenLiveData() {
        return mListKitchenLiveData;
    }

    // Thêm phương thức để thông báo sự thay đổi trong danh sách món ăn
    public MutableLiveData<Boolean> getUpdateKitchenListLiveData() {
        return mUpdateKitchenListLiveData;
    }
}
