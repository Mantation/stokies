package connectionHandler.external;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapters.groceryListAdapter;
import io.eyec.bombo.stokies.R;
import menuFragments.groceries;
import menuFragments.mainScreen;

import static methods.globalMethods.clearList;

public class groceries_ extends Application {
    public static List<String> item = new ArrayList<String>();
    public static List<String> image = new ArrayList<String>();
    public static List<String> price = new ArrayList<String>();
    public static List<String> category = new ArrayList<String>();
    static groceryListAdapter grocerylistAdapter;


    public static void getAllDocuments(final Activity activity,final String Category, final androidx.recyclerview.widget.RecyclerView recyclerView) {
        clearList(item);
        clearList(image);
        clearList(price);
        clearList(category);
        //gets all documents from firestore
        getFirestoreGroceryDetails(activity,Category,recyclerView);
        //getCommentIssues(activity, context, view, recyclerView); - should be like this
    }

    //get patients
    public static void getGroceryDetails(final Activity activity,final String Category, final androidx.recyclerview.widget.RecyclerView recyclerView){
        clearList(item);
        clearList(image);
        clearList(price);
        clearList(category);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                int totalDocs = document.getData().size();
                                String Price = "price";
                                String Image = "image";
                                String Item = "item";
                                int count = 0;
                                for (int i = 0; i < totalDocs; i++) {
                                    if(document.get(Price+i)!=null && document.get(Image+i)!=null && document.get(Item+i)!=null){
                                        String Itm  = document.get(Item+i).toString();
                                        String Img  = document.get(Image+i).toString();
                                        String Prc  = document.get(Price+i).toString();
                                        item.add(Itm);
                                        price.add(Prc);
                                        image.add(Img);
                                        if(count == 0) {
                                            category.add(document.get("document").toString());
                                        }else{
                                            category.add("zerrrrO");
                                        }
                                        count++;
                                    }

                                }
                            }
                            final String []MyItem = new String [item.size()];
                            final String []MyPrice = new String [price.size()];
                            final String []MyImage = new String [image.size()];
                            final String []MyCategory = new String [category.size()];
                            for (int i = 0; i < item.size(); i++) {
                                MyItem[i] = item.get(i);
                                MyPrice[i] = price.get(i);
                                MyImage[i] = image.get(i);
                                MyCategory[i] = category.get(i);
                            }
                            grocerylistAdapter = new groceryListAdapter(activity);
                            grocerylistAdapter.setItem(MyItem);
                            grocerylistAdapter.setPrice(MyPrice);
                            grocerylistAdapter.setImage(MyImage);
                            grocerylistAdapter.setCategory(MyCategory);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(grocerylistAdapter);
                            //add items to autocomplete
                            if(item.size() == 0){
                                Toast.makeText(activity, "No grocery list currently set up for this store ", Toast.LENGTH_LONG).show();
                                methods.globalMethods.loadFragments(R.id.main, new mainScreen(), activity);
                            }

                        }
                    }
                });
    }

    //get all documents from firestore
    public static  void getFirestoreGroceryDetails(final Activity activity,final String Category, final androidx.recyclerview.widget.RecyclerView recyclerView){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Category).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("", "Error : " + e.getMessage());
                }
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Log.d("Brand Name: ", doc.getDocument().getId());
                        doc.getDocument().getReference().collection(doc.getDocument().getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.d("", "Error : " + e.getMessage());
                                }

                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        Log.d("SubBrands Name: ", doc.getDocument().getId());
                                    }
                                }

                            }
                        });
                    }

                }
            }});
        final FirebaseFirestore dbs = FirebaseFirestore.getInstance();
        dbs.collection(Category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Documents.add(document.getId());
                            }
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                //Documents.add(document.getId());
                            }
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            //Documents.addAll(myListOfDocuments);
                            //getCompanyInformation(activity);
                            getGroceryDetails(activity,Category,recyclerView);
                            //getIssues(activity,context, view, recyclerView); //ammended
                        }
                    }
                });
    }
}
