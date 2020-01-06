package com.novv.dzdesk;

import java.io.File;

public class Const {

    public interface OnlineKey {
        int CONTENT_TYPE_AD = 1;
        String ONLINE_PARAMS = "online_params_pre";
    }

    public interface SinaConstants {
        String APP_KEY = "1759061911";
        String APP_SECRET = "13e8cd5c472a4c2fd468c4c599d8810b";
        String REDIRECT_URL = "http://www.adesk.com";
        String SCOPE = "email,direct_messages_read,direct_messages_write,"
                + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                + "follow_app_official_microblog,"
                + "invitation_write";
    }

    public interface AliConstants {

        String APP_KEY = "23747232";
        String APP_SECRET = "f203ca5acf4aaec706d80332311e686b";
    }

    public interface WXConstants {

        String APP_ID = "wxb512d991de42d2af";
        String APP_SECRET = "ebf62d226d8cb9f45b82e3cf0c8a58e5";
    }

    public interface QQConstants {

        String APP_ID = "1106130401";
        String APP_KEY = "tzKBe66AoxUMgcup";
    }

    public static class PARAMS {

        public static final String LiveMp4Key = "live_mp4_key";

        public static boolean DEBUG = BuildConfig.DEBUG;
    }

    public static class Dir {

        public static final String ROOT = VVApplication.getContext().getExternalFilesDir(null)
                .getAbsolutePath();

        public static final String APP = ROOT + File.separator + "avideoshare";

        public static final String SHARE_PATH = APP + File.separator + "share";

        public static final String EDIT_VIDEO_PATH = APP + File.separator + "video_edit";

        public static final String LOCAL_FINISH_VIDEO_PATH =
                EDIT_VIDEO_PATH + File.separator + ".output";

        public static final String AVATAR_DIR = APP + File.separator + "avatars";

        public static final String RINGS_DIR = APP + File.separator + "rings";

        public static final String CACHE = APP + File.separator + ".cache";

        public static final String VIDEO_ROOT_PATH = APP + File.separator + "local_video";

        public static final String THUMB_NAIL_PATH =
                VIDEO_ROOT_PATH + File.separator + "thumbnails";

        public static final String VIDEO_CROP_PATH_UPLOAD = APP + File.separator + "output_crop";

        public static final String VIDEO_CROP_PATH_LOCAL =
                APP + File.separator + "output_crop_local";

        public static final String IMG_CROP_PATH = APP + File.separator + "img_crop";

        public static final String VIDEO_TEMP_PATH = APP + File.separator + "player_cache";

        public static final String APK_DOWNLOAD = APP + File.separator + "apk_download";
    }

    public static class SHARE {

        public static final String IMG = "share.jpg";
        public static final String SharePageURL = "http://service.videowp.adesk.com/web/videowallpaper";
    }

    public static class Policy{
        public static final String VIP_POLICY = "http://s.novapps.com/web_html/coloregg_service_protocol_vip.html";
        public static final String USER_POLICY = "http://s.novapps.com/web_html/coloregg_service_protocol.html";
        public static final String PRIVACY_POLICY = "http://s.novapps.com/web_html/coloregg_new_privacy.html";
        public static final String PRIVACY_UPGRADE_VERSION = "private_policy_update_version";

    }
}
