package menuFragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import connectionHandler.external.acceptedStatus_;
import io.eyec.bombo.stokies.R;

import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class pending extends Fragment {
View myview;
androidx.recyclerview.widget.RecyclerView recyclerView;
TextView Info;

    public pending() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_pending, container, false);
        if (savedInstanceState == null) {
            recyclerView = myview.findViewById(R.id.recycler_pending);
            Info = myview.findViewById(R.id.info);
            connectionHandler.external.pendingStatus_.getAllDocuments(getActivity(), "pending", recyclerView, Info);
        }
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Info.getText().toString(), "information");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!= null) {
            Info.setText(savedInstanceState.getString("information").toString());
        }
    }

}
