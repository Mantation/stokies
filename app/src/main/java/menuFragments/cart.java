package menuFragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import accessors.cartDetails;
import adapters.CartAdapter;
import constants.constants;
import io.eyec.bombo.stokies.MainActivity;
import io.eyec.bombo.stokies.R;
import io.realm.Realm;
import io.realm.RealmResults;
import methods.globalMethods;
import properties.accessKeys;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static authentication.initiaterealm.getConfiguration;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.ShowDialog;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static methods.globalMethods.clearList;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class cart extends android.app.Fragment implements View.OnClickListener{
    static Realm realm;
    View myview;
    static TextView amount;
    androidx.recyclerview.widget.RecyclerView recyclerView;
    static CardView CheckOut;
    public static List<String> image = new ArrayList<String>();
    public static List<String> item = new ArrayList<String>();

    public static TextView getAmount() {
        return amount;
    }

    public static void setAmount(TextView amount) {
        cart.amount = amount;
    }

    public cart() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_cart, container, false);
        if(savedInstanceState == null) {
            CheckOut = myview.findViewById(R.id.CheckOut);
            recyclerView = myview.findViewById(R.id.recycler_checkout);
            amount = myview.findViewById(R.id.amount);
            connectionHandler.internal.cart_.getCartItems(getActivity(), realm, recyclerView);
            CheckOut.setOnClickListener(this);
        }
        setExitApplication(true);
        MainActivity.setFragmentTag("cart");
        return myview;
    }

    public static void checkoutFromRealm(final Activity activity, final String Total){
        clearList(item);
        clearList(image);
        Realm.setDefaultConfiguration(getConfiguration());
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<cartDetails> results = bgRealm.where(cartDetails.class)
                        .contains("userid", accessKeys.getDefaultUserId())
                        //.findAllAsync();
                        .findAll();
                results.load();
                for (int i = 0; i < results.size(); i++) {
                    item.add(results.get(i).getItemSummary());
                    image.add(results.get(i).getImage());
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Loginfo("cart information successfully obtained");
                submitCheckOut(activity,Total);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logerror("unable to get customer cart information, " + error.getMessage());

            }
        });
        realm.close();
    }

    public static void submitCheckOut(final Activity activity, final String Total){
        ShowDialog(activity);
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("document ref", defaultvalue);
            String Item = "item";
            String Image = "image";
            for (int i = 0; i < item.size(); i++) {
                user.put(Item+i, item.get(i));
                user.put(Image+i, image.get(i));
            }
            user.put("total", Total);
            user.put("userid", accessKeys.getDefaultUserId());
            user.put("name", accessKeys.getName());
            user.put("surname", accessKeys.getSurname());
            user.put("location", accessKeys.getLocation());
            user.put("phone", accessKeys.getPhone());
            user.put("stokvel", accessKeys.getStokvel());
            user.put("altphone", accessKeys.getAltPhone());
            user.put("status", "pending");
            user.put("date", ToDate());
            user.put("time", Time());

            // Add a new document with a generated ID
            db.collection(constants.checkout)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            final  String document = documentReference.getId();
                            CollectionReference collectionReference = db.collection(constants.checkout);
                            collectionReference.document(documentReference.getId()).update("document ref", document).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //set Realm user information
                                        //Toast.makeText(this, "Document created/updated", Toast.LENGTH_SHORT).show();
                                        emptyCheckOutFromRealm(activity);
                                    }
                                }

                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            globalMethods.stopProgress = true;
                            Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception exception){
            exception.getMessage();
            exception.printStackTrace();
        }

    }

    public static void emptyCheckOutFromRealm(final Activity activity){
        Realm.setDefaultConfiguration(getConfiguration());
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<cartDetails> results = bgRealm.where(cartDetails.class)
                        .contains("userid", accessKeys.getDefaultUserId())
                        //.findAllAsync();
                        .findAll();
                results.load();
                results.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Loginfo("cart information successfully obtained");
                globalMethods.stopProgress = true;
                methods.globalMethods.loadFragmentWithTag(R.id.main, new history(), activity,"history");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logerror("unable to get customer cart information, " + error.getMessage());
                globalMethods.stopProgress = true;

            }
        });
        realm.close();
    }

    @Override
    public void onClick(View v) {
        checkoutFromRealm(getActivity(),amount.getText().toString());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(amount.getText().toString(), "amount");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!= null) {
            amount.setText(savedInstanceState.getString("amount").toString());
        }
    }
}
