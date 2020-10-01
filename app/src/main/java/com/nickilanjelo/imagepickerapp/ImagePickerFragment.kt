package com.nickilanjelo.imagepickerapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.nickilanjelo.imagepickerapp.adapters.PickedImageAdapter
import kotlinx.android.synthetic.main.fragment_image_picker.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ImagePickerFragment : Fragment() {

    lateinit var currentPhotoPath: String
    private val imgAdapter by lazy {
        PickedImageAdapter()
    }

    companion object {
        const val PICK_FROM_GALLERY = 0
        const val PICK_FROM_CAMERA = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addImageBtn.setOnClickListener {
            openImagePickerDialog()
        }

        srlImages.setColorSchemeResources(R.color.colorAccent)
        srlImages.setOnRefreshListener {
            imgAdapter.clearItems()
            srlImages.isRefreshing = false
        }

        imgRecycler.layoutManager = GridLayoutManager(context, 5)
        imgRecycler.adapter = imgAdapter
    }

    override fun onDestroyView() {
        srlImages.setOnRefreshListener(null)
        super.onDestroyView()
    }

    private fun openImagePickerDialog() {
        val builder = AlertDialog.Builder(context)
        val pickerView = LayoutInflater.from(context).inflate(R.layout.image_picker_dialog, null)
        val galleryBtn = pickerView.findViewById<LinearLayout>(R.id.llGallery)
        val cameraBtn = pickerView.findViewById<LinearLayout>(R.id.llCamera)
        builder.setView(pickerView)

        val dialog = builder.create()

        galleryBtn.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }
        cameraBtn.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = ArrayList<Uri>()
            if (requestCode == PICK_FROM_GALLERY) {
                data?.let { intent ->
                    val imageUriList = intent.clipData

                    if (imageUriList != null) {
                        val size = imageUriList.itemCount
                        for (i in 0 until size) {
                            imageUriList.getItemAt(i)?.uri?.let {
                                result.add(it)
                            }
                        }
                    } else {
                        intent.data?.let {
                            result.add(it)
                        }
                    }
                }
            }
            if (requestCode == PICK_FROM_CAMERA) {
                val uri = Uri.parse(currentPhotoPath)
                result.add(uri)
            }
            imgAdapter.addItems(result)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_FROM_GALLERY)
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            context?.apply {
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also { file ->
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            file
                        )

                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                            val resInfoList = packageManager.queryIntentActivities(
                                takePictureIntent,
                                PackageManager.MATCH_DEFAULT_ONLY
                            )
                            for (resolveInfo in resInfoList) {
                                val packageName = resolveInfo.activityInfo.packageName
                                grantUriPermission(
                                    packageName,
                                    photoURI,
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )
                            }
                        }

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, PICK_FROM_CAMERA)
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}