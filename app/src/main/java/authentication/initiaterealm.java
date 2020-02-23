package authentication;

import android.app.Application;

import constants.constants;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class initiaterealm extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.
                Builder()
                .name(constants.AppName)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    public static RealmConfiguration  getConfiguration(){
        RealmConfiguration configuration = new RealmConfiguration.
                Builder()
                .name(constants.AppName)
                .deleteRealmIfMigrationNeeded()
                .build();
        return configuration;

    }

    public static void DropRealm(RealmConfiguration configuration){
        Realm.deleteRealm(configuration);

    }
}
