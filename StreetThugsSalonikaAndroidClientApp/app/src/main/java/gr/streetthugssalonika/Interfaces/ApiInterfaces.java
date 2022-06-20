package gr.streetthugssalonika.Interfaces;

import java.util.List;

import gr.streetthugssalonika.Models.PolyLine;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterfaces {

    @GET("sts/sts_getby_id_polyline.php/")
    Call<List<PolyLine>> getPolyLines(@Query("id") int polylineId);

    //retro
}
