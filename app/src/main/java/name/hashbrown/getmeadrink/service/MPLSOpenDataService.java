package name.hashbrown.getmeadrink.service;

import name.hashbrown.getmeadrink.model.LicenseResponse;
import retrofit.http.GET;
import rx.Observable;

public interface MPLSOpenDataService {

    @GET("/On-Sale_Liquor_Licenses/FeatureServer/0/query?f=json&where=1%3D1&returnGeometry=false&spatialRel=esriSpatialRelIntersects&maxAllowableOffset=0.29858214164732144&outFields=*&outSR=3857&resultOffset=0&resultRecordCount=2000")
    Observable<LicenseResponse> getOnSaleLiquorLicenses();
}
