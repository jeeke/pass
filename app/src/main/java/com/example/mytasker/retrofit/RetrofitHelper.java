package com.example.mytasker.retrofit;

public class RetrofitHelper {

//    private static void callRetrofit(Context context,String mesSuccess, String mesUnsuccess){
//        ProgressDialog dlg = new ProgressDialog(context);
//        dlg.setTitle("Creating your profile...");
//        dlg.show();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Contracts.BASE_POST_URL)
//                .addConverterFactory(new NullOnEmptyConverterFactory())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        User user;
//        if(FROM){
//            user = new User(firstName.getText().toString(),
//                    null, "9453449939",
//                    "gsdklghsdfkghsdkhfg", null, null,0);
//        }else
//            user = new User(null,
//                    firstName.getText().toString(), "9453449939",
//                    "gsdklghsdfkghsdkhfg", null, null,0);
//
//        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
//        Call<User> call = jsonPlaceHolder.updateUser(user);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                dlg.dismiss();
//                if (!response.isSuccessful()) {
//                    Toast.makeText(context, mesUnsuccess, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                finish();
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText(context, mesUnsuccess, Toast.LENGTH_SHORT).show();
//                dlg.dismiss();
//            }
//        });
//    }
}
