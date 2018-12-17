package com.zeeroapps.wssp.utils;

import android.content.SharedPreferences;

import com.zeeroapps.wssp.BuildConfig;

/**
 * Created by Zeero on 10/27/2016.
 */
public class Constants {

    public static String END_POINT = "http://103.240.220.52/local_goverment/";

    public static final String HOST_URL = "http://103.240.220.52/local_goverment";
    public static final String URL_LOGIN = END_POINT + "index.php/main/login_validations";
    public static final String URL_NEW_COMP = END_POINT + "main/add_comp/add";
    public static final String URL_MY_COMPLAINTS = END_POINT + "main/add_comp/list";
    public static final String URL_MEMBERS = END_POINT + "main/members_app";
    public static final String URL_PROFILE_PIC = END_POINT + "uploads/profile/";
    public static final String URL_SEND_FEEDBACK = END_POINT + "main/add_comp/feedback";
    public static final String All_Complaints = END_POINT +"Admin/all_complaints_list/";
    public static final String Admin_Home =  END_POINT +"Admin/all_complaints?";
    public static final String Pending_Complaints = END_POINT +"Admin/all_complaints_pending_list";
    public static final String Inprogress_Complaints =  END_POINT +"Admin/all_complaints_inprogress_list";
    public static final String Completed_Complaints = END_POINT +"Admin/all_complaints_completed_list";
    public static final String OverDue_Complaints = END_POINT +"admin/over_due_complaints";
    public static final String Update_Complaints = END_POINT +"Admin/update_copmplaint";
    public static final String SignUp = END_POINT +"user/user_register";

}
