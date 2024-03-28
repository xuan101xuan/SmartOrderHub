package com.example.soh.Models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TableViewModel extends ViewModel {
    private MutableLiveData<List<TableNumber>> mListTableNumberLiveData;
    private List<TableNumber> mListTableNumber;

    // Thêm LiveData để theo dõi sự thay đổi trong danh sách món ăn
    private MutableLiveData<Boolean> mUpdateTableNumberListLiveData = new MutableLiveData<>();

    public TableViewModel() {
        mListTableNumberLiveData = new MutableLiveData<>();

        initData();
    }

    private void initData() {
        mListTableNumber = new ArrayList<>();
        mListTableNumberLiveData.setValue(mListTableNumber);
    }

    public MutableLiveData<List<TableNumber>> getListTableNumberLiveData() {
        return mListTableNumberLiveData;
    }

    // Thêm phương thức để thông báo sự thay đổi trong danh sách món ăn
    public MutableLiveData<Boolean> getUpdateTableNumberListLiveData() {
        return mUpdateTableNumberListLiveData;
    }
}
