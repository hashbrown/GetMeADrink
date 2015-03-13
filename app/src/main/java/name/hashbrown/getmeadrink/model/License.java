package name.hashbrown.getmeadrink.model;

import com.google.gson.annotations.SerializedName;

public class License {

    @SerializedName("ESTABLIS_1")
    private String name;

    @SerializedName("ESTABLIS_2")
    private String address;

    private String decision;

    private String type;

    @SerializedName("TYPE_DESCR")
    private String description;

    @SerializedName("WGS_X")
    private String wgsX;

    @SerializedName("WGS_Y")
    private String wgsY;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDecision() {
        return decision;
    }

   public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Double getWgsX() {
        return Double.valueOf(wgsX);
    }

    public Double getWgsY() {
        return Double.valueOf(wgsY);
    }

    public boolean isFullLicense(){
        return this.description != null && this.description.matches(".*[E|SS] SERIES.*");
    }


}
