package authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;

import java.util.List;

import accessors.userDetails;
import constants.constants;
import io.realm.Realm;
import io.realm.RealmResults;
import properties.accessKeys;

import static authentication.initiaterealm.getConfiguration;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.Device;
import static methods.globalMethods.InitializeFirstLetter;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static properties.accessKeys.getDefaultUserId;
import static properties.accessKeys.setAuthenticated;
import static properties.accessKeys.setDefaultUserId;
import static properties.accessKeys.setNetworkUnAvailable;

public class stayLoggedIn extends MultiDexApplication {
    public static FirebaseUser firebaseUser;
    static Realm realm;
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        context = this.getApplicationContext();
        //firebaseUser = null;
        //firebaseAuth.signOut();
        if (Availability()) {
            if(firebaseUser != null) {
                isRegistered(context);
            }
        }else{
            setNetworkUnAvailable(true);
            accessKeys.setLoggedin(false);
        }
    }

    //get clients ID
    public static void isRegistered(final Context context){
        Realm.init(context);
        Realm.setDefaultConfiguration(getConfiguration());
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<userDetails> results = bgRealm.where(userDetails.class)
                        .contains("userid", firebaseUser.getUid())
                        //.findAllAsync();
                        .findAll();
                results.load();
                if (results.size()>0){
                    accessKeys.setLoggedin(true);
                    setAuthenticated(true);
                    accessKeys.setName(results.get(0).getUserName());
                    accessKeys.setSurname(results.get(0).getUserSurname());
                    accessKeys.setLocation(results.get(0).getUserLocation());
                    accessKeys.setStokvel(results.get(0).getUserStokvel());
                    accessKeys.setPhone(results.get(0).getUserPhone());
                    accessKeys.setAltPhone(results.get(0).getUserAltPhone());
                    accessKeys.setDefaultUserId(results.get(0).getUserid());
                }else{
                    accessKeys.setLoggedin(false);
                    //error dialog, show active device and alert if a switch is made, last device won't be able to log in
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Loginfo("customer phone number successfully set");
                accessKeys.setLoggedin(true);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logerror("unable to get client's phone number, " + error.getMessage());
                accessKeys.setLoggedin(false);

            }
        });
        realm.close();
    }

    //checks network availability
    public boolean Availability() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
