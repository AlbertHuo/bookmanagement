package com.practice.albert.bookmanagement;

/**
 * Created by DELL on 2018/11/01.
 */

public final class ViewBook {

    
    public static final class ViewBookInfo {
    /**
     * Get Book info from scan or storage, via the different intent show UI
     */
        public static final String SCAN_BOOKINFO_INTENT = "android.intent.action.scan.bookinfo";
        public static final String VIEW_BOOKINFO_INTENT = "android.intent.action.view.bookinfo";

        private ViewBookInfo() {
        }
    }
}
