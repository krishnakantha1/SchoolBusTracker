package com.krishna.schoolbustracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.krishna.schoolbustracker.R;
import com.krishna.schoolbustracker.models.StudentModels;

import java.util.List;

public class StudentAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<StudentModels> students;

    public StudentAdapter(Activity activity, List<StudentModels> students) {
        this.activity = activity;
        this.students = students;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int location) {
        return students.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.student_model, null);
        TextView textView =(TextView)convertView.findViewById(R.id.stud_name);
        final CheckBox checkBox=(CheckBox)convertView.findViewById(R.id.check);
        final StudentModels studentModels=students.get(position);
        textView.setText(studentModels.getName());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox.isChecked()){
                    studentModels.setCheck(true);
                }else{
                    studentModels.setCheck(false);
                }
            }
        });
        return convertView;
    }
}
