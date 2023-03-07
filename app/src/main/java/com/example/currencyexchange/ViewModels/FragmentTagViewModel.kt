package com.example.currencyexchange.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentTagViewModel : ViewModel() {
    var mIsMoved = MutableLiveData<Boolean>()

    fun setMoveFlag(movedFlag: Boolean) {
        mIsMoved.value = movedFlag
    }
}