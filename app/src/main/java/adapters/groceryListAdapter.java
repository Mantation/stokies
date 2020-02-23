package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;

import accessors.cartDetails;
import accessors.userDetails;
import interfaces.interface_;
import io.eyec.bombo.stokies.R;
import io.realm.Realm;
import io.realm.RealmResults;
import menuFragments.groceries;
import menuFragments.mainScreen;
import methods.globalMethods;
import properties.accessKeys;

import static authentication.initiaterealm.getConfiguration;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.InitializeFirstLetter;
import static properties.accessKeys.setDefaultUserId;

public class groceryListAdapter extends RecyclerView.Adapter<groceryViewHolder> {
    static Realm realm;
    Activity activity;
    private String[] Item;
    private String[] Price;
    private String[] Category;
    private String[] Image;

    public String[] getItem() {
        return Item;
    }

    public void setItem(String[] item) {
        Item = item;
    }

    public String[] getPrice() {
        return Price;
    }

    public void setPrice(String[] price) {
        Price = price;
    }

    public String[] getCategory() {
        return Category;
    }

    public void setCategory(String[] category) {
        Category = category;
    }

    public String[] getImage() {
        return Image;
    }

    public void setImage(String[] image) {
        Image = image;
    }

    public groceryListAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public groceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocerylist, parent, false);
        return new groceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final groceryViewHolder holder, int position) {
        if(!Category[position].equals("zerrrrO")){
            holder.Type.setText(InitializeFirstLetter(Category[position]));
            //holder.Type.getRootView().setVisibility(View.VISIBLE);
            //holder.TypeLine.getRootView().setVisibility(View.VISIBLE);
            holder.Type.setVisibility(View.VISIBLE);
            holder.TypeLine.setVisibility(View.VISIBLE);
        }else{
            //holder.Type.getRootView().setVisibility(View.GONE);
            //holder.TypeLine.getRootView().setVisibility(View.GONE);
            holder.Type.setVisibility(View.INVISIBLE);
            holder.TypeLine.setVisibility(View.INVISIBLE);
        }
        Glide.with(activity).load(Image[position]).into(holder.Image);
        holder.Product.setText(Item[position]);
        holder.Amount.setText("R"+Price[position]);
        holder.Quantity.setText("");
        holder.Quantity.setText("1");
        holder.Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                    EditText quatity = holder.Quantity;
                    if(!quatity.getText().toString().isEmpty()){
                        try{
                            int amount = Integer.parseInt(quatity.getText().toString());
                            if (Integer.parseInt(quatity.getText().toString()) > 0) {
                                setItemsToCart(activity, v, Image[position], Price[position], Item[position], String.valueOf(amount));
                            }

                        }catch (Exception e){
                            quatity.requestFocus();
                        }
                        //Toast.makeText(activity, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                }
        });

    }

    public static void setItemsToCart(final Activity activity,final View view, final String Image, final String Price, final String Item, final String Quantity){
        Realm.init(activity);
        Realm.setDefaultConfiguration(getConfiguration());
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<cartDetails> results = bgRealm.where(cartDetails.class)
                        .contains("shop", accessKeys.getShop())
                        .contains("userid", accessKeys.getDefaultUserId())
                        //.findAllAsync();
                        .findAll();
                results.load();
                //bgRealm.deleteAll();
                //results.deleteFromRealm(0);
                //bgRealm.commitTransaction();
                //format of money value
                final DecimalFormat f = new DecimalFormat("#.00");
                if (results.size()>0) {
                    boolean isFound = false;
                    double price = 0;
                    int rowNum = 0;
                    //get all prices
                    for (int x = 0; x < results.size(); x++) {
                        price+= Double.parseDouble(results.get(x).getPrice());
                    }
                    final double newPrice = price + (Double.parseDouble(Price) * Double.parseDouble(Quantity));
                    //check for similar item
                    for (int i = 0; i < results.size(); i++) {
                        if(Item.equals(results.get(i).getItem())){
                            isFound = true;
                            rowNum = i;
                            break;
                        }
                    }
                    //update Item if found
                    if(isFound) {
                        String summary = results.get(rowNum).getItemSummary();
                        String[] getSummary = summary.split(" ");
                        int Qty = Integer.parseInt(getSummary[0].substring(0, getSummary[0].length() - 1)) + Integer.parseInt(Quantity);
                        String Itm = Item;
                        String totalPrice = getSummary[getSummary.length-1].substring(1, getSummary[getSummary.length-1].length());
                        double Prc = Double.parseDouble(totalPrice.replace(",",".")) + (Double.parseDouble(Price) * Double.parseDouble(Quantity));
                        results.get(rowNum).setItemSummary(Qty + "x " + Itm + " R" + f.format(Prc));
                    }else{
                        cartDetails user = bgRealm.createObject(cartDetails.class);
                        user.setUserid(accessKeys.getDefaultUserId());
                        user.setImage(Image);
                        user.setPrice(Price);
                        user.setItem(Item);
                        user.setShop(accessKeys.getShop());
                        Double prices = Double.parseDouble(Price) * Double.parseDouble(Quantity);
                        user.setItemSummary(Quantity+"x "+Item+" R"+f.format(prices));
                    }
                    //search prices
                    double newprice = 0;
                    for (int i = 0; i < results.size(); i++) {
                        String summary = results.get(i).getItemSummary();
                        String[] getSummary = summary.split(" ");
                        String totalPrice = getSummary[getSummary.length-1].substring(1, getSummary[getSummary.length-1].length());
                        newprice+= Double.parseDouble(totalPrice.replace(",","."));
                    }
                    final double newPrice_ = newprice;
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            groceries.getTotal().setText("R"+f.format(newPrice_));
                        }
                    });

                }else{
                    cartDetails user = bgRealm.createObject(cartDetails.class);
                    user.setUserid(accessKeys.getDefaultUserId());
                    user.setImage(Image);
                    user.setPrice(Price);
                    user.setItem(Item);
                    user.setShop(accessKeys.getShop());
                    Double price = Double.parseDouble(Price) * Double.parseDouble(Quantity);
                    user.setItemSummary(Quantity+"x "+Item+" R"+f.format(price));
                    final double price_ = price;
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            groceries.getTotal().setText("R"+f.format(price_));
                            //groceries.getTotal().setText("R"+f.format(newPrice_));
                        }
                    });
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Loginfo("user Items successfully added to cart");
                if(groceries.getCheckOut().getVisibility() == View.GONE){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            groceries.getCheckOut().setVisibility(View.VISIBLE);
                            groceries.getTotal().setVisibility(View.VISIBLE);
                        }
                    });
                }
                globalMethods.ConfirmResolution(view,Item + " added!");
                //methods.globalMethods.loadFragments(R.id.home, new profile(), activity);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logerror("unable to add user items to cart, " + error.getMessage());
                Toast.makeText(activity, "Unable to add this Item to cart", Toast.LENGTH_SHORT).show();

            }
        });
        realm.close();
    }

    @Override
    public int getItemCount() {
        return Item.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
        //return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }
}

    class groceryViewHolder extends RecyclerView.ViewHolder {
        public View TypeLine;
        public TextView Type;
        public ImageView Image;
        public TextView Product;
        public TextView Amount;
        public CardView Add;
        public EditText Quantity;
        public ConstraintLayout Main;

        public groceryViewHolder(View view) {
            super(view);
            TypeLine =  view.findViewById(R.id.groceryLine);
            Type = view.findViewById(R.id.type);
            Image = view.findViewById(R.id.image);
            Product = view.findViewById(R.id.product);
            Amount = view.findViewById(R.id.price);
            Add = view.findViewById(R.id.Add);
            Quantity = view.findViewById(R.id.quantity);
            Main = view.findViewById(R.id.main);
        }
}
