package com.novv.dzdesk.res.model;

import java.util.List;

public class AEData {
    public List<TextBean> textList;
    public List<ImageBean> imageList;

    public static class TextBean {
        public String id;
        public List<String> text;
    }

    public static class ImageBean {
        public String id;
        public String path;
    }
}
