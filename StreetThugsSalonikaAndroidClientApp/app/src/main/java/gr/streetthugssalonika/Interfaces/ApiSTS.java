package gr.streetthugssalonika.Interfaces;

import java.util.List;

import gr.streetthugssalonika.Models.LocationModel;
import gr.streetthugssalonika.Models.PolyLineModel;
import gr.streetthugssalonika.Models.UserModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiSTS {

    @GET("sts/sts_getby_id_polyline.php/")
    Call<List<PolyLineModel>> getPolyLineById(@Query("id") int polylineId);


    @GET("sts/sts_insert_latlong_into_location.php/")
    Call<List<LocationModel>> sendCurrentLocationById(@Query("id") int userId,
                                                      @Query("longitude") String longitudeOf,
                                                      @Query("latitude") String latitudeOf);
    @GET("sts/sts_getby_id_users.php/")
    Call<List<UserModel>> getUserById(@Query("id") int userId);
}
