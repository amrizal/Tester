package com.amrizal.example.fragmenttester;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    private TextView sum;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_main, container, false);

        final List<Integer> ids = new ArrayList<>();
        ids.add(R.id.value_1);
        ids.add(R.id.value_2);

        sum = (TextView) view.findViewById(R.id.sum);
        for(Integer id:ids){
            EditText editText = (EditText) view.findViewById(id);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    int total = 0;
                    for(Integer id:ids){
                        EditText editText2 = (EditText) view.findViewById(id);
                        String value = String.valueOf(editText2.getText());
                        if(!value.isEmpty()){
                            total += Integer.valueOf(String.valueOf(editText2.getText()));
                        }
                    }
                    sum.setText(String.valueOf(total));
                }
            });
        }

        return view;
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }
}
