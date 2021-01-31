package com.UnayShah.countdownTimer.fragmentsInflater;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.UnayShah.countdownTimer.R;
import com.UnayShah.countdownTimer.model.DataHolder;
import com.google.android.material.button.MaterialButton;

public class loopSwitchFragmentInflater extends Fragment implements View.OnClickListener {

    MaterialButton loopButton;
    ColorStateList accentColorStateList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.loop_button, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        loopButton = view.findViewById(R.id.loop_button);
        loopButton.setOnClickListener(this);
        loopButton.setCheckable(true);
        loopButton.setIconTintResource(R.color.iconTint);
        loopButton.setChecked(true);
        accentColorStateList = DataHolder.getInstance().getAccentColor(getContext());
        setTint();
    }

    @Override
    public void onClick(View v) {
        DataHolder.getInstance().getAllTimerGroups().get(DataHolder.getInstance().getMapTimerGroups().get(DataHolder.getInstance().getStackNavigation().peek())).setLooped(!DataHolder.getInstance().getAllTimerGroups().get(DataHolder.getInstance().getMapTimerGroups().get(DataHolder.getInstance().getStackNavigation().peek())).getLooped());
        setTint();
        DataHolder.getInstance().setDisableButtonClick(false);
    }

    private void setTint() {
        if (!DataHolder.getInstance().getAllTimerGroups().get(DataHolder.getInstance().getMapTimerGroups().get(DataHolder.getInstance().getStackNavigation().peek())).getLooped()) {
            loopButton.setIconTintResource(R.color.iconTint);
            loopButton.setRippleColor(accentColorStateList);
        } else {
            loopButton.setIconTint(accentColorStateList);
            loopButton.setRippleColorResource(R.color.iconTint);
        }
    }
}