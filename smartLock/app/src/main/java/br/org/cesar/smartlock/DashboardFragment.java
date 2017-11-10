package br.org.cesar.smartlock;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import at.abraxas.amarino.Amarino;
import at.markushi.ui.CircleButton;
import br.org.cesar.smartlock.interfaces.IAmarinoCommand;
import br.org.cesar.smartlock.utils.AmarinoUtil;

public class DashboardFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private CircleButton mBtnLockUnlock;
    private boolean isLocked;

    public DashboardFragment() {

    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AmarinoUtil.getDataToArduino(new IAmarinoCommand(){
            @Override
            public void callback(String dataReturned) {
                isLocked = dataReturned.equals("T") ? true : false;
                changeResourceButtonAndGetCommand();
            }
        }, this.getActivity(), AmarinoUtil.GetStatusDoorLockFlag);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mBtnLockUnlock = (CircleButton) view.findViewById(R.id.btn_lock_unlock);
        mBtnLockUnlock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendCommandDoorLock();
            }

            private void sendCommandDoorLock() {
                String command = changeResourceButtonAndGetCommand();

                String[] data = {command};

                Amarino.sendDataToArduino(DashboardFragment.this.getActivity(), AmarinoUtil.Address, AmarinoUtil.DoorLockFlag, data);
                AmarinoUtil.sendDataToArduinoWithReturn(data, new IAmarinoCommand() {
                    @Override
                    public void callback(String dataReturned) {
                        if(!dataReturned.equals(AmarinoUtil.Success)){
                            Toast.makeText(DashboardFragment.this.getActivity(),
                                    "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, DashboardFragment.this.getActivity(), AmarinoUtil.DoorLockFlag);

                isLocked = !isLocked;
            }
        });

        return view;
    }

    private String changeResourceButtonAndGetCommand() {
        String command;
        if(isLocked){
            mBtnLockUnlock.setImageResource(R.drawable.unlock);
            mBtnLockUnlock.setColor(getResources().getColor(R.color.green));
            command = AmarinoUtil.CommandLock;
        }
        else{
            mBtnLockUnlock.setImageResource(R.drawable.lock);
            mBtnLockUnlock.setColor(getResources().getColor(R.color.red));
            command = AmarinoUtil.CommandUnlock;
        }
        return command;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
