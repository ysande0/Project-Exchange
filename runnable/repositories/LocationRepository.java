package com.syncadapters.czar.exchange.repositories;


public class LocationRepository {

    @SuppressWarnings("unused")
    private static final String TAG = "MSG";
    private static LocationRepository location_repository;

    public static LocationRepository  getInstance(){

        if(location_repository == null)
            location_repository = new LocationRepository();

        return location_repository;
    }

// --Commented out by Inspection START (1/9/2021 11:52 PM):
//    public void remote(Context context, String url, JSONObject json_object){
//
//        Volley volley = new Volley(context, Request.Method.POST, url, json_object);
//        volley.set_priority(Request.Priority.NORMAL);
//        volley.Execute(new VolleyCallback() {
//// --Commented out by Inspection START (1/9/2021 11:52 PM):
////            @Override
////            public void network_response(JSONObject json_response) {
////
////                if(json_response.has("location_error_2")){
////
////                    try{
////
////                        if(json_response.getBoolean("location_error_2")){
////
////                            Log.d(TAG, "Location error occurred");
////
////                        }
////                        else if(!json_response.getBoolean("location_error_2")){
////
////                            Log.d(TAG, "Location updated");
////                        }
////
////                    }catch(JSONException json_error){
////
////                        json_error.printStackTrace();
////
////                    }
////
////                }
////
////            }
//// --Commented out by Inspection STOP (1/9/2021 11:52 PM)
// --Commented out by Inspection STOP (1/9/2021 11:52 PM)

// --Commented out by Inspection START (1/9/2021 11:52 PM):
//            @Override
//            public void network_error(VolleyError error) {
//                error.printStackTrace();
//            }
// --Commented out by Inspection STOP (1/9/2021 11:52 PM)
//        });


//    }


}
