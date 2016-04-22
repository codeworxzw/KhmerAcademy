package org.khmeracademy.NetworkRequest;

/**
 * Created by PC1 on 1/18/2016.
 */
public class API {
    public static final String BASE_URL = "http://1.246.219.166:8080/KAAPI";
    // API URL
    public static final String logInUrl = BASE_URL + "/api/authentication/mobilelogin";
    public static final String fbLoginUrl = BASE_URL + "/api/authentication/login_with_fb";
    public static final String signUpUrl = BASE_URL + "/api/user";
    public static final String mainCategoryUrl = BASE_URL + "/api/elearning/maincategory/listmaincategory";
    public static final String profileDetail = BASE_URL + "/api/user/";
    public static final String editProfile = BASE_URL + "/api/user/";
    public static final String updatePassword = BASE_URL + "/api/user/changepassword";
    public static final String listVideoByPlayListIdUrl = BASE_URL + "/api/elearning/playlist/listAllVideoInPlaylist/";
    public static final String subCategoryById = BASE_URL + "/api/elearning/playlist/listplaylistbymaincategory/";
    public static final String searchSubcategory = BASE_URL + "/api/elearning/playlist/searchplaylist/";
    public static final String listCommentByVideoId = BASE_URL + "/api/elearning/comment/reply/video/v/";
    public static final String listReplyCommentByCommentId = BASE_URL + "/api/elearning/comment/reply/list/";
    public static final String addComment = BASE_URL + "/api/elearning/comment/returnid";
    public static final String addReplyComment = BASE_URL + "/api/elearning/comment/reply/returnid";
    public static final String checkLikeVideo = BASE_URL + "/api/elearning/vote";
    public static final String deleteComment = BASE_URL + "/api/elearning/comment/";
    public static final String updateComment = BASE_URL + "/api/elearning/comment/";
    public static final String deleteReplyComment = BASE_URL + "/api/elearning/comment/";
    public static final String updateReplyComment = BASE_URL + "/api/elearning/comment/";
    public static final String listUniversity_Department = BASE_URL + "/api/user/listuniversity_department?page=1&item=1000";

    // Header
    public static final String API_KEY = "Authorization";
    public static final String API_CODE = "Basic S0FBUEkhQCMkOiFAIyRLQUFQSQ==";
}
