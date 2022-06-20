package gr.streetthugssalonika.Models;

public class PolyLineModel {

    private int id;
    private String polyline;

    public PolyLineModel(int id, String polyline) {
        this.id = id;
        this.polyline = polyline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }
}
