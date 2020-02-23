package menuFragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import accessors.cartDetails;
import accessors.userDetails;
import io.eyec.bombo.stokies.MainActivity;
import io.eyec.bombo.stokies.R;
import io.realm.Realm;
import io.realm.RealmResults;
import properties.accessKeys;

import static authentication.initiaterealm.getConfiguration;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static properties.accessKeys.setAuthenticated;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class groceries extends android.app.Fragment implements View.OnClickListener {
View myview;
static Realm realm;
static String shopName;
static TextView total;
androidx.recyclerview.widget.RecyclerView recyclerView;
static CardView CheckOut;
static CardView Orders;

    public static String getShopName() {
        return shopName;
    }

    public static void setShopName(String shopName) {
        groceries.shopName = shopName;
    }

    public static TextView getTotal() {
        return total;
    }

    public static void setTotal(TextView total) {
        groceries.total = total;
    }

    public static CardView getCheckOut() {
        return CheckOut;
    }

    public static void setCheckOut(CardView checkOut) {
        CheckOut = checkOut;
    }

    public groceries() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_groceries, container, false);
        recyclerView = myview.findViewById(R.id.recycler_cart);
        if(savedInstanceState == null) {
            connectionHandler.external.groceries_.getAllDocuments(getActivity(), getShopName(), recyclerView);
        }
        CheckOut = myview.findViewById(R.id.Cart);
        Orders = myview.findViewById(R.id.Orders);
        total = myview.findViewById(R.id.cartAmount);
        CheckOut.setOnClickListener(this);
        Orders.setOnClickListener(this);
        checkCartContents(getActivity());
        //Toast.makeText(getActivity(), "Welcome to " + getShopName(), Toast.LENGTH_SHORT).show();
            //recyclerView.setNestedScrollingEnabled(false);
        accessKeys.setShop(getShopName());
        setExitApplication(true);
        MainActivity.setFragmentTag("groceries");
        return myview;
    }
    //check Cart details
    public static void checkCartContents(final Context context){
        Realm.init(context);
        Realm.setDefaultConfiguration(getConfiguration());
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<cartDetails> results = bgRealm.where(cartDetails.class)
                        .contains("userid", accessKeys.getDefaultUserId())
                        .contains("shop", getShopName())
                        //.findAllAsync();
                        .findAll();
                results.load();
                if (results.size()>0){
                    total.setVisibility(View.VISIBLE);
                    CheckOut.setVisibility(View.VISIBLE);
                    double price = 0;
                    for (int i = 0; i < results.size(); i++) {
                        String summary = results.get(i).getItemSummary();
                        String[] getSummary = summary.split(" ");
                        String totalPrice = getSummary[getSummary.length-1].substring(1, getSummary[getSummary.length-1].length());
                        price+= Double.parseDouble(totalPrice.replace(",","."));
                    }
                    total.setText("R"+price);
                }else{
                    total.setVisibility(View.GONE);
                    CheckOut.setVisibility(View.GONE);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Loginfo("customer cart successfully obtained");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logerror("unable to get customer cart, " + error.getMessage());

            }
        });
        realm.close();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.Cart){
            methods.globalMethods.loadFragmentWithTag(R.id.main, new cart(), getActivity(),"cart");
        }else{
            methods.globalMethods.loadFragmentWithTag(R.id.main, new history(), getActivity(),"history");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(total.getText().toString(), "total");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!= null) {
            total.setText(savedInstanceState.getString("total"));
        }
    }
}
