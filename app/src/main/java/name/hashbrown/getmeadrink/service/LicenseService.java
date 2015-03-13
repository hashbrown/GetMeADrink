package name.hashbrown.getmeadrink.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import name.hashbrown.getmeadrink.model.ApiResponse;
import name.hashbrown.getmeadrink.model.Feature;
import name.hashbrown.getmeadrink.model.License;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.functions.Func1;

public class LicenseService {

    private static final String WEB_SERVICE_BASE_URL = "http://services.arcgis.com/afSMGVsC7QlRK1kZ/arcgis/rest/services";

    private MPLSOpenDataService dataService;

    public LicenseService(){

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(WEB_SERVICE_BASE_URL)
//                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        dataService = restAdapter.create(MPLSOpenDataService.class);
    }

    public Observable<License> getOnSalesLiquorLicenses(){
        return dataService.getOnSaleLiquorLicenses()
                .flatMap(new Func1<ApiResponse, Observable<Feature>>() {
                    @Override
                    public Observable<Feature> call(ApiResponse apiResponse) {
                        ApiResponse resp = apiResponse;
                        return Observable.from(apiResponse.features);
                    }
                })
                .map(new Func1<Feature, License>() {
                    @Override
                    public License call(Feature feature) {
                        Feature f = feature;
                        return feature.license;
                    }
                });
    }


    public static class ItemTypeAdapterFactory implements TypeAdapterFactory {

        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {

            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

            return new TypeAdapter<T>() {

                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                public T read(JsonReader in) throws IOException {

                    JsonElement jsonElement = elementAdapter.read(in);

                    return delegate.fromJsonTree(jsonElement.getAsJsonObject().get("features"));
                }
            }.nullSafe();
        }
    }

}
