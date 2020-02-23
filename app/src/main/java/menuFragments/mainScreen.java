package menuFragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import constants.constants;
import io.eyec.bombo.stokies.MainActivity;
import io.eyec.bombo.stokies.R;
import properties.accessKeys;

import static methods.globalMethods.clearList;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class mainScreen extends android.app.Fragment implements View.OnClickListener {
View myview;
static ImageView image1;
static TextView shop1;
static ImageView image2;
static TextView shop2;
static ImageView image3;
static TextView shop3;
static ImageView image4;
static TextView shop4;
static ImageView image5;
static TextView shop5;
static ImageView image6;
static TextView shop6;


    public mainScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview =  inflater.inflate(R.layout.fragment_main_screen, container, false);
        image1 = myview.findViewById(R.id.image1);
        shop1 = myview.findViewById(R.id.shop1);
        image2 = myview.findViewById(R.id.image2);
        shop2 = myview.findViewById(R.id.shop2);
        image3 = myview.findViewById(R.id.image3);
        shop3 = myview.findViewById(R.id.shop3);
        image4 = myview.findViewById(R.id.image4);
        shop4 = myview.findViewById(R.id.shop4);
        image5 = myview.findViewById(R.id.image5);
        shop5 = myview.findViewById(R.id.shop5);
        image6 = myview.findViewById(R.id.image6);
        shop6 = myview.findViewById(R.id.shop6);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        image5.setOnClickListener(this);
        image6.setOnClickListener(this);
        if (savedInstanceState == null) {
            getFirestoreCategories(getActivity());
        }
        setExitApplication(true);
        MainActivity.setFragmentTag(null);
        return myview;
    }

    public static void getFirestoreCategories(final Activity activity){
        //gets all documents from firestore

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.shops)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Glide.with(activity).load(document.get("shop_1_image").toString()).into(image1);
                                shop1.setText(document.get("shop_1").toString());
                                Glide.with(activity).load(document.get("shop_2_image").toString()).into(image2);
                                shop2.setText(document.get("shop_2").toString());
                                Glide.with(activity).load(document.get("shop_3_image").toString()).into(image3);
                                shop3.setText(document.get("shop_3").toString());
                                Glide.with(activity).load(document.get("shop_4_image").toString()).into(image4);
                                shop4.setText(document.get("shop_4").toString());
                                Glide.with(activity).load(document.get("shop_5_image").toString()).into(image5);
                                shop5.setText(document.get("shop_5").toString());
                                Glide.with(activity).load(document.get("shop_6_image").toString()).into(image6);
                                shop6.setText(document.get("shop_6").toString());
                                /*if (document.get("userid")!=null){
                                    String user = document.get("userid").toString();
                                    if (user.equals(getDefaultUserId())) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        if(document.get("Points")!=null && document.get("Initiated")!=null) {
                                            total += Integer.parseInt(document.get("Points").toString());
                                            String Points = document.get("Points").toString();
                                            String initiated = document.get("Initiated").toString();
                                            points.add(Points);
                                            Initiated.add(initiated);
                                        }
                                    }
                                }*/
                            }


                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.image1 : String shop_1 = shop1.getText().toString();
                groceries.setShopName(shop_1);
                methods.globalMethods.loadFragmentWithTag(R.id.main, new groceries(), getActivity(),"groceries");
                break;
            case R.id.image2 : String shop_2 = shop2.getText().toString();
                groceries.setShopName(shop_2);
                methods.globalMethods.loadFragmentWithTag(R.id.main, new groceries(), getActivity(),"groceries");
                break;
            case R.id.image3 : String shop_3 = shop3.getText().toString();
                groceries.setShopName(shop_3);
                methods.globalMethods.loadFragmentWithTag(R.id.main, new groceries(), getActivity(),"groceries");
                break;
            case R.id.image4 : String shop_4 = shop4.getText().toString();
                groceries.setShopName(shop_4);
                methods.globalMethods.loadFragmentWithTag(R.id.main, new groceries(), getActivity(),"groceries");
                break;
            case R.id.image5 : String shop_5 = shop5.getText().toString();
                groceries.setShopName(shop_5);
                methods.globalMethods.loadFragmentWithTag(R.id.main, new groceries(), getActivity(),"groceries");
                break;
            case R.id.image6 : String shop_6 = shop6.getText().toString();
                groceries.setShopName(shop_6);
                methods.globalMethods.loadFragmentWithTag(R.id.main, new groceries(), getActivity(),"groceries");
                break;
        }
    }

}
