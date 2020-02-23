package connectionHandler.external;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

import adapters.StatusAdapter;
import constants.constants;
import properties.accessKeys;

import static methods.globalMethods.clearList;

public class pendingStatus_ extends Application {
    public static List<String> Document = new ArrayList<String>();
    public static List<String> date = new ArrayList<String>();
    public static List<String> time = new ArrayList<String>();
    static StatusAdapter statusAdapter;


    public static void getAllDocuments(final Activity activity, final String status, final androidx.recyclerview.widget.RecyclerView recyclerView, final TextView textView) {
        clearList(Document);
        clearList(date);
        clearList(time);
        //gets all documents from firestore
        getFirestoreApplications(activity,status,recyclerView,textView);
        //getCommentIssues(activity, context, view, recyclerView); - should be like this
    }

    //get patients
    public static void getApplicationsDetails(final Activity activity,final String status, final androidx.recyclerview.widget.RecyclerView recyclerView, final TextView textView){
        clearList(Document);
        clearList(date);
        clearList(time);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.checkout)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.get("document ref")!=null && document.get("userid")!=null && document.get("date")!=null && document.get("time")!=null) {
                                    if (status.equals(document.get("status").toString()) && accessKeys.getDefaultUserId().equals(document.get("userid").toString())) {
                                        Document.add(document.get("document ref").toString());
                                        date.add(document.get("date").toString());
                                        time.add(document.get("time").toString());
                                    }
                                }
                            }
                            final String []Mydate = new String [date.size()];
                            final String []MyDocument = new String [Document.size()];
                            final String []Mytime = new String [time.size()];
                            for (int i = 0; i < date.size(); i++) {
                                Mydate[i] = date.get(i);
                                MyDocument[i] = Document.get(i);
                                Mytime[i] = time.get(i);
                            }
                            //sort ascending
                            String tempDate = "";
                            String tempDoc = "";
                            String tempTime = "";
                            for (int i = 0; i < MyDocument.length; i++) {
                                for (int j = i; j < MyDocument.length - 1; j++) {
                                    int MyDate = Integer.parseInt(Mydate[j+1].replace("/",""));
                                    int MyTime = Integer.parseInt(Mytime[j+1].replace(":",""));
                                    int MyOldDate = Integer.parseInt(Mydate[i].replace("/",""));
                                    int MyOldTime = Integer.parseInt(Mytime[i].replace(":",""));
                                    if(MyOldDate<MyDate)
                                    {
                                        tempDoc = MyDocument[j+1];
                                        MyDocument[j+1] = MyDocument[i];
                                        MyDocument[i] = tempDoc;

                                        tempDate = Mydate[j+1];
                                        Mydate[j+1] = Mydate[i];
                                        Mydate[i] = tempDate;

                                        tempTime = Mytime[j+1];
                                        Mytime[j+1] = Mytime[i];
                                        Mytime[i] = tempTime;
                                    }else{
                                        if (MyOldDate==MyDate){
                                            if(MyOldTime<MyTime)
                                            {
                                                tempDoc = MyDocument[j+1];
                                                MyDocument[j+1] = MyDocument[i];
                                                MyDocument[i] = tempDoc;

                                                tempDate = Mydate[j+1];
                                                Mydate[j+1] = Mydate[i];
                                                Mydate[i] = tempDate;

                                                tempTime = Mytime[j+1];
                                                Mytime[j+1] = Mytime[i];
                                                Mytime[i] = tempTime;
                                            }
                                        }
                                    }
                                }
                            }
                            statusAdapter = new StatusAdapter(activity);
                            statusAdapter.setDocument(MyDocument);
                            statusAdapter.setDate(Mydate);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(statusAdapter);
                            //add items to autocomplete
                            if(Document.size() > 0){
                                textView.setVisibility(View.GONE);
                            }

                        }
                    }
                });
    }

    //get all documents from firestore
    public static  void getFirestoreApplications(final Activity activity,final String status, final androidx.recyclerview.widget.RecyclerView recyclerView, final TextView textView){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.checkout).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        dbs.collection(constants.checkout)
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
                            getApplicationsDetails(activity,status,recyclerView,textView);
                            //getIssues(activity,context, view, recyclerView); //ammended
                        }
                    }
                });
    }
}
