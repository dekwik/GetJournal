package com.example.getjournal;

public class Constant {
    public static final String URL="https://getjournal.pemirahimanikaunud.web.id/";
//    public static final String URL="http://192.168.100.11/";
    public static final String HOME=URL+"api";
    public static final String LOGIN=HOME+"/login";
    public static final String REGISTER=HOME+"/register";
    public static final String SAVE_USER_INFO=HOME+"/save_user_info";
    public static final String GET_USER = HOME+"/userRead";
    public static final String SET_USER = HOME+"/userEdit";
    public static final String UPDATE_PASSWORD = HOME+"/userEditPass";

    public static final String POSTS_CREATE = HOME+"/posts/create";
    public static final String POSTS = HOME+"/posts";
    public static final String POSTS_UPDATE = HOME+"/posts/update";
    public static final String POSTS_DELETE = HOME+"/posts/delete";

    public static final String DOWNLOAD = URL+"uploads/";
}
