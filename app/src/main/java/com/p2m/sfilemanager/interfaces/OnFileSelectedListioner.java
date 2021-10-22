package com.p2m.sfilemanager.interfaces;

import java.io.File;

public interface OnFileSelectedListioner {
    void onFileClicked(File file);
    void onFileLongClicked(File file, int position);
}
