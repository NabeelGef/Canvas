package models;


import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface API {
   @POST ("api/login/")
   public Call<Log_in> getingUsers(@Body LogWithBody log );
   @Multipart
   @POST("api/editProfile")
   public Call<Log_in>editing(@Part("email") String email,@Part("name") String name , @Part("password") String password,
                                    @Part ("address") String address , @Part MultipartBody.Part image , @Part("role") String role);
    @Multipart
    @POST("api/register/")
    public Call<ResponseBody> posting(@Part("name") String name, @Part("email") String email
    , @Part("password") String password , @Part("address") String address , @Part MultipartBody.Part image
    ,@Part("role") String role,@Part("url") String url);
    @POST("api/getUsers/")
    public Call<ArrayList<User>> getAllUsers(@Body ID_USER id_user);
    @POST("api/addmessage")
    public Call<ResponseBody> putMessages(@Body ChatData chatData);
    @POST("api/getMessage")
    public  Call<ArrayList<ChatData>> getAllMessages(@Body ChatData chatData);
}
