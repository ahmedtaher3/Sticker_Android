package com.sticker_android.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by user on 10/4/18.
 */
public class ImageFileFilter implements FileFilter
{
    File file;
    private final String[] okFileExtensions =  new String[] {"gif","png"};

    /**
     *
     */
    public ImageFileFilter()
    {

    }

    public boolean accept(File file)
    {
        AppLogger.debug("Image filter","filter image");
        for (String extension : okFileExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                AppLogger.debug("Image filter","filter image");
                return true;

            }
        }
        return false;
    }

}