package com.amrizal.example.fragmenttester;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlertDialogFragment extends DialogFragment implements View.OnClickListener {


    private OnFragmentInteractionListener listener;

    public AlertDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alert_dialog, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        rootView.findViewById(R.id.first_fragment).setOnClickListener(this);
        rootView.findViewById(R.id.second_fragment).setOnClickListener(this);
        rootView.findViewById(R.id.third_fragment).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View view) {
        if(listener != null){
            listener.onAlertDialogFragmentShowFragment(view.getId());
        }
        dismissAllowingStateLoss();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAlertDialogFragmentShowFragment(int id);
    }
}
