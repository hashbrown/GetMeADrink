package name.hashbrown.getmeadrink.service;


import name.hashbrown.getmeadrink.model.LicenseResponse;
import name.hashbrown.getmeadrink.model.Feature;
import name.hashbrown.getmeadrink.model.License;
import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;

public class LicenseService {

    private static final String WEB_SERVICE_BASE_URL = "http://services.arcgis.com/afSMGVsC7QlRK1kZ/arcgis/rest/services";

    private MPLSOpenDataService dataService;

    public LicenseService(){

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(WEB_SERVICE_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        dataService = restAdapter.create(MPLSOpenDataService.class);
    }

    public Observable<License> getOnSalesLiquorLicenses(){
        return dataService.getOnSaleLiquorLicenses()
                .flatMap(new Func1<LicenseResponse, Observable<Feature>>() {
                    @Override
                    public Observable<Feature> call(LicenseResponse apiResponse) {
                        return Observable.from(apiResponse.features);
                    }
                })
                .map(new Func1<Feature, License>() {
                    @Override
                    public License call(Feature feature) {
                        return feature.license;
                    }
                });
    }

}
