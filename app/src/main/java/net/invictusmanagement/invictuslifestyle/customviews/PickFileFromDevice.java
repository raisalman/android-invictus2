package net.invictusmanagement.invictuslifestyle.customviews;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;

import net.invictusmanagement.invictuslifestyle.activities.FavouriteRedeemActivity;
import net.invictusmanagement.invictuslifestyle.activities.GeneralChatActivity;
import net.invictusmanagement.invictuslifestyle.activities.NewMaintenanceRequestActivity;
import net.invictusmanagement.invictuslifestyle.activities.NewServiceKeyActivity;
import net.invictusmanagement.invictuslifestyle.activities.RedeemActivity;
import net.invictusmanagement.invictuslifestyle.activities.SellActivity;
import net.invictusmanagement.invictuslifestyle.activities.ServiceActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PickFileFromDevice {
    private Activity context;
    private String[] permissions;
    private String temp_profile_image = "";
    private View view;
    private RedeemActivity redeemActivity;
    private FavouriteRedeemActivity favouriteRedeemActivity;
    private SellActivity sellActivity;
    private GeneralChatActivity generalChatActivity;
    private ServiceActivity serviceActivity;
    private NewMaintenanceRequestActivity newMaintenanceRequestActivity;
    private NewServiceKeyActivity newServiceKeyActivity;
    private String imageFilePath = "";
    private String videoFilePath = "";
    int position = 0;
    public static int PICK_FILE = 109;
    public static int PICK_IMAGE = 119;
    public static int CAPTURE_IMAGE = 129;


    public PickFileFromDevice(Activity context, GeneralChatActivity generalChatActivity) {
        this.view = view;
        this.context = context;
        this.generalChatActivity = generalChatActivity;
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public PickFileFromDevice(Activity context, RedeemActivity documentVerificationFragment) {
        this.view = view;
        this.context = context;
        this.redeemActivity = documentVerificationFragment;
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public PickFileFromDevice(Activity context, FavouriteRedeemActivity profileFragment) {
        this.view = view;
        this.context = context;
        this.favouriteRedeemActivity = profileFragment;
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public PickFileFromDevice(Activity context, SellActivity editProfileFragment) {
        this.view = view;
        this.context = context;
        this.sellActivity = editProfileFragment;
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }


    public PickFileFromDevice(Activity context, ServiceActivity editProfileFragment) {
        this.view = view;
        this.context = context;
        this.serviceActivity = editProfileFragment;
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public PickFileFromDevice(Activity context, NewMaintenanceRequestActivity newMaintenanceRequestActivity) {
        this.view = view;
        this.context = context;
        this.newMaintenanceRequestActivity = newMaintenanceRequestActivity;
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public PickFileFromDevice(Activity context, NewServiceKeyActivity newServiceKeyActivity) {
        this.view = view;
        this.context = context;
        this.newServiceKeyActivity = newServiceKeyActivity;
        permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }


    public String getImagePath() {
        String imagePath = imageFilePath;
        imageFilePath = "";
        return imagePath;
    }

    public String getVideoPath() {
        String videoPath = videoFilePath;
        videoFilePath = "";
        return videoPath;
    }

    public void showCameraIntent(int requestCode) {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(context.getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, FileUtils.AUTHORITY, photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                if (redeemActivity != null) {
                    redeemActivity.startActivityResult(pictureIntent,
                            requestCode);
                } else if (favouriteRedeemActivity != null) {
                    favouriteRedeemActivity.startActivityResult(pictureIntent,
                            requestCode);
                } else if (sellActivity != null) {
                    sellActivity.startActivityResult(pictureIntent,
                            requestCode);
                } else if (serviceActivity != null) {
                    serviceActivity.startActivityResult(pictureIntent,
                            requestCode);
                } else if (newMaintenanceRequestActivity != null) {
                    newMaintenanceRequestActivity.startActivityResult(pictureIntent,
                            requestCode);
                } else if (newServiceKeyActivity != null) {
                    newServiceKeyActivity.startActivityResult(pictureIntent,
                            requestCode);
                } else if (generalChatActivity != null) {
                    generalChatActivity.startActivityResult(pictureIntent,
                            requestCode);
                }

            }
        }
    }

    public void showVideoIntent(int requestCode) {
        Intent pictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (pictureIntent.resolveActivity(context.getPackageManager()) != null) {
            //Create a file to store the image
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (videoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, FileUtils.AUTHORITY, videoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                if (newMaintenanceRequestActivity != null) {
                    newMaintenanceRequestActivity.startActivityResult(pictureIntent,
                            requestCode);
                } else if (newServiceKeyActivity != null) {
                    newServiceKeyActivity.startActivityResult(pictureIntent,
                            requestCode);
                }

            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

//        New-way to store image in gallery (not secured) ******

        String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Invictus";
        File NewStorageDir = new File(storagePath);
        if (!NewStorageDir.exists()) {
            File wallpaperDirectory = new File(storagePath);
            wallpaperDirectory.mkdirs();
        }


//        *****************

//        old-way to store images in app's package name pictures folder (secured)
//        -------------------
//        File storageDir =
//                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        -------------------

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                NewStorageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private File createVideoFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "VID_" + timeStamp + "_";

//        New-way to store image in gallery (not secured) ******

        String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Invictus";
        File NewStorageDir = new File(storagePath);
        if (!NewStorageDir.exists()) {
            File wallpaperDirectory = new File(storagePath);
            wallpaperDirectory.mkdirs();
        }


//        *****************

//        old-way to store images in app's package name pictures folder (secured)
//        -------------------
//        File storageDir =
//                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        -------------------

        File video = File.createTempFile(
                imageFileName,  /* prefix */
                ".mp4",         /* suffix */
                NewStorageDir      /* directory */
        );

        videoFilePath = video.getAbsolutePath();
        return video;
    }

    public void showFileChooser(int requestCode) {
        String[] mimeTypes = {"image/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        try {
            if (redeemActivity != null) {
                redeemActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            } else if (favouriteRedeemActivity != null) {
                favouriteRedeemActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            } else if (sellActivity != null) {
                sellActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            } else if (serviceActivity != null) {
                serviceActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            } else if (generalChatActivity != null) {
                generalChatActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            }
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Snackbar.make(view, "Please install a File Manager.", Snackbar.LENGTH_LONG).show();
        }

    }

    public void showFileChooserWithVideo(int requestCode) {
        /*String[] mimeTypes = {"image/* video/*"};*/
        String[] mimeTypes = {"image/*", "video/*"};
        //String[] mimeTypes = {"*/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        try {
            if (newMaintenanceRequestActivity != null) {
                newMaintenanceRequestActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            } else if (newServiceKeyActivity != null) {
                newServiceKeyActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            }
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Snackbar.make(view, "Please install a File Manager.", Snackbar.LENGTH_LONG).show();
        }

    }


    public void showImagePicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        try {
            if (redeemActivity != null) {
                redeemActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            } else if (favouriteRedeemActivity != null) {
                favouriteRedeemActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            } else if (sellActivity != null) {
                sellActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            } else if (serviceActivity != null) {
                serviceActivity.startActivityResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        requestCode
                );
            }
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(
                    context, "Please install a File Manager.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
