package connectionHandler.internal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import accessors.cartDetails;
import adapters.CartAdapter;
import adapters.groceryListAdapter;
import io.eyec.bombo.stokies.R;
import io.realm.Realm;
import io.realm.RealmResults;
import menuFragments.cart;
import menuFragments.groceries;
import properties.accessKeys;

import static authentication.initiaterealm.getConfiguration;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.clearList;

public class cart_ extends Application {
    public static List<String> summary = new ArrayList<String>();
    public static List<String> image = new ArrayList<String>();
    public static List<String> item = new ArrayList<String>();
    static CartAdapter cartAdapter;
    static int totalCount;
    //check Cart details
    public static void getCartItems(final Activity activity,Realm realm, final androidx.recyclerview.widget.RecyclerView recyclerView){
        clearList(image);
        clearList(summary);
        clearList(item);
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
                double price = 0;
                if(results.size() == 0){
                    methods.globalMethods.loadFragments(R.id.main, new groceries(), activity);
                }else {
                    for (int i = 0; i < results.size(); i++) {
                        //results.deleteFromRealm(i);
                        summary.add(results.get(i).getItemSummary());
                        image.add(results.get(i).getImage());
                        item.add(results.get(i).getItem());
                        String[] getSummary = results.get(i).getItemSummary().split(" ");
                        String totalPrice = getSummary[getSummary.length - 1].substring(1, getSummary[getSummary.length - 1].length());
                        String Quantity = getSummary[0].substring(0, getSummary[0].length() - 1);
                        price += Double.parseDouble(totalPrice.replace(",", "."));
                    }
                    //format of money value
                    final DecimalFormat f = new DecimalFormat("#.00");
                    final double Total = price;
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            cart.getAmount().setText("R" + f.format(Total));
                        }
                    });
                    final String[] MySummary = new String[summary.size()];
                    final String[] MyImage = new String[image.size()];
                    final String[] MyItem = new String[item.size()];
                    for (int i = 0; i < summary.size(); i++) {
                        MySummary[i] = summary.get(i);
                        MyImage[i] = image.get(i);
                        MyItem[i] = item.get(i);
                    }
                    cartAdapter = new CartAdapter(activity);
                    cartAdapter.setImage(MyImage);
                    cartAdapter.setSummary(MySummary);
                    cartAdapter.setItem(MyItem);
                    recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                    recyclerView.setAdapter(cartAdapter);
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Loginfo("cart information successfully obtained");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logerror("unable to get customer cart information, " + error.getMessage());

            }
        });
        realm.close();
    }
}
