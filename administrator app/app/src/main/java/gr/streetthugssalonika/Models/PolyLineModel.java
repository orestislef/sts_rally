
package gr.streetthugssalonika.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PolyLineModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("polyline")
    @Expose
    private String polyline;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PolyLineModel() {
    }

    /**
     * 
     * @param id
     * @param polyline
     */
    public PolyLineModel(String id, String polyline) {
        super();
        this.id = id;
        this.polyline = polyline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PolyLineModel.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("polyline");
        sb.append('=');
        sb.append(((this.polyline == null)?"<null>":this.polyline));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
