package name.hashbrown.getmeadrink;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import name.hashbrown.getmeadrink.model.License;
import name.hashbrown.getmeadrink.service.LicenseService;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LicenseService licenseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        licenseService = new LicenseService();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.98, -93.2638),11));
        licenseService.getOnSalesLiquorLicenses()
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<License, MarkerOptions>() {
                    @Override
                    public MarkerOptions call(License license) {
                        float markerColor = license.isFullLicense()?BitmapDescriptorFactory.HUE_GREEN:BitmapDescriptorFactory.HUE_RED;
                        return new MarkerOptions()
                                .position(toLatLong(license.getWgsX(), license.getWgsY()))
                                .title(license.getName())
                                .snippet(license.getAddress())
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor));
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<MarkerOptions>() {
                    @Override
                    public void onNext(final MarkerOptions marker) {
                        mMap.addMarker(marker);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i("Map", "All licenses added to map!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Map", e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private LatLng toLatLong(double wgs_x, double wgs_y){
        if (Math.abs(wgs_x) < 180 && Math.abs(wgs_y) < 90) {
            return new LatLng(0.0, 0.0);
        }

        if ((Math.abs(wgs_x) > 20037508.3427892) || (Math.abs(wgs_y) > 20037508.3427892)) {
            return new LatLng(0.0, 0.0);
        }

        double x = wgs_x;
        double y = wgs_y;
        double num3 = x / 6378137.0;
        double num4 = num3 * 57.295779513082323;
        double num5 = Math.floor((double) ((num4 + 180.0) / 360.0));
        double num6 = num4 - (num5 * 360.0);
        double num7 = 1.5707963267948966 - (2.0 * Math.atan(Math.exp((-1.0 * y) / 6378137.0)));

        double lon = num6;
        double lat = num7 * 57.295779513082323;
        return new LatLng(lat, lon);
    }
}

