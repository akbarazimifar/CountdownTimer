package com.example.timerApplication.popupactivity;

import android.content.Context;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.timerApplication.R;
import com.example.timerApplication.RecyclerAdapter;
import com.example.timerApplication.common.ConstantsClass;
import com.example.timerApplication.model.DataHolder;
import com.example.timerApplication.timers.Timer;
import com.example.timerApplication.timers.TimerGroup;
import com.example.timerApplication.timers.TimerGroupType;

import java.util.Locale;

public class TimePickerPopup extends PopupWindow implements View.OnClickListener, NumberPicker.OnValueChangeListener, PopupWindow.OnDismissListener, NumberPicker.OnScrollListener {

    Button setTimerButton;
    Button cancelSetTimerButton;
    Button toggleNumberPickerTimerGroup;
    NumberPicker numberPickerHours;
    NumberPicker numberPickerMinutes;
    NumberPicker numberPickerSeconds;
    LinearLayout numberPickerLayout;
    LinearLayout timerGroupPickerLayout;
    LinearLayout timerPickerButtons;
    LinearLayout hr;
    View view;
    View parent;
    View addNewTimerGroupView;
    Vibrator vibrator;
    Integer position;
    RecyclerAdapter recyclerAdapter;


    public TimePickerPopup(View view, RecyclerAdapter recyclerAdapter) {
        this(view, recyclerAdapter, null);
    }

    public TimePickerPopup(View view, RecyclerAdapter recyclerAdapter, Integer position) {
        super(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.position = position;
        setOutsideTouchable(true);
        setTouchable(true);
        setFocusable(true);
        this.view = view;
        this.recyclerAdapter = recyclerAdapter;
        init();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        this.parent = parent;
        super.showAtLocation(parent, gravity, x, y);
        View container = (View) getContentView().getParent();
        WindowManager wm = (WindowManager) getContentView().getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.5f;
        wm.updateViewLayout(container, p);
        DataHolder.getInstance().setDisableButtonClick(false);
    }

    private void init() {
        //Initialize number picker
        numberPickerHours = view.findViewById(R.id.number_picker_hours);
        numberPickerMinutes = view.findViewById(R.id.number_picker_minutes);
        numberPickerSeconds = view.findViewById(R.id.number_picker_seconds);
        numberPickerHours.setOnValueChangedListener(this);
        numberPickerMinutes.setOnValueChangedListener(this);
        numberPickerSeconds.setOnValueChangedListener(this);

        //Set number picker range
        numberPickerInit(numberPickerHours, ConstantsClass.NUMBER_PICKER_HOURS_START, ConstantsClass.NUMBER_PICKER_HOURS_END);
        numberPickerInit(numberPickerMinutes, ConstantsClass.NUMBER_PICKER_MINUTES_START, ConstantsClass.NUMBER_PICKER_MINUTES_END);
        numberPickerInit(numberPickerSeconds, ConstantsClass.NUMBER_PICKER_SECONDS_START, ConstantsClass.NUMBER_PICKER_SECONDS_END);

        hr = view.findViewById(R.id.popup_hr);
        timerPickerButtons = view.findViewById(R.id.timer_picker_buttons);
        addNewTimerGroupView = view.findViewById(R.id.add_new_timer_group);
        numberPickerLayout = view.findViewById(R.id.timer_picker_numbers);
        timerGroupPickerLayout = view.findViewById(R.id.new_timer_group);
        timerGroupPickerLayout.setVisibility(View.GONE);
        addNewTimerGroupView.setVisibility(View.GONE);
        numberPickerLayout.setVisibility(View.VISIBLE);

        //Route number picker buttons
        setTimerButton = view.findViewById(R.id.set_timer_button);
        cancelSetTimerButton = view.findViewById(R.id.cancel_set_timer_button);
        addNewTimerGroupView.setOnClickListener(this);
        toggleNumberPickerTimerGroup = view.findViewById(R.id.toggle_numberpicker_timergroup);
        setTimerButton.setOnClickListener(this);
        cancelSetTimerButton.setOnClickListener(this);
        toggleNumberPickerTimerGroup.setOnClickListener(this);
        vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        setOnDismissListener(this);
        numberPickerHours.setOnValueChangedListener(this);
        numberPickerMinutes.setOnValueChangedListener(this);
        numberPickerSeconds.setOnValueChangedListener(this);
        numberPickerHours.setOnScrollListener(this);
        numberPickerMinutes.setOnScrollListener(this);
        numberPickerSeconds.setOnScrollListener(this);

        if (position != null) {
            if (DataHolder.getInstance().getListTimerGroup().get(position).getTimerGroupType().equals(TimerGroupType.TIMER_GROUP))
                toggleView();
            else if (DataHolder.getInstance().getListTimerGroup().get(position).getTimerGroupType().equals(TimerGroupType.TIMER)) {
                numberPickerSeconds.setValue(DataHolder.getInstance().getListTimerGroup().get(position).getTimer().getSeconds());
                numberPickerMinutes.setValue(DataHolder.getInstance().getListTimerGroup().get(position).getTimer().getMinutes());
                numberPickerHours.setValue(DataHolder.getInstance().getListTimerGroup().get(position).getTimer().getHours());
            }
        }

        DataHolder.getInstance().setDisableButtonClick(false);
    }

    public void toggleView() {
        if (numberPickerLayout.getVisibility() == View.VISIBLE) {
            hr.setVisibility(View.GONE);
            timerPickerButtons.setVisibility(View.GONE);
            numberPickerLayout.setVisibility(View.GONE);
            addNewTimerGroupView.setVisibility(View.VISIBLE);
            timerGroupPickerLayout.setVisibility(View.VISIBLE);
            toggleNumberPickerTimerGroup.setText(R.string.toggle_number_picker);
            for (TimerGroup tg : DataHolder.getInstance().getAllTimerGroups()) {
                if (!tg.getName().equals(DataHolder.getInstance().getStackNavigation().peek())) {
                    View timergroupPickerView = LayoutInflater.from(view.getContext()).inflate(R.layout.timergroup_picker_item, (ViewGroup)view, false);
                    TextView textView = timergroupPickerView.findViewById(R.id.timer_group_picker_name);
                    textView.setText(tg.getName());
                    timergroupPickerView.setVisibility(View.VISIBLE);
                    timergroupPickerView.setOnClickListener(v -> {
                        tg.incrementInternalUsageCount();
                        setTimerInRecyclerView(tg);
                    });
                    timerGroupPickerLayout.addView(timergroupPickerView);
                }
            }
        } else {
            hr.setVisibility(View.VISIBLE);
            timerPickerButtons.setVisibility(View.VISIBLE);
            numberPickerLayout.setVisibility(View.VISIBLE);
            timerGroupPickerLayout.setVisibility(View.GONE);
            addNewTimerGroupView.setVisibility(View.GONE);
            toggleNumberPickerTimerGroup.setText(R.string.toggle_timer_group_picker);
            timerGroupPickerLayout.removeAllViewsInLayout();
        }
        DataHolder.getInstance().setDisableButtonClick(false);
    }

    /**
     * Set number picker range
     *
     * @param numberPicker number picker to initialize
     * @param min minimum value for picker
     * @param max maximum value for picker
     */
    private void numberPickerInit(NumberPicker numberPicker, int min, int max) {
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
        numberPicker.setFormatter(value -> String.format(Locale.US, "%02d", value));
    }

    /**
     * Cancel the setting of timer after add button is pressed
     * Delete from the recycler view
     */
    private void cancelSetTimer() {
        dismiss();
    }

    /**
     * Set value of a new timer or that of edited timer
     */
    private void setTimer() {
        TimerGroup timerGroup = new TimerGroup(new Timer(numberPickerHours.getValue(), numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
        setTimerInRecyclerView(timerGroup);
    }

    private void setTimerInRecyclerView(TimerGroup timerGroup) {
        if (position == null) {
            if (DataHolder.getInstance().getMapTimerGroups().containsKey(DataHolder.getInstance().getStackNavigation().peek()) && DataHolder.getInstance().getMapTimerGroups().get(DataHolder.getInstance().getStackNavigation().peek()) != null) {
                DataHolder.getInstance().getAllTimerGroups().get(DataHolder.getInstance().getMapTimerGroups().get(DataHolder.getInstance().getStackNavigation().peek())).getListTimerGroup().add(timerGroup);
            }
        } else {
            if (DataHolder.getInstance().getMapTimerGroups().containsKey(DataHolder.getInstance().getStackNavigation().peek()) && DataHolder.getInstance().getMapTimerGroups().get(DataHolder.getInstance().getStackNavigation().peek()) != null) {
                DataHolder.getInstance().getAllTimerGroups().get(DataHolder.getInstance().getMapTimerGroups().get(DataHolder.getInstance().getStackNavigation().peek())).getListTimerGroup().set(position, timerGroup);
            }
        }
        DataHolder.getInstance().saveData(getContentView().getContext());
        recyclerAdapter.notifyDataSetChanged();
        dismiss();
    }

    private Boolean allNumberPickersZero() {
        return numberPickerHours.getValue() == 0 && numberPickerMinutes.getValue() == 0 && numberPickerSeconds.getValue() == 0;
    }

    @Override
    public void onClick(View view) {
        if (!DataHolder.getInstance().getDisableButtonClick()) {
            DataHolder.getInstance().setDisableButtonClick(true);
            if (view.getId() == cancelSetTimerButton.getId()) cancelSetTimer();
            else if (view.getId() == setTimerButton.getId() && !allNumberPickersZero()) setTimer();
            else if (view.getId() == toggleNumberPickerTimerGroup.getId()) toggleView();
            else if (view.getId() == addNewTimerGroupView.getId()) {
                View timerNamePopupWindowView = LayoutInflater.from(getContentView().getContext()).inflate(R.layout.timer_name_popup, (ViewGroup)this.view, false);
                PopupWindow timerNamePopupWindow = new TimerNamePopup(timerNamePopupWindowView, recyclerAdapter);
                timerNamePopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
//                DataHolder.getInstance().getListTimerGroup().add(new TimerGroup(TimerGroupType.TIMER_GROUP));
                recyclerAdapter.notifyItemInserted(DataHolder.getInstance().getListTimerGroup().size());
                timerNamePopupWindow.setOnDismissListener(() -> {
                    toggleView();
                    toggleView();
                });
                DataHolder.getInstance().setDisableButtonClick(false);
            }
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        setTimerButton.setEnabled(!allNumberPickersZero());
    }

    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {
        vibrator.vibrate(ConstantsClass.VIBRATE_VERY_SHORT);
    }

    @Override
    public void onDismiss() {
        super.dismiss();
        DataHolder.getInstance().setDisableButtonClick(false);
    }
}
