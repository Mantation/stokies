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

import accessors.cartDetails;
import io.eyec.bombo.stokies.R;
import io.realm.Realm;
import io.realm.RealmResults;
import menuFragments.cart;
import properties.accessKeys;

import static authentication.initiaterealm.getConfiguration;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.InitializeFirstLetter;

public class CartAdapter extends RecyclerView.Adapter<cartViewHolder> {
    static Realm realm;
    Activity activity;
    private String[] Summary;
    private String[] Image;
    private String[] Item;

    public String[] getSummary() {
        return Summary;
    }

    public void setSummary(String[] summary) {
        Summary = summary;
    }

    public String[] getImage() {
        return Image;
    }

    public void setImage(String[] image) {
        Image = image;
    }

    public String[] getItem() {
        return Item;
    }

    public void setItem(String[] item) {
        Item = item;
    }

    public CartAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartlist, parent, false);
        return new cartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cartViewHolder holder, int position) {
        Glide.with(activity).load(Image[position]).into(holder.Image);
        holder.Summary.setText(Summary[position]);
        holder.Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                    //Toast.makeText(activity, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    removeItem(activity,position);

            }
        });

    }

    //check Cart details
    public static void removeItem(final Context context, final int position){
        Realm.init(context);
        Realm.setDefaultConfiguration(getConfiguration());
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<cartDetails> results = bgRealm.where(cartDetails.class)
                        .contains("userid", accessKeys.getDefaultUserId())
                        .contains("userid", accessKeys.getDefaultUserId())
                        //.findAllAsync();
                        .findAll();
                results.load();
                results.deleteFromRealm(position);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Loginfo("item successfully removed from cart");
                methods.globalMethods.loadFragmentWithTag(R.id.main, new cart(), context,"cart");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logerror("unable remove item from cart, " + error.getMessage());

            }
        });
        realm.close();
    }


    @Override
    public int getItemCount() {
        return Image.length;
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

class cartViewHolder extends RecyclerView.ViewHolder {
    public ImageView Image;
    public TextView Summary;
    public CardView Remove;
    public ConstraintLayout Main;

    public cartViewHolder(View view) {
        super(view);
        Image = view.findViewById(R.id.image);
        Summary = view.findViewById(R.id.summary);
        Remove = view.findViewById(R.id.Remove);
        Main = view.findViewById(R.id.cartmain);
    }
}
