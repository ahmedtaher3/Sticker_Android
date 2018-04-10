package com.sticker_android.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by user on 10/4/18.
 */
public class ImageFileFilter implements FileFilter
{
    File file;
    private final String[] okFileExtensions =  new String[] {"gif"};

    /**
     *
     */
    public ImageFileFilter(File newfile)
    {
        this.file=newfile;
    }

    public boolean accept(File file)
    {
        for (String extension : okFileExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                return true;
            }
        }
        return false;
    }

}