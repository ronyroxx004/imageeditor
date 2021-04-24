package com.example.imageeditor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity() {

    var bitmapp: Bitmap? = null
    var resultUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 102)
            }
        }

        

        openphoto.setOnClickListener{
            Log.d("admin.kt","pic button is clicked")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type= "image/=*"
            startActivityForResult(intent,0)
        }

        editphoto.setOnClickListener(){

            CropImage.activity(resultUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

            /* if(photoview.rotationX.equals(0))
             photoview.setRotationX(180F)
             else
                 photoview.setRotationX(0)*/
        }

        exifphoto.setOnClickListener(){

            if(resultUri == null)
            {
                Toast.makeText(this,"Image is not Selected ",LENGTH_SHORT).show()

                return@setOnClickListener
            }


            Log.d("244","$resultUri")

            var uri: Uri? = resultUri // the URI you've received from the other app

            val `in`: InputStream?
            try {
                `in` = uri?.let { it1 -> contentResolver.openInputStream(it1) }
                val exifInterface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ExifInterface(`in`)
                } else {
                    TODO("VERSION.SDK_INT < N")
                }

                // Now you can extract any Exif tag you want
                // Assuming the image is a JPEG or supported raw format
                var DPATH = resultUri
                var ORIENTATION_FLIP_HORIZONTAL = ExifInterface.ORIENTATION_FLIP_HORIZONTAL
                var ORIENTATION_FLIP_VERTICAL = ExifInterface.ORIENTATION_FLIP_VERTICAL
                var ORIENTATION_NORMAL = ExifInterface.ORIENTATION_NORMAL
                var ORIENTATION_ROTATE_180 = ExifInterface.ORIENTATION_ROTATE_180
                var ORIENTATION_ROTATE_270 = ExifInterface.ORIENTATION_ROTATE_270
                var ORIENTATION_ROTATE_90 = ExifInterface.ORIENTATION_ROTATE_90
                var ORIENTATION_TRANSPOSE = ExifInterface.ORIENTATION_TRANSPOSE
                var ORIENTATION_TRANSVERSE =  ExifInterface.ORIENTATION_TRANSVERSE
                var ORIENTATION_UNDEFINED = ExifInterface.ORIENTATION_UNDEFINED
                var TAG_APERTURE = ExifInterface.TAG_APERTURE
                var TAG_APERTURE_VALUE = ExifInterface.TAG_APERTURE_VALUE
                var TAG_BITS_PER_SAMPLE = ExifInterface.TAG_BITS_PER_SAMPLE
                var TAG_BRIGHTNESS_VALUE     = ExifInterface.TAG_BRIGHTNESS_VALUE
                var TAG_CFA_PATTERN = ExifInterface.TAG_CFA_PATTERN
                var TAG_COLOR_SPACE = ExifInterface.TAG_COLOR_SPACE
                var TAG_COMPONENTS_CONFIGURATION =  ExifInterface.TAG_COMPONENTS_CONFIGURATION
                var TAG_COMPRESSED_BITS_PER_PIXEL = ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL
                var TAG_COMPRESSION = ExifInterface.TAG_COMPRESSION
                var TAG_CONTRAST = ExifInterface.TAG_CONTRAST

                exifinfo.setText("Path: $DPATH \n" +
                        "ORIENTATION_FLIP_HORIZONTAL: $ORIENTATION_FLIP_HORIZONTAL \n" +
                        "ORIENTATION_FLIP_VERTICAL: $ORIENTATION_FLIP_VERTICAL \n" +
                        "ORIENTATION_NORMAL: $ORIENTATION_NORMAL \n" +
                        "ORIENTATION_ROTATE_180: $ORIENTATION_ROTATE_180 \n" +
                        "ORIENTATION_ROTATE_270: $ORIENTATION_ROTATE_270 \n" +
                        "ORIENTATION_ROTATE_90: $ORIENTATION_ROTATE_90 \n" +
                        "ORIENTATION_TRANSPOSE: $ORIENTATION_TRANSPOSE \n" +
                        "ORIENTATION_TRANSVERSE: $ORIENTATION_TRANSVERSE \n" +
                        "ORIENTATION_UNDEFINED: $ORIENTATION_UNDEFINED \n" +
                        "TAG_APERTURE: $TAG_APERTURE \n" +
                        "TAG_APERTURE_VALUE: $TAG_APERTURE_VALUE \n" +
                        "TAG_BITS_PER_SAMPLE: $TAG_BITS_PER_SAMPLE \n" +
                        "TAG_BRIGHTNESS_VALUE: $TAG_BRIGHTNESS_VALUE \n" +
                        "TAG_CFA_PATTERN: $TAG_CFA_PATTERN \n" +
                        "TAG_COLOR_SPACE: $TAG_COLOR_SPACE \n" +
                        "TAG_COMPONENTS_CONFIGURATION: $TAG_COMPONENTS_CONFIGURATION \n" +
                        "TAG_COMPRESSED_BITS_PER_PIXEL: $TAG_COMPRESSED_BITS_PER_PIXEL \n" +
                        "TAG_COMPRESSION: $TAG_COMPRESSION \n" +
                        "TAG_CONTRAST: $TAG_CONTRAST \n" );


            } catch (e: IOException) {
                // Handle any errors
            }
        }

        savephoto.setOnClickListener(){


            if(photoview.getDrawable() == null) {

                Toast.makeText(this,"Image is not Selected ",LENGTH_SHORT).show()
                return@setOnClickListener

            }




            val path = Environment.getExternalStorageDirectory().toString()

            // Create a file to save the image
            val file = File(path, "${UUID.randomUUID()}.jpg")

            try {
                // Get the file output stream
                val stream: OutputStream = FileOutputStream(file)

                // Compress the bitmap

                val bitmaph = photoview.getDrawable().toBitmap()



                bitmaph?.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                // Flush the output stream
                stream.flush()

                // Close the output stream
                stream.close()
                Toast.makeText(this,"$file",Toast.LENGTH_SHORT).show()

            } catch (e: IOException){ // Catch the exception
                e.printStackTrace()
                Toast.makeText(this,"Not Saved",Toast.LENGTH_SHORT).show()
            }


        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null ){

            Log.d("admin.kt","photo was selected")

            resultUri = data.data
            Log.d("admin.kt","$resultUri")

            bitmapp = MediaStore.Images.Media.getBitmap(contentResolver,resultUri)
            val bitmapDrawable = BitmapDrawable(bitmapp)
            photoview.setImageDrawable(bitmapDrawable)

        }


        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                resultUri = result.uri
                photoview.setImageURI(null);
                photoview.setImageURI(resultUri);
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

    }
}
