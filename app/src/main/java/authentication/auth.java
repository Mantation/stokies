package authentication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import accessors.userDetails;
import constants.constants;
import io.eyec.bombo.stokies.R;
import io.realm.Realm;
import menuFragments.mainScreen;
import methods.globalMethods;
import pl.droidsonroids.gif.GifImageView;
import properties.accessKeys;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static authentication.initiaterealm.getConfiguration;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.ShowDialog;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static properties.accessKeys.setDefaultUserEmail;
import static properties.accessKeys.setDefaultUserId;

public class auth {
    static Realm realm;
    static String phoneVerificationId;
    static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    static PhoneAuthProvider.ForceResendingToken resendingToken;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser firebaseUser;

    public static void PhoneRegistration(final Activity activity, final String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity, mCallbacks);
    }

    public static void callback_verification(final Activity activity) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // this method executed if phone number verified you can do your stuff here if you only want to verify phone number.
                // or you can also use this credentials for authenticate purpose.
                //SignInWithPhoneAuthCredential(activity,credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.d(constants.PhoneRegTag, "Invalid Credentials");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.d(constants.PhoneRegTag, "SMS Verification Pin needed");
                }
            }


            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationId = verificationId;
                resendingToken = token;
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                //and then execute your method if number entered is correct.
            }
        };
    }

    public static void verifyCode(final Activity activity, final TextView textView){
        String code = textView.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId,code);
        //SignInWithPhoneAuthCredential(activity,credential);

    }

    public static void SignInWithPhoneAuthCredential(final Activity activity, PhoneAuthCredential credential, final Dialog dialog, final EditText input, final Button send, final Button resend, final pl.droidsonroids.gif.GifImageView counter){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isComplete()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = null;
                            if (user != null) {
                                uid = user.getUid();
                                counter.setVisibility(View.GONE);
                                dialog.dismiss();
                                ShowDialog(activity);
                                Toast.makeText(activity, "Signed In", Toast.LENGTH_SHORT).show();
                                Toast.makeText(activity, uid, Toast.LENGTH_SHORT).show();
                                //userInformation(activity,uid);updated from this
                                getFireBaseUserToken(activity,uid);// to this
                            }else{
                                counter.setVisibility(View.GONE);
                                input.setVisibility(View.VISIBLE);
                                send.setVisibility(View.VISIBLE);
                                resend.setVisibility(View.VISIBLE);
                            }

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(activity, "Invalid Code", Toast.LENGTH_SHORT).show();
                                globalMethods.stopProgress = true;
                            }
                        }
                        //FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        //firebaseUser = firebaseAuth.getCurrentUser();
                        //FirebaseUser user = task.getResult().getUser();
                        //Toast.makeText(activity, "Registered successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public  static void resendCode(final Activity activity, final TextView textView){
        String phoneNumber = textView.getText().toString();
        callback_verification(activity);
        PhoneRegistration(activity,phoneNumber);
    }

    public static void signOut(View view){
        firebaseAuth.signOut();
    }

    public static void InitiateAuth(final Activity activity, final String phoneNumber){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.tokenlayout);
        dialog.setCancelable(true);
        final EditText input = (EditText) dialog.findViewById(R.id.token);
        final Button send = (Button) dialog.findViewById(R.id.send);
        final Button resend = (Button) dialog.findViewById(R.id.resend);
        final pl.droidsonroids.gif.GifImageView counter = (pl.droidsonroids.gif.GifImageView) dialog.findViewById(R.id.counter);
        input.setVisibility(View.GONE);
        send.setVisibility(View.GONE);
        resend.setVisibility(View.GONE);
        final String phone = "+27"+phoneNumber.substring(1,phoneNumber.length());
        RegisterPhone(activity,phone,dialog,input,send,resend,counter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getText().toString().isEmpty()&& input.getText().toString().length()>5){
                    //ShowDialog(activity);
                    final String token = input.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId,token);
                    SignInWithPhoneAuthCredential(activity,credential,dialog,input,send,resend,counter);
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,60,TimeUnit.SECONDS,activity,mCallbacks,resendingToken);
                Toast.makeText(activity, "verification code is resent", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
    }


    public static void RegisterPhone(final Activity activity,final String phoneNumber, final Dialog dialog,final EditText input,final Button send,final Button resend,final pl.droidsonroids.gif.GifImageView counter){
        // The test phone number and code should be whitelisted in the console.
        //String phoneNumber = phone;//"+27820964587";
        String smsCode = phoneVerificationId;
        accessKeys.setPhone(phoneNumber);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();

// Configure faking the auto-retrieval with the whitelisted numbers.
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode);

        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
        phoneAuthProvider.verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                activity, /* activity */
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // Instant verification is applied and a credential is directly returned.
                        // ...
                        SignInWithPhoneAuthCredential(activity,credential,dialog,input,send,resend,counter);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e(TAG, "Error adding document", e);
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.d(constants.PhoneRegTag, "Invalid Credentials");
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Log.d(constants.PhoneRegTag, "SMS Verification Pin needed");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        phoneVerificationId = verificationId;
                        resendingToken = token;
                        counter.setVisibility(View.GONE);
                        input.setVisibility(View.VISIBLE);
                        input.requestFocus();
                        send.setVisibility(View.VISIBLE);
                        resend.setVisibility(View.VISIBLE);
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        //and then execute your method if number entered is correct.
                    }


                });

    }
    private static void getFireBaseUserToken(final Activity activity, final String UID){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(GifHeaderParser.TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        String msg = activity.getString(R.string.msg_token_fmt, token);
                        Log.d(GifHeaderParser.TAG, msg);
                        accessKeys.setMessagingToken(token);
                        userInformation(activity,UID);
                        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public static void userInformation(final Activity activity, final String UUID){
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("document ref", defaultvalue);
            user.put("userid", UUID);
            user.put("name", accessKeys.getName());
            user.put("surname", accessKeys.getSurname());
            user.put("location", accessKeys.getLocation());
            user.put("phone", accessKeys.getPhone());
            user.put("stokvel", accessKeys.getStokvel());
            user.put("altphone", accessKeys.getAltPhone());
            user.put("token", accessKeys.getMessagingToken());
            user.put("date", ToDate());
            user.put("time", Time());

            // Add a new document with a generated ID
            db.collection(constants.users)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            final  String document = documentReference.getId();
                            CollectionReference collectionReference = db.collection(constants.users);
                            collectionReference.document(documentReference.getId()).update("document ref", document).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //set Realm user information
                                        InitiateUser(activity,UUID);
                                        //Toast.makeText(this, "Document created/updated", Toast.LENGTH_SHORT).show();
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

    public static void InitiateUser(final Activity activity, final String userId){
        Realm.init(activity);
        Realm.setDefaultConfiguration(getConfiguration());
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                userDetails user = bgRealm.createObject(userDetails.class);
                user.setUserName(accessKeys.getName());
                user.setUserSurname(accessKeys.getSurname());
                user.setUserid(userId);
                user.setUserLocation(accessKeys.getLocation());
                user.setUserStokvel(accessKeys.getStokvel());
                user.setUserPhone(accessKeys.getPhone());
                user.setUserAltPhone(accessKeys.getAltPhone());
                setDefaultUserId(userId);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Loginfo("user Personal Details successfully added");
                globalMethods.stopProgress = true;
                methods.globalMethods.loadFragmentWithTag(R.id.main, new mainScreen(), activity,"mainScreen");
                //methods.globalMethods.loadFragments(R.id.home, new profile(), activity);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logerror("unable to add user Personal Details, " + error.getMessage());
                globalMethods.stopProgress = true;

            }
        });
        realm.close();
    }


}
